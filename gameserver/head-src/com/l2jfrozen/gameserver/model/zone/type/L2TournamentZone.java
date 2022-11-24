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
package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;

public class L2TournamentZone extends L2ZoneType
{
	public L2TournamentZone(int id) 
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PEACE, true);
		character.setInsideZone(L2Character.TOURNAMENT, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance player = (L2PcInstance) character;
			player.setTournamentTeleport(true);
			if (!player.isOlympiadProtection())
			{
				player.sendMessage("You have entered a Tournament zone.");
				//player.getAppearance().setInvisible();
				player.broadcastUserInfo();
			}
			
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.TOURNAMENT, false);
		character.setInsideZone(L2Character.ZONE_PEACE, false);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance player = (L2PcInstance) character;
			
			if (!player.isArenaProtection() && !player.isOlympiadProtection())
			{
				player.setTournamentTeleport(false);
				
				if (!player.inObserverMode() && !player.isOlympiadProtection() && !player.isGM())
					player.getAppearance().setVisible();
				
				/*
				if (!player.inObserverMode())
				{
					player._player_n = 1;
					player.sendMessage("Tournament: You have been removed from the waiting list!");
				}
				 */
				
				player.sendMessage("You left a Tournament zone.");
				player.broadcastUserInfo();
			}
			
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
}