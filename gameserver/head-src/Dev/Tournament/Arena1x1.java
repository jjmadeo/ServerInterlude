package Dev.Tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Effect;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfrozen.gameserver.model.base.ClassId;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.util.Broadcast;
import com.l2jfrozen.util.random.Rnd;

import Dev.Tournament.properties.ArenaConfig;
import Dev.Tournament.properties.ArenaTask;

public class Arena1x1 implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(Config.class);

	// list of participants
	public static List<Pair> registered;
	// number of Arenas
	int free = ArenaConfig.ARENA_EVENT_COUNT_1X1;
	// Arenas
	Arena[] arenas = new Arena[ArenaConfig.ARENA_EVENT_COUNT_1X1];
	// list of fights going on
	Map<Integer, String> fights = new HashMap<>(ArenaConfig.ARENA_EVENT_COUNT_1X1);
	
	public Arena1x1()
	{
		registered = new ArrayList<>();
		int[] coord;
		for (int i = 0; i < ArenaConfig.ARENA_EVENT_COUNT_1X1; i++)
		{
			coord = ArenaConfig.ARENA_EVENT_LOCS_1X1[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}
	
	public static Arena1x1 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public boolean register(L2PcInstance player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player)
			{
				player.sendMessage("Tournament: You already registered!");
				return false;
			}
		}
		return registered.add(new Pair(player));
	}
	
	public boolean isRegistered(L2PcInstance player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player)
			{
				return true;
			}
		}
		return false;
	}
	
	public Map<Integer, String> getFights()
	{
		return fights;
	}
	
	public boolean remove(L2PcInstance player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player)
			{
				p.removeMessage();
				registered.remove(p);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized void run()
	{
		boolean load = true;
		
		// while server is running
		while (load)
		{
			if (!ArenaTask.is_started())
				load = false;
			
			// if no have participants or arenas are busy wait 1 minute
			if (registered.size() < 2 || free == 0)
			{
				try
				{
					Thread.sleep(ArenaConfig.ARENA_CALL_INTERVAL * 1000);
				}
				catch (InterruptedException e)
				{
				}
				continue;
			}
			List<Pair> opponents = selectOpponents();
			if (opponents != null && opponents.size() == 2)
			{
				Thread T = new Thread(new EvtArenaTask(opponents));
				T.setDaemon(true);
				T.start();
			}
			// wait 1 minute for not stress server
			try
			{
				Thread.sleep(ArenaConfig.ARENA_CALL_INTERVAL * 1000);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	@SuppressWarnings("null")
	private List<Pair> selectOpponents()
	{
		List<Pair> opponents = new ArrayList<>();
		Pair pairOne = null, pairTwo = null;
		int tries = 3;
		do
		{
			int first = 0, second = 0;
			if (getRegisteredCount() < 2)
				return opponents;
			
			if (pairOne == null)
			{
				first = Rnd.get(getRegisteredCount());
				pairOne = registered.get(first);
				if (pairOne.check())
				{
					opponents.add(0, pairOne);
					registered.remove(first);
				}
				else
				{
					pairOne = null;
					registered.remove(first);
					return null;
				}
				
			}
			if (pairTwo == null)
			{
				second = Rnd.get(getRegisteredCount());
				pairTwo = registered.get(second);
				if (pairTwo.check())
				{
					opponents.add(1, pairTwo);
					registered.remove(second);
				}
				else
				{
					pairTwo = null;
					registered.remove(second);
					return null;
				}
				
			}
		}
		while ((pairOne == null || pairTwo == null) && --tries > 0);
		return opponents;
	}
	
	public void clear()
	{
		registered.clear();
	}
	
	public static int getRegisteredCount()
	{
		return registered.size();
	}
	
	private class Pair
	{
		private L2PcInstance leader;
		
		public Pair(L2PcInstance leader)
		{
			this.leader = leader;
		}
		
		public L2PcInstance getLeader()
		{
			return leader;
		}
		
		public boolean check()
		{
			if ((!(leader == null || leader.isOnline() == 1)))
				return false;
			
			return true;
		}
		
		public boolean isDead()
		{
			if (ArenaConfig.ARENA_PROTECT)
			{
				if (leader != null && leader.isOnline() == 1 && leader.isArenaAttack() && !leader.isDead() && !leader.isInsideZone(L2Character.ARENA_EVENT))
					leader.logout();
			}
			
			if ((leader == null || leader.isDead() || !(leader.isOnline() == 1) || !leader.isInsideZone(L2Character.ARENA_EVENT) || !leader.isArenaAttack()))
				return false;
			
			return !(leader.isDead());
		}
		
		public boolean isAlive()
		{
			if ((leader == null || leader.isDead() || !(leader.isOnline() == 1) || !leader.isInsideZone(L2Character.ARENA_EVENT) || !leader.isArenaAttack()))
				return false;
			
			return !(leader.isDead());
		}
		
		public void teleportTo(int x, int y, int z)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				//leader.getAppearance().setInvisible();
				leader.setCurrentCp(leader.getMaxCp());
				leader.setCurrentHp(leader.getMaxHp());
				leader.setCurrentMp(leader.getMaxMp());
				
				/*
				if (leader.isInObserverMode())
				{
					leader.setLastCords(x, y, z);
					leader.leaveOlympiadObserverMode();
				}
				 */
				if (!leader.isInJail())
					leader.teleToLocation(x, y, z);
				
				leader.broadcastUserInfo();
			}
		}
		
		public void EventTitle(String title, String color)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setTitle(title);
				leader.getAppearance().setTitleColor(Integer.decode("0x" + color));
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}
		}
		
		public void saveTitle()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader._originalTitleColorTournament = leader.getAppearance().getTitleColor();
				leader._originalTitleTournament = leader.getTitle();
			}
		}
		
		public void backTitle()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setTitle(leader._originalTitleTournament);
				leader.getAppearance().setTitleColor(leader._originalTitleColorTournament);
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}
		}
		
		public void setArenaInstance() 
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInstanceId(1); //1x1 Tournament Instance
		}
		
		public void setRealInstance() 
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInstanceId(0);
		}
		
		public void rewards()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				if (leader.isDonator())
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_1X1 * ArenaConfig.ARENA_VIP_DROP_RATE, leader, true);
				else
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_1X1, leader, true);
			}
			
			sendPacket("Congratulations, your team won the event!", 5);
		}
		
		public void rewardsLost()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				if (leader.isDonator())
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_1X1 * ArenaConfig.ARENA_VIP_DROP_RATE, leader, true);
				else
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_1X1, leader, true);
			}
			
			sendPacket("your team lost the event! =(", 5);
		}
		
		public void setInTournamentEvent(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInArenaEvent(val);
		}
		
		public void removeMessage()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena1x1(false);
			}
		}
		
		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setArenaProtection(val);
				leader.setArena1x1(val);
			}
		}
		
		public void revive()
		{
			if (leader != null && leader.isOnline() == 1 && leader.isDead())
				leader.doRevive();
		}
		
		public void setImobilised(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setIsInvul(val);
				leader.setStopArena(val);
			}
		}
		
		public void setArenaAttack(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setArenaAttack(val);
				leader.broadcastUserInfo();
			}
		}
		
		public void removePet()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				// Remove Summon's buffs
				if (leader.getPet() != null)
				{
					L2Summon summon = leader.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof L2PetInstance)
						summon.unSummon(leader);
				}
				
				if (leader.getMountType() == 1 || leader.getMountType() == 2)
					leader.dismount();
			}
		}
		
		public void removeSkills()
		{
			if (!(leader.getClassId() == ClassId.shillenElder || leader.getClassId() == ClassId.shillienSaint || leader.getClassId() == ClassId.bishop || leader.getClassId() == ClassId.cardinal || leader.getClassId() == ClassId.elder || leader.getClassId() == ClassId.evaSaint))
			{
				for (L2Effect effect : leader.getAllEffects())
				{
					if (ArenaConfig.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						leader.stopSkillEffects(effect.getSkill().getId());
				}
			}
		}
		
		public void sendPacket(String message, int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000));
		}
		
		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				ThreadPoolManager.getInstance().scheduleGeneral(new countdown(leader, duration), 0);
		}
		
		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
		}
	}
	
	private class EvtArenaTask implements Runnable
	{
		private final Pair pairOne;
		private final Pair pairTwo;
		private final int pOneX, pOneY, pOneZ, pTwoX, pTwoY, pTwoZ;
		private Arena arena;
		
		public EvtArenaTask(List<Pair> opponents)
		{
			pairOne = opponents.get(0);
			pairTwo = opponents.get(1);
			L2PcInstance leader = pairOne.getLeader();
			pOneX = leader.getX();
			pOneY = leader.getY();
			pOneZ = leader.getZ();
			leader = pairTwo.getLeader();
			pTwoX = leader.getX();
			pTwoY = leader.getY();
			pTwoZ = leader.getZ();
		}
		
		@Override
		public void run()
		{
			free--;
			pairOne.saveTitle();
			pairTwo.saveTitle();
			portPairsToArena();
			pairOne.inicarContagem(ArenaConfig.ARENA_WAIT_INTERVAL_1X1);
			pairTwo.inicarContagem(ArenaConfig.ARENA_WAIT_INTERVAL_1X1);
			try
			{
				Thread.sleep(ArenaConfig.ARENA_WAIT_INTERVAL_1X1 * 1000);
			}
			catch (InterruptedException e1)
			{
			}
			pairOne.sendPacketinit("Started. Good Fight!", 3);
			pairTwo.sendPacketinit("Started. Good Fight!", 3);
			pairOne.EventTitle(ArenaConfig.MSG_TEAM1, ArenaConfig.TITLE_COLOR_TEAM1);
			pairTwo.EventTitle(ArenaConfig.MSG_TEAM2, ArenaConfig.TITLE_COLOR_TEAM2);
			pairOne.setImobilised(false);
			pairTwo.setImobilised(false);
			pairOne.setArenaAttack(true);
			pairTwo.setArenaAttack(true);
			
			while (check())
			{
				// check players status each seconds
				try
				{
					
					Thread.sleep(ArenaConfig.ARENA_CHECK_INTERVAL);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
			finishDuel();
			free++;
		}
		
		private void finishDuel()
		{
			fights.remove(arena.id);
			rewardWinner();
			pairOne.revive();
			pairTwo.revive();
			pairOne.teleportTo(pOneX, pOneY, pOneZ);
			pairTwo.teleportTo(pTwoX, pTwoY, pTwoZ);
			pairOne.backTitle();
			pairTwo.backTitle();
			pairOne.setRealInstance();
			pairTwo.setRealInstance();
			pairOne.setInTournamentEvent(false);
			pairTwo.setInTournamentEvent(false);
			pairOne.setArenaProtection(false);
			pairTwo.setArenaProtection(false);
			pairOne.setArenaAttack(false);
			pairTwo.setArenaAttack(false);
			arena.setFree(true);
		}
		
		private void rewardWinner()
		{
			L2PcInstance leader1 = pairOne.getLeader();
			L2PcInstance leader2 = pairTwo.getLeader();
			
			if (pairOne.isAlive() && !pairTwo.isAlive())
			{
				if (ArenaConfig.TOURNAMENT_EVENT_ANNOUNCE)
					Broadcast.gameAnnounceToOnlinePlayers("(1X1): (" + leader1.getName() + " VS " + leader2.getName() + ") ~> " + leader1.getName() + " win!");
				
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			
			if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				if (ArenaConfig.TOURNAMENT_EVENT_ANNOUNCE)
					Broadcast.gameAnnounceToOnlinePlayers("(1X1): (" + leader1.getName() + " VS " + leader2.getName() + ") ~> " + leader2.getName() + " win!");
				
				pairTwo.rewards();
				pairOne.rewardsLost();
			}
		}
		
		private boolean check()
		{
			return (pairOne.isDead() && pairTwo.isDead());
		}
		
		private void portPairsToArena()
		{
			for (Arena arena : arenas)
			{
				if (arena.isFree)
				{
					this.arena = arena;
					arena.setFree(false);
					pairOne.removePet();
					pairTwo.removePet();
					pairOne.setArenaInstance();
					pairTwo.setArenaInstance();
					pairOne.teleportTo(arena.x - 850, arena.y, arena.z);
					pairTwo.teleportTo(arena.x + 850, arena.y, arena.z);
					pairOne.setImobilised(true);
					pairTwo.setImobilised(true);
					pairOne.setInTournamentEvent(true);
					pairTwo.setInTournamentEvent(true);
					pairOne.removeSkills();
					pairTwo.removeSkills();
					fights.put(this.arena.id, pairOne.getLeader().getName() + " vs " + pairTwo.getLeader().getName());
					break;
				}
			}
		}
	}
	
	private class Arena
	{
		protected int x, y, z;
		protected boolean isFree = true;
		int id;
		
		public Arena(int id, int x, int y, int z)
		{
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public void setFree(boolean val)
		{
			isFree = val;
		}
	}
	
	protected class countdown implements Runnable
	{
		private final L2PcInstance _player;
		private int _time;
		
		public countdown(L2PcInstance player, int time)
		{
			_time = time;
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isOnline() == 1)
			{
				
				switch (_time)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 57:
						if (_player.isOnline() == 1)
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 60 second(s)..", 4000));
							_player.sendMessage("60 second(s) to start the battle.");
						}
						break;
					case 45:
						if (_player.isOnline() == 1)
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 27:
						if (_player.isOnline() == 1)
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 30 second(s)..", 4000));
							_player.sendMessage("30 second(s) to start the battle.");
						}
						break;
					case 20:
						if (_player.isOnline() == 1)
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 15:
						if (_player.isOnline() == 1)
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 10:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 5:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 4:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 3:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 2:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 1:
						if (_player.isOnline() == 1)
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
				}
				if (_time > 1)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new countdown(_player, _time - 1), 1000);
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final Arena1x1 INSTANCE = new Arena1x1();
	}
}