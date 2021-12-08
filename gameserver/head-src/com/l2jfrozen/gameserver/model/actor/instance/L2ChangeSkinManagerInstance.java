/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.model.base.ClassId;

/**
 * @author execanciani & Toxico
 */
public class L2ChangeSkinManagerInstance extends L2NpcInstance
{
	public enum Races
	{
		FIGHTER(com.l2jfrozen.gameserver.model.base.Race.human, ClassId.fighter),
		MAGE(com.l2jfrozen.gameserver.model.base.Race.human, ClassId.mage),
		ELVENFIGHTER(com.l2jfrozen.gameserver.model.base.Race.elf, ClassId.elvenFighter),
		ELVENMAGE(com.l2jfrozen.gameserver.model.base.Race.elf, ClassId.elvenMage),
		DARKFIGHTER(com.l2jfrozen.gameserver.model.base.Race.darkelf, ClassId.darkFighter),
		DARKMAGE(com.l2jfrozen.gameserver.model.base.Race.darkelf, ClassId.darkMage),
		ORCFIGHTER(com.l2jfrozen.gameserver.model.base.Race.orc, ClassId.orcFighter),
		ORCMAGE(com.l2jfrozen.gameserver.model.base.Race.orc, ClassId.orcMage),
		DWARVEN(com.l2jfrozen.gameserver.model.base.Race.dwarf, ClassId.dwarvenFighter);
		
		private com.l2jfrozen.gameserver.model.base.Race races;
		private ClassId classId;
		
		private Races(com.l2jfrozen.gameserver.model.base.Race races, ClassId classid)
		{
			this.races = races;
			this.classId = classid;
		}
		
		public int getRaceId()
		{
			return races.ordinal();
		}
		
		public int getClassId()
		{
			return classId.getId();
		}
	}


	public L2ChangeSkinManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		Races races = null;
		if (command.startsWith("HumanFighter"))
		{
			races = Races.FIGHTER;
		}
		
		else if (command.startsWith("HumanMage"))
		{
			races = Races.MAGE;
		}
		
		else if (command.startsWith("ElfFighter"))
		{
			races = Races.ELVENFIGHTER;
		}
		
		else if (command.startsWith("ElfMage"))
		{
			races = Races.ELVENMAGE;
		}
		
		else if (command.startsWith("DarkElfFighter"))
		{
			races = Races.DARKFIGHTER;
		}
		
		else if (command.startsWith("DarkElfMage"))
		{
			races = Races.DARKMAGE;
		}
		
		else if (command.startsWith("OrcFighter"))
		{
			races = Races.ORCFIGHTER;
		}
		
		else if (command.startsWith("OrcMage"))
		{
			races = Races.ORCMAGE;
		}
		
		else if (command.startsWith("Dwarven"))
		{
			races = Races.DWARVEN;

		}
		else if (command.startsWith("BackMainSkin"))
		{
			player.setCustomClassSkin(-1);
			player.setCustomRaceSkin(-1);
		}
		setRaceCustomSkin(player, races);
		
		player.broadcastUserInfo();
		player.broadcastUserInfo();

	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/ChangeSkin/" + filename + ".htm";
	}
	
	private static void setRaceCustomSkin(L2PcInstance player, Races valor)
	{
		if ((player == null) || (valor == null)) 
		{
			return;
		}
		player.setCustomRaceSkin(valor.getRaceId());
		player.setCustomClassSkin(valor.getClassId());
	}
}
