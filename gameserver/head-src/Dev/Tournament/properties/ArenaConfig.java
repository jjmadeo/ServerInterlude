/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package Dev.Tournament.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2jfrozen.ExProperties;

public class ArenaConfig
{
	protected static final Logger _log = Logger.getLogger(ArenaConfig.class.getName());
	
	private static final String ARENA_FILE = "./config/frozen/Tournament.properties";
	
	// tournament
	public static boolean TOURNAMENT_EVENT_START;
	public static boolean TOURNAMENT_EVENT_TIME;
	public static boolean TOURNAMENT_EVENT_SUMMON;
	public static boolean TOURNAMENT_EVENT_ANNOUNCE;
	public static boolean CHECK_NOBLESS;
	
	public static int TOURNAMENT_TIME;
	public static String[] TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY;
	
	public static String TITLE_COLOR_TEAM1;
	public static String TITLE_COLOR_TEAM2;
	
	public static String MSG_TEAM1;
	public static String MSG_TEAM2;
	
	public static boolean Allow_Same_HWID_On_Tournament;
	
	public static int ARENA_NPC;
	
	public static int NPC_locx;
	public static int NPC_locy;
	public static int NPC_locz;
	public static int NPC_Heading;
	
	public static int NPC_locx2;
	public static int NPC_locy2;
	public static int NPC_locz2;
	public static int NPC_Heading2;
	
	public static int Tournament_locx;
	public static int Tournament_locy;
	public static int Tournament_locz;
	
	public static boolean ALLOW_1X1_REGISTER;
	public static boolean ALLOW_3X3_REGISTER;
	public static boolean ALLOW_5X5_REGISTER;
	public static boolean ALLOW_9X9_REGISTER;
	
	public static boolean ALLOW_5X5_LOSTBUFF;
	
	public static boolean ARENA_MESSAGE_ENABLED;
	public static String ARENA_MESSAGE_TEXT;
	public static int ARENA_MESSAGE_TIME;
	
	public static int ARENA_EVENT_COUNT_1X1;
	public static int[][] ARENA_EVENT_LOCS_1X1;
	
	public static int ARENA_EVENT_COUNT_3X3;
	public static int[][] ARENA_EVENT_LOCS_3X3;
	
	public static int ARENA_EVENT_COUNT_5X5;
	public static int[][] ARENA_EVENT_LOCS_5X5;
	
	public static int ARENA_EVENT_COUNT_9X9;
	public static int[][] ARENA_EVENT_LOCS_9X9;
	
	public static int duelist_COUNT_3X3;
	public static int dreadnought_COUNT_3X3;
	public static int tanker_COUNT_3X3;
	public static int dagger_COUNT_3X3;
	public static int archer_COUNT_3X3;
	public static int bs_COUNT_3X3;
	public static int archmage_COUNT_3X3;
	public static int soultaker_COUNT_3X3;
	public static int mysticMuse_COUNT_3X3;
	public static int stormScreamer_COUNT_3X3;
	public static int titan_COUNT_3X3;
	public static int dominator_COUNT_3X3;
	
	public static int duelist_COUNT_5X5;
	public static int dreadnought_COUNT_5X5;
	public static int tanker_COUNT_5X5;
	public static int dagger_COUNT_5X5;
	public static int archer_COUNT_5X5;
	public static int bs_COUNT_5X5;
	public static int archmage_COUNT_5X5;
	public static int soultaker_COUNT_5X5;
	public static int mysticMuse_COUNT_5X5;
	public static int stormScreamer_COUNT_5X5;
	public static int titan_COUNT_5X5;
	public static int dominator_COUNT_5X5;
	
	public static int duelist_COUNT_9X9;
	public static int dreadnought_COUNT_9X9;
	public static int tanker_COUNT_9X9;
	public static int dagger_COUNT_9X9;
	public static int archer_COUNT_9X9;
	public static int bs_COUNT_9X9;
	public static int archmage_COUNT_9X9;
	public static int soultaker_COUNT_9X9;
	public static int mysticMuse_COUNT_9X9;
	public static int stormScreamer_COUNT_9X9;
	public static int titan_COUNT_9X9;
	public static int dominator_COUNT_9X9;
	
	public static int ARENA_REWARD_ID;
	public static int ARENA_VIP_DROP_RATE;
	public static int ARENA_WIN_REWARD_COUNT_1X1;
	public static int ARENA_LOST_REWARD_COUNT_1X1;
	public static int ARENA_WIN_REWARD_COUNT_3X3;
	public static int ARENA_LOST_REWARD_COUNT_3X3;
	public static int ARENA_WIN_REWARD_COUNT_5X5;
	public static int ARENA_LOST_REWARD_COUNT_5X5;
	public static int ARENA_WIN_REWARD_COUNT_9X9;
	public static int ARENA_LOST_REWARD_COUNT_9X9;
	
	public static int ARENA_CHECK_INTERVAL;
	public static int ARENA_CALL_INTERVAL;
	
	public static int ARENA_WAIT_INTERVAL_1X1;
	public static int ARENA_WAIT_INTERVAL_3X3;
	public static int ARENA_WAIT_INTERVAL_5X5;
	public static int ARENA_WAIT_INTERVAL_9X9;
	
	public static String TOURNAMENT_ID_RESTRICT;
	public static List<Integer> TOURNAMENT_LISTID_RESTRICT;
	public static boolean ARENA_SKILL_PROTECT;
	public static List<Integer> ARENA_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_DISABLE_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_STOP_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_DISABLE_SKILL_LIST_PERM = new ArrayList<>();
	public static boolean ARENA_PROTECT;
	
	public static void init()
	{
		ExProperties tournament = load(ARENA_FILE);
		
		TOURNAMENT_EVENT_START = tournament.getProperty("TournamentStartOn", false);
		TOURNAMENT_EVENT_TIME = tournament.getProperty("TournamentAutoEvent", false);
		TOURNAMENT_EVENT_SUMMON = tournament.getProperty("TournamentSummon", false);
		TOURNAMENT_EVENT_ANNOUNCE = tournament.getProperty("TournamentAnnounce", false);
		CHECK_NOBLESS = tournament.getProperty("TournamentNobles", false);
		
		TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY = tournament.getProperty("TournamentStartTime", "20:00").split(",");
		
		TOURNAMENT_TIME = Integer.parseInt(tournament.getProperty("TournamentEventTime", "1"));
		
		TITLE_COLOR_TEAM1 = tournament.getProperty("TitleColorTeam_1", "FFFFFF");
		TITLE_COLOR_TEAM2 = tournament.getProperty("TitleColorTeam_2", "FFFFFF");
		
		MSG_TEAM1 = tournament.getProperty("TitleTeam_1", "Team [1]");
		MSG_TEAM2 = tournament.getProperty("TitleTeam_2", "Team [2]");
		
		Allow_Same_HWID_On_Tournament = Boolean.parseBoolean(tournament.getProperty("Allow_Same_HWID_On_Tournament", "true"));
		
		ARENA_NPC = Integer.parseInt(tournament.getProperty("NPCRegister", "1"));
		
		NPC_locx = Integer.parseInt(tournament.getProperty("Locx", "1"));
		NPC_locy = Integer.parseInt(tournament.getProperty("Locy", "1"));
		NPC_locz = Integer.parseInt(tournament.getProperty("Locz", "1"));
		NPC_Heading = Integer.parseInt(tournament.getProperty("Heading", "1"));
		
		NPC_locx2 = Integer.parseInt(tournament.getProperty("Locx2", "1"));
		NPC_locy2 = Integer.parseInt(tournament.getProperty("Locy2", "1"));
		NPC_locz2 = Integer.parseInt(tournament.getProperty("Locz2", "1"));
		NPC_Heading2 = Integer.parseInt(tournament.getProperty("Heading2", "1"));
		
		Tournament_locx = Integer.parseInt(tournament.getProperty("TournamentLocx", "1"));
		Tournament_locy = Integer.parseInt(tournament.getProperty("TournamentLocy", "1"));
		Tournament_locz = Integer.parseInt(tournament.getProperty("TournamentLocz", "1"));
		
		ALLOW_1X1_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow1x1Register", "true"));
		ALLOW_3X3_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow3x3Register", "true"));
		ALLOW_5X5_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow5x5Register", "true"));
		ALLOW_9X9_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow9x9Register", "true"));
		
		ALLOW_5X5_LOSTBUFF = Boolean.parseBoolean(tournament.getProperty("Allow5x5LostBuff", "false"));
		
		ARENA_MESSAGE_ENABLED = Boolean.parseBoolean(tournament.getProperty("ScreenArenaMessageEnable", "false"));
		ARENA_MESSAGE_TEXT = tournament.getProperty("ScreenArenaMessageText", "Welcome to L2J server!");
		ARENA_MESSAGE_TIME = Integer.parseInt(tournament.getProperty("ScreenArenaMessageTime", "10")) * 1000;
		
		String[] arenaLocs1x1 = tournament.getProperty("ArenasLoc1x1", "").split(";");
		String[] locSplit1x1 = null;
		ARENA_EVENT_COUNT_1X1 = arenaLocs1x1.length;
		ARENA_EVENT_LOCS_1X1 = new int[ARENA_EVENT_COUNT_1X1][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_1X1; i++)
		{
			locSplit1x1 = arenaLocs1x1[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_1X1[i][j] = Integer.parseInt(locSplit1x1[j].trim());
			}
		}
		
		String[] arenaLocs3x3 = tournament.getProperty("ArenasLoc3x3", "").split(";");
		String[] locSplit3x3 = null;
		ARENA_EVENT_COUNT_3X3 = arenaLocs3x3.length;
		ARENA_EVENT_LOCS_3X3 = new int[ARENA_EVENT_COUNT_3X3][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_3X3; i++)
		{
			locSplit3x3 = arenaLocs3x3[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_3X3[i][j] = Integer.parseInt(locSplit3x3[j].trim());
			}
		}
		
		String[] arenaLocs5x5 = tournament.getProperty("Arenas5x5Loc", "").split(";");
		String[] locSplit5x5 = null;
		ARENA_EVENT_COUNT_5X5 = arenaLocs5x5.length;
		ARENA_EVENT_LOCS_5X5 = new int[ARENA_EVENT_COUNT_5X5][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_5X5; i++)
		{
			locSplit5x5 = arenaLocs5x5[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_5X5[i][j] = Integer.parseInt(locSplit5x5[j].trim());
			}
		}
		
		String[] arenaLocs9x9 = tournament.getProperty("Arenas9x9Loc", "").split(";");
		String[] locSplit8x8 = null;
		ARENA_EVENT_COUNT_9X9 = arenaLocs9x9.length;
		ARENA_EVENT_LOCS_9X9 = new int[ARENA_EVENT_COUNT_9X9][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_9X9; i++)
		{
			locSplit8x8 = arenaLocs9x9[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_9X9[i][j] = Integer.parseInt(locSplit8x8[j].trim());
			}
		}
		
		duelist_COUNT_3X3 = tournament.getProperty("duelist_amount_3x3", 1);
		dreadnought_COUNT_3X3 = tournament.getProperty("dreadnought_amount_3x3", 1);
		tanker_COUNT_3X3 = tournament.getProperty("tanker_amount_3x3", 1);
		dagger_COUNT_3X3 = tournament.getProperty("dagger_amount_3x3", 1);
		archer_COUNT_3X3 = tournament.getProperty("archer_amount_3x3", 1);
		bs_COUNT_3X3 = tournament.getProperty("bs_amount_3x3", 1);
		archmage_COUNT_3X3 = tournament.getProperty("archmage_amount_3x3", 1);
		soultaker_COUNT_3X3 = tournament.getProperty("soultaker_amount_3x3", 1);
		mysticMuse_COUNT_3X3 = tournament.getProperty("mysticMuse_amount_3x3", 1);
		stormScreamer_COUNT_3X3 = tournament.getProperty("stormScreamer_amount_3x3", 1);
		titan_COUNT_3X3 = tournament.getProperty("titan_amount_3x3", 1);
		dominator_COUNT_3X3 = tournament.getProperty("dominator_amount_3x3", 1);
		
		duelist_COUNT_5X5 = tournament.getProperty("duelist_amount_5x5", 1);
		dreadnought_COUNT_5X5 = tournament.getProperty("dreadnought_amount_5x5", 1);
		tanker_COUNT_5X5 = tournament.getProperty("tanker_amount_5x5", 1);
		dagger_COUNT_5X5 = tournament.getProperty("dagger_amount_5x5", 1);
		archer_COUNT_5X5 = tournament.getProperty("archer_amount_5x5", 1);
		bs_COUNT_5X5 = tournament.getProperty("bs_amount_5x5", 1);
		archmage_COUNT_5X5 = tournament.getProperty("archmage_amount_5x5", 1);
		soultaker_COUNT_5X5 = tournament.getProperty("soultaker_amount_5x5", 1);
		mysticMuse_COUNT_5X5 = tournament.getProperty("mysticMuse_amount_5x5", 1);
		stormScreamer_COUNT_5X5 = tournament.getProperty("stormScreamer_amount_5x5", 1);
		titan_COUNT_5X5 = tournament.getProperty("titan_amount_5x5", 1);
		dominator_COUNT_5X5 = tournament.getProperty("dominator_amount_5x5", 1);
		
		duelist_COUNT_9X9 = tournament.getProperty("duelist_amount_9x9", 1);
		dreadnought_COUNT_9X9 = tournament.getProperty("dreadnought_amount_9x9", 1);
		tanker_COUNT_9X9 = tournament.getProperty("tanker_amount_9x9", 1);
		dagger_COUNT_9X9 = tournament.getProperty("dagger_amount_9x9", 1);
		archer_COUNT_9X9 = tournament.getProperty("archer_amount_9x9", 1);
		bs_COUNT_9X9 = tournament.getProperty("bs_amount_9x9", 1);
		archmage_COUNT_9X9 = tournament.getProperty("archmage_amount_9x9", 1);
		soultaker_COUNT_9X9 = tournament.getProperty("soultaker_amount_9x9", 1);
		mysticMuse_COUNT_9X9 = tournament.getProperty("mysticMuse_amount_9x9", 1);
		stormScreamer_COUNT_9X9 = tournament.getProperty("stormScreamer_amount_9x9", 1);
		titan_COUNT_9X9 = tournament.getProperty("titan_amount_9x9", 1);
		dominator_COUNT_9X9 = tournament.getProperty("dominator_amount_9x9", 1);
		
		ARENA_REWARD_ID = tournament.getProperty("ArenaRewardId", 57);
		ARENA_VIP_DROP_RATE = tournament.getProperty("ArenaRateDropVIP", 1);
		ARENA_WIN_REWARD_COUNT_1X1 = tournament.getProperty("ArenaWinRewardCount1x1", 1);
		ARENA_LOST_REWARD_COUNT_1X1 = tournament.getProperty("ArenaLostRewardCount1x1", 1);
		ARENA_WIN_REWARD_COUNT_3X3 = tournament.getProperty("ArenaWinRewardCount3x3", 1);
		ARENA_LOST_REWARD_COUNT_3X3 = tournament.getProperty("ArenaLostRewardCount3x3", 1);
		ARENA_WIN_REWARD_COUNT_5X5 = tournament.getProperty("ArenaWinRewardCount5x5", 1);
		ARENA_LOST_REWARD_COUNT_5X5 = tournament.getProperty("ArenaLostRewardCount5x5", 1);
		ARENA_WIN_REWARD_COUNT_9X9 = tournament.getProperty("ArenaWinRewardCount9x9", 1);
		ARENA_LOST_REWARD_COUNT_9X9 = tournament.getProperty("ArenaLostRewardCount9x9", 1);
		
		ARENA_CHECK_INTERVAL = tournament.getProperty("ArenaBattleCheckInterval", 15) * 1000;
		ARENA_CALL_INTERVAL = tournament.getProperty("ArenaBattleCallInterval", 60);
		
		ARENA_WAIT_INTERVAL_1X1 = tournament.getProperty("ArenaBattleWaitInterval1x1", 20);
		ARENA_WAIT_INTERVAL_3X3 = tournament.getProperty("ArenaBattleWaitInterval3x3", 20);
		ARENA_WAIT_INTERVAL_5X5 = tournament.getProperty("ArenaBattleWaitInterval5x5", 45);
		ARENA_WAIT_INTERVAL_9X9 = tournament.getProperty("ArenaBattleWaitInterval9x9", 45);
		
		TOURNAMENT_ID_RESTRICT = tournament.getProperty("ItemsRestriction");
		
		TOURNAMENT_LISTID_RESTRICT = new ArrayList<>();
		for (String id : TOURNAMENT_ID_RESTRICT.split(","))
			TOURNAMENT_LISTID_RESTRICT.add(Integer.parseInt(id));
		
		ARENA_SKILL_PROTECT = Boolean.parseBoolean(tournament.getProperty("ArenaSkillProtect", "false"));
		
		for (String id : tournament.getProperty("ArenaDisableSkillList", "0").split(","))
		{
			ARENA_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("DisableSkillList", "0").split(","))
		{
			ARENA_DISABLE_SKILL_LIST_PERM.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("ArenaDisableSkillList_noStart", "0").split(","))
		{
			ARENA_DISABLE_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("ArenaStopSkillList", "0").split(","))
		{
			ARENA_STOP_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		ARENA_PROTECT = Boolean.parseBoolean(tournament.getProperty("ArenaProtect", "true"));
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		return result;
	}
}