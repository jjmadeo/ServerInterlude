package Dev.Tournament.properties;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.clientpackets.Say2;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.util.Broadcast;

import Dev.Tournament.Arena1x1;
import Dev.Tournament.Arena3x3;
import Dev.Tournament.Arena5x5;
import Dev.Tournament.Arena9x9;

import java.util.Arrays;

public abstract class ArenaTask
{
	private static final Logger LOGGER = Logger.getLogger(ArenaTask.class);

	public static L2Spawn _npcSpawn1;
	public static L2Spawn _npcSpawn2;
	
	public static int _bossHeading = 0;
	
	/** The _in progress. */
	public static boolean _started = false;
	
	public static boolean _aborted = false;
	
	public static void SpawnEvent()
	{
		Arena1x1.getInstance().clear();
		Arena3x3.getInstance().clear();
		Arena5x5.getInstance().clear();
		Arena9x9.getInstance().clear();
		
		spawnNpc1();
		spawnNpc2();
		
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Event Started");
		//Broadcast.gameAnnounceToOnlinePlayers("Tournament: 1x1| 3x3 | 5x5 | 9x9");
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Duration: " + ArenaConfig.TOURNAMENT_TIME + " minute(s)!");
		
		_aborted = false;
		_started = true;
		

		ThreadPoolManager.getInstance().scheduleGeneral(Arena1x1.getInstance(), 5000);

		ThreadPoolManager.getInstance().scheduleGeneral(Arena3x3.getInstance(), 5000);

		ThreadPoolManager.getInstance().scheduleGeneral(Arena5x5.getInstance(), 5000);

		ThreadPoolManager.getInstance().scheduleGeneral(Arena9x9.getInstance(), 5000);
		
		

		
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{

			@Override
			public void run()
			{
				waiter(ArenaConfig.TOURNAMENT_TIME * 60 * 1000); // minutes for event time
				
				if (!_aborted)
					finishEvent();
				
			}
			
		},5000);
		
	}
	
	public static void finishEvent()
	{
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Event Finished!");
		
		unspawnNpc1();
		unspawnNpc2();
		_started = false;
		
		ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player != null && player.isOnline() == 1)
			{
				if (player.isArenaProtection())
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
					{
						
						@Override
						public void run()
						{
							if (player.isOnline() == 1 && !player.isInArenaEvent() && !player.isArenaAttack())
							{
								if (player.isArena1x1())
									Arena1x1.getInstance().remove(player);
								if (player.isArena3x3())
									Arena3x3.getInstance().remove(player);
								if (player.isArena5x5())
									Arena5x5.getInstance().remove(player);
								if (player.isArena9x9())
									Arena9x9.getInstance().remove(player);
								
								player.setArenaProtection(false);
							}
						}
					}, 25000);
				}
				
				CreatureSay cs = new CreatureSay(player.getObjectId(), Say2.PARTY, "[Tournament]", ("Next Tournament: " + ArenaEvent.getInstance().getNextTime()) + " (GMT-3)."); // 8D
				player.sendPacket(cs);
			}
		}
	}
	
	public static void spawnNpc1()
	{
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(ArenaConfig.ARENA_NPC);
		
		try
		{
			_npcSpawn1 = new L2Spawn(tmpl);
			_npcSpawn1.setLocx(loc1x());
			_npcSpawn1.setLocy(loc1y());
			_npcSpawn1.setLocz(loc1z());
			_npcSpawn1.setHeading(ArenaConfig.NPC_Heading);
			_npcSpawn1.setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn1, false);
			
			_npcSpawn1.doSpawn();
			_npcSpawn1.getLastSpawn().getStatus().setCurrentHp(999999999);
			_npcSpawn1.getLastSpawn().isAggressive();
			_npcSpawn1.getLastSpawn().decayMe();
			_npcSpawn1.getLastSpawn().spawnMe(_npcSpawn1.getLastSpawn().getX(), _npcSpawn1.getLastSpawn().getY(), _npcSpawn1.getLastSpawn().getZ());
			_npcSpawn1.getLastSpawn().broadcastPacket(new MagicSkillUser(_npcSpawn1.getLastSpawn(), _npcSpawn1.getLastSpawn(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void spawnNpc2()
	{
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(ArenaConfig.ARENA_NPC);
		
		try
		{
			_npcSpawn2 = new L2Spawn(tmpl);
			_npcSpawn2.setLocx(loc2x());
			_npcSpawn2.setLocy(loc2y());
			_npcSpawn2.setLocz(loc2z());
			_npcSpawn2.setHeading(ArenaConfig.NPC_Heading2);
			_npcSpawn2.setRespawnDelay(1);
			
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn2, false);
			
			
			_npcSpawn2.doSpawn();
			_npcSpawn2.getLastSpawn().getStatus().setCurrentHp(999999999);
			_npcSpawn2.getLastSpawn().isAggressive();
			_npcSpawn2.getLastSpawn().decayMe();
			_npcSpawn2.getLastSpawn().spawnMe(_npcSpawn2.getLastSpawn().getX(), _npcSpawn2.getLastSpawn().getY(), _npcSpawn2.getLastSpawn().getZ());
			_npcSpawn2.getLastSpawn().broadcastPacket(new MagicSkillUser(_npcSpawn2.getLastSpawn(), _npcSpawn2.getLastSpawn(), 1034, 1, 1, 1));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	public static void unspawnNpc1()
	{
		if (_npcSpawn1 == null)
			return;
		
		_npcSpawn1.getLastSpawn().deleteMe();
		_npcSpawn1.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(_npcSpawn1, true);
	}
	
	public static void unspawnNpc2()
	{
		if (_npcSpawn2 == null)
			return;
		
		_npcSpawn2.getLastSpawn().deleteMe();
		_npcSpawn2.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(_npcSpawn2, true);
	}
	
	public static int loc1x()
	{
		int loc1x = ArenaConfig.NPC_locx;
		return loc1x;
	}
	
	public static int loc1y()
	{
		int loc1y = ArenaConfig.NPC_locy;
		return loc1y;
	}
	
	public static int loc1z()
	{
		int loc1z = ArenaConfig.NPC_locz;
		return loc1z;
	}
	
	public static int loc2x()
	{
		int loc2x = ArenaConfig.NPC_locx2;
		return loc2x;
	}
	
	public static int loc2y()
	{
		int loc2y = ArenaConfig.NPC_locy2;
		return loc2y;
	}
	
	public static int loc2z()
	{
		int loc2z = ArenaConfig.NPC_locz2;
		return loc2z;
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	/*protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		


		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					
					if (_started)
					{
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: Party Event PvP");
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: Teleport in the GK to (Tournament) Zone");
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: Reward: " + ItemTable.getInstance().getTemplate(ArenaConfig.ARENA_REWARD_ID).getName());
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds / 60 / 60 + " hour(s) till event finish!");
					}
					break;
				case 1800: // 30 minutes left
				case 900: // 15 minutes left
				case 600: // 10 minutes left
				case 300: // 5 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					// removeOfflinePlayers();
					
					if (_started)
					{
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds / 60 + " minute(s) till event finish!");
					}
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
				case 10: // 10 seconds left
				case 3: // 3 seconds left
				case 2: // 2 seconds left
				case 1: // 1 seconds left
					if (_started)
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds + " second(s) till event finish!");
					
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}*/

	protected static void waiter(long interval) {
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);

		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted) {
			seconds--;

			if (_started) {
				// Usamos un stream para iterar por las diferentes cantidades de tiempo restantes
				int finalSeconds = seconds;
				Arrays.asList(3600, 1800, 900, 600, 300, 240, 180, 120, 60, 30, 15, 10, 3, 2, 1)
						.stream()
						.filter(timeLeft -> finalSeconds == timeLeft)
						.findFirst()
						.ifPresent(timeLeft -> {
							// Utilizamos una función lambda para hacer el anuncio correspondiente según la cantidad de tiempo restante
							if (timeLeft >= 60) {
								Broadcast.gameAnnounceToOnlinePlayers("Tournament: Party Event PvP");
								Broadcast.gameAnnounceToOnlinePlayers("Tournament: Teleport in the GK to (Tournament) Zone");
								Broadcast.gameAnnounceToOnlinePlayers("Tournament: Reward: " + ItemTable.getInstance().getTemplate(ArenaConfig.ARENA_REWARD_ID).getName());
								Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + timeLeft / 60 + " minute(s) till event finish!");
							} else {
								Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + timeLeft + " second(s) till event finish!");
							}
						});
			}

			try {
				// Esperamos 1 segundo antes de continuar
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}