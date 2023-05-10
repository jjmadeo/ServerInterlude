package Dev.Tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class Arena3x3 implements Runnable
{
	// list of participants
	public static List<Pair> registered;
	// number of Arenas
	int free = ArenaConfig.ARENA_EVENT_COUNT_3X3;
	// Arenas
	Arena[] arenas = new Arena[ArenaConfig.ARENA_EVENT_COUNT_3X3];
	// list of fights going on
	Map<Integer, String> fights = new HashMap<>(ArenaConfig.ARENA_EVENT_COUNT_3X3);
	
	public Arena3x3()
	{
		registered = new ArrayList<>();
		int[] coord;
		for (int i = 0; i < ArenaConfig.ARENA_EVENT_COUNT_3X3; i++)
		{
			coord = ArenaConfig.ARENA_EVENT_LOCS_3X3[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}
	
	public static Arena3x3 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public boolean register(L2PcInstance player, L2PcInstance assist, L2PcInstance assist2)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player)
			{
				player.sendMessage("Tournament: You already registered!");
				return false;
			}
			else if (p.getLeader() == assist || p.getAssist() == assist)
			{
				player.sendMessage("Tournament: " + assist.getName() + " already registered!");
				return false;
			}
			else if (p.getLeader() == assist2 || p.getAssist2() == assist2)
			{
				player.sendMessage("Tournament: " + assist2.getName() + " already registered!");
				return false;
			}
		}
		return registered.add(new Pair(player, assist, assist2));
	}
	
	public boolean isRegistered(L2PcInstance player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player)
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
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player)
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
	
	public int getRegisteredCount()
	{
		return registered.size();
	}
	
	private class Pair
	{
		private L2PcInstance leader, assist, assist2;
		
		public Pair(L2PcInstance leader, L2PcInstance assist, L2PcInstance assist2)
		{
			this.leader = leader;
			this.assist = assist;
			this.assist2 = assist2;
		}
		
		public L2PcInstance getAssist()
		{
			return assist;
		}
		
		public L2PcInstance getAssist2()
		{
			return assist2;
		}
		
		public L2PcInstance getLeader()
		{
			return leader;
		}
		
		public boolean check()
		{
			if ((leader == null || !(leader.isOnline() == 1)))
			{
				if (assist != null || assist.isOnline() == 1)
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist2 != null || assist2.isOnline() == 1)
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				
				return false;
			}
			
			else if (((assist == null || !(assist.isOnline() == 1) || (assist2 == null || !(assist2.isOnline() == 1)) && (leader != null || leader.isOnline() == 1))))
			{
				leader.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist != null || assist.isOnline() == 1)
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist2 != null || assist2.isOnline() ==1)
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				
				return false;
			}
			return true;
		}
		
		public boolean isDead()
		{
			if (ArenaConfig.ARENA_PROTECT)
			{
				if (leader != null && leader.isOnline() == 1 && leader.isArenaAttack() && !leader.isDead() && !leader.isInsideZone(L2Character.ARENA_EVENT))
					leader.logout();
				if (assist != null && assist.isOnline() == 1 && assist.isArenaAttack() && !assist.isDead() && !assist.isInsideZone(L2Character.ARENA_EVENT))
					assist.logout();
				if (assist2 != null && assist2.isOnline() == 1 && assist2.isArenaAttack() && !assist2.isDead() && !assist2.isInsideZone(L2Character.ARENA_EVENT))
					assist2.logout();
			}
			
			if ((leader == null || leader.isDead() || !(leader.isOnline() == 1) || !leader.isInsideZone(L2Character.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !(assist.isOnline() == 1) || !assist.isInsideZone(L2Character.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !(assist2.isOnline() == 1) || !assist2.isInsideZone(L2Character.ARENA_EVENT)))
				return false;
			
			return !(leader.isDead() && assist.isDead() && assist2.isDead());
		}
		
		public boolean isAlive()
		{
			if ((leader == null || leader.isDead() || !(leader.isOnline() == 1) || !leader.isInsideZone(L2Character.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !(assist.isOnline() == 1) || !assist.isInsideZone(L2Character.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !(assist2.isOnline() == 1) || !assist2.isInsideZone(L2Character.ARENA_EVENT)))
				return false;
			
			return !(leader.isDead() && assist.isDead() && assist2.isDead());
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
			if (assist != null && assist.isOnline() == 1)
			{
				//assist.getAppearance().setInvisible();
				assist.setCurrentCp(assist.getMaxCp());
				assist.setCurrentHp(assist.getMaxHp());
				assist.setCurrentMp(assist.getMaxMp());
				
				/*
				if (assist.isInObserverMode())
				{
					assist.setLastCords(x, y + 200, z);
					assist.leaveOlympiadObserverMode();
				}
				 */
				if (!assist.isInJail())
					assist.teleToLocation(x, y + 200, z);
				
				assist.broadcastUserInfo();
			}
			if (assist2 != null && assist2.isOnline() == 1)
			{
				//assist2.getAppearance().setInvisible();
				assist2.setCurrentCp(assist2.getMaxCp());
				assist2.setCurrentHp(assist2.getMaxHp());
				assist2.setCurrentMp(assist2.getMaxMp());
				
				/*
				if (assist2.isInObserverMode())
				{
					assist2.setLastCords(x, y + 150, z);
					assist2.leaveOlympiadObserverMode();
				}
				 */
				if (!assist2.isInJail())
					assist2.teleToLocation(x, y + 150, z);
				
				assist2.broadcastUserInfo();
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
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist.setTitle(title);
				assist.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.setTitle(title);
				assist2.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
			}
		}
		
		public void saveTitle()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader._originalTitleColorTournament = leader.getAppearance().getTitleColor();
				leader._originalTitleTournament = leader.getTitle();
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist._originalTitleColorTournament = assist.getAppearance().getTitleColor();
				assist._originalTitleTournament = assist.getTitle();
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2._originalTitleColorTournament = assist2.getAppearance().getTitleColor();
				assist2._originalTitleTournament = assist2.getTitle();
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
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist.setTitle(assist._originalTitleTournament);
				assist.getAppearance().setTitleColor(assist._originalTitleColorTournament);
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.setTitle(assist2._originalTitleTournament);
				assist2.getAppearance().setTitleColor(assist2._originalTitleColorTournament);
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
			}
		}
		
		public void setArenaInstance() 
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInstanceId(2); //3x3 Tournament Instance
			
			if (assist != null && assist.isOnline() == 1)
				assist.setInstanceId(2); //3x3 Tournament Instance
			
			if (assist2 != null && assist2.isOnline() == 1)
				assist2.setInstanceId(2); //3x3 Tournament Instance
		}
		
		public void setRealInstance() 
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInstanceId(0);
			
			if (assist != null && assist.isOnline() == 1)
				assist.setInstanceId(0);
			
			if (assist2 != null && assist2.isOnline() == 1)
				assist2.setInstanceId(0);
		}
		
		public void rewards()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				if (leader.isDonator())
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, leader, true);
				else
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3, leader, true);
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				if (assist.isDonator())
					assist.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, assist, true);
				else
					assist.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3, assist, true);
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				if (assist2.isDonator())
					assist2.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, assist2, true);
				else
					assist2.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_WIN_REWARD_COUNT_3X3, assist2, true);
			}
			
			sendPacket("Congratulations, your team won the event!", 5);
		}
		
		public void rewardsLost()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				if (leader.isDonator())
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, leader, true);
				else
					leader.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3, leader, true);
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				if (assist.isDonator())
					assist.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, assist, true);
				else
					assist.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3, assist, true);
			}
			if (assist2 != null && assist2.isOnline() == 1)
			{
				if (assist2.isDonator())
					assist2.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3 * ArenaConfig.ARENA_VIP_DROP_RATE, assist2, true);
				else
					assist2.addItem("Arena_Event", ArenaConfig.ARENA_REWARD_ID, ArenaConfig.ARENA_LOST_REWARD_COUNT_3X3, assist2, true);
			}
			
			sendPacket("your team lost the event! =(", 5);
		}
		
		public void setInTournamentEvent(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.setInArenaEvent(val);
			
			if (assist != null && assist.isOnline() == 1)
				assist.setInArenaEvent(val);
			
			if (assist2 != null && assist2.isOnline() == 1)
				assist2.setInArenaEvent(val);
		}
		
		public void removeMessage()
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena3x3(false);
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist.sendMessage("Tournament: Your participation has been removed.");
				assist.setArenaProtection(false);
				assist.setArena3x3(false);
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.sendMessage("Tournament: Your participation has been removed.");
				assist2.setArenaProtection(false);
				assist2.setArena3x3(false);
			}
		}
		
		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setArenaProtection(val);
				leader.setArena3x3(val);
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist.setArenaProtection(val);
				assist.setArena3x3(val);
			}
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.setArenaProtection(val);
				assist2.setArena3x3(val);
			}
		}
		
		public void revive()
		{
			if (leader != null && leader.isOnline() == 1 && leader.isDead())
				leader.doRevive();
			
			if (assist != null && assist.isOnline() == 1 && assist.isDead())
				assist.doRevive();
			
			if (assist2 != null && assist2.isOnline() == 1 && assist2.isDead())
				assist2.doRevive();
		}
		
		public void setImobilised(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setIsInvul(val);
				leader.setStopArena(val);
			}
			if (assist != null && assist.isOnline() == 1)
			{
				assist.setIsInvul(val);
				assist.setStopArena(val);
			}
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.setIsInvul(val);
				assist2.setStopArena(val);
			}
		}
		
		public void setArenaAttack(boolean val)
		{
			if (leader != null && leader.isOnline() == 1)
			{
				leader.setArenaAttack(val);
				leader.broadcastUserInfo();
			}
			
			if (assist != null && assist.isOnline() == 1)
			{
				assist.setArenaAttack(val);
				assist.broadcastUserInfo();
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				assist2.setArenaAttack(val);
				assist2.broadcastUserInfo();
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
			
			if (assist != null && assist.isOnline() == 1)
			{
				// Remove Summon's buffs
				if (assist.getPet() != null)
				{
					L2Summon summon = assist.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof L2PetInstance)
						summon.unSummon(assist);
				}
				
				if (assist.getMountType() == 1 || assist.getMountType() == 2)
					assist.dismount();
			}
			
			if (assist2 != null && assist2.isOnline() == 1)
			{
				// Remove Summon's buffs
				if (assist2.getPet() != null)
				{
					L2Summon summon = assist2.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof L2PetInstance)
						summon.unSummon(assist2);
					
				}
				
				if (assist2.getMountType() == 1 || assist2.getMountType() == 2)
					assist2.dismount();
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
			
			if (!(assist.getClassId() == ClassId.shillenElder || assist.getClassId() == ClassId.shillienSaint || assist.getClassId() == ClassId.bishop || assist.getClassId() == ClassId.cardinal || assist.getClassId() == ClassId.elder || assist.getClassId() == ClassId.evaSaint))
			{
				for (L2Effect effect : assist.getAllEffects())
				{
					if (ArenaConfig.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (!(assist2.getClassId() == ClassId.shillenElder || assist2.getClassId() == ClassId.shillienSaint || assist2.getClassId() == ClassId.bishop || assist2.getClassId() == ClassId.cardinal || assist2.getClassId() == ClassId.elder || assist2.getClassId() == ClassId.evaSaint))
			{
				for (L2Effect effect : assist2.getAllEffects())
				{
					if (ArenaConfig.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist2.stopSkillEffects(effect.getSkill().getId());
				}
			}
		}
		
		public void sendPacket(String message, int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist != null && assist.isOnline() == 1)
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist2 != null && assist2.isOnline() == 1)
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000));
		}
		
		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				ThreadPoolManager.getInstance().scheduleGeneral(new countdown(leader, duration), 0);
			
			if (assist != null && assist.isOnline() == 1)
				ThreadPoolManager.getInstance().scheduleGeneral(new countdown(assist, duration), 0);
			
			if (assist2 != null && assist2.isOnline() == 1)
				ThreadPoolManager.getInstance().scheduleGeneral(new countdown(assist2, duration), 0);
		}
		
		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline() == 1)
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			
			if (assist != null && assist.isOnline() == 1)
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			
			if (assist2 != null && assist2.isOnline() == 1)
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
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
			pairOne.inicarContagem(ArenaConfig.ARENA_WAIT_INTERVAL_3X3);
			pairTwo.inicarContagem(ArenaConfig.ARENA_WAIT_INTERVAL_3X3);
			try
			{
				Thread.sleep(ArenaConfig.ARENA_WAIT_INTERVAL_3X3 * 1000);
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
			if (pairOne.isAlive() && !pairTwo.isAlive())
			{
				L2PcInstance leader1 = pairOne.getLeader();
				L2PcInstance leader2 = pairTwo.getLeader();
				
				if (leader1.getClan() != null && leader2.getClan() != null && ArenaConfig.TOURNAMENT_EVENT_ANNOUNCE)
					Broadcast.gameAnnounceToOnlinePlayers("(3X3): (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			else if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				L2PcInstance leader1 = pairTwo.getLeader();
				L2PcInstance leader2 = pairOne.getLeader();
				
				if (leader1.getClan() != null && leader2.getClan() != null && ArenaConfig.TOURNAMENT_EVENT_ANNOUNCE)
					Broadcast.gameAnnounceToOnlinePlayers("(3X3): (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				
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
		protected static final Arena3x3 INSTANCE = new Arena3x3();
	}
}