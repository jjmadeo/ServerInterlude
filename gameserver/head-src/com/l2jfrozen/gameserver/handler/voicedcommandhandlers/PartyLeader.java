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
package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2CommandChannel;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.event.CTF;
import com.l2jfrozen.gameserver.model.entity.event.DM;
import com.l2jfrozen.gameserver.model.entity.event.TvT;
import com.l2jfrozen.gameserver.model.entity.event.VIP;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jfrozen.gameserver.network.serverpackets.RadarControl;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.util.Util;

/**
 * @author Juan
 *
 */
public class PartyLeader implements IVoicedCommandHandler 
{
	private static final Logger LOGGER = Logger.getLogger(PartyLeader.class);
	
	private  final int  ITEM_FOR_SERVICE = 9522;
	
	private static String[] _voicedCommands =
	{
		"pgo",
		"ploc",
		"passist"
	};


	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		
		command = command.toLowerCase();	
		
		switch (command)
		{
			case "pgo":
				PartyGo(activeChar);

				break;
				
			case "ploc":
				PartyLoc(activeChar);	
				break;
							
			case "passist":
				PartyAssist(activeChar);	
				break;
			
			default:
				return false;
				
		}
		
		
		
		return true;
	}

	
	/**
	 * @param activeChar
	 */
	private void PartyAssist(L2PcInstance activeChar)
	{
		L2Party pp = activeChar.getParty();	
		
		
		
		if(pp==null || !pp.getLeader().equals(activeChar))
			return;
		
		for (L2PcInstance pm : pp.getPartyMembers())
		{	
			pm.setTarget(activeChar.getTarget());	
			
		}
		
		
		
	}


	/**
	 * @param activeChar
	 */
	private void PartyLoc(L2PcInstance activeChar)
	{
		
		activeChar.setPlocCaptureLocation(true);
		
	}


	/**
	 * @param activeChar
	 */
	private void PartyGo(L2PcInstance activeChar)
	{
		
		
		if (!(activeChar instanceof L2PcInstance)) // currently not implemented for others
			return;
		
		L2PcInstance activePlayer = activeChar;
		
		if (!L2PcInstance.checkSummonerStatus(activePlayer))
			return;
		
		if (activePlayer.isInOlympiadMode())
		{
			activePlayer.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
			return;
		}
		
		if (activePlayer._inEvent)
		{
			activePlayer.sendMessage("You cannot use this skill in Event.");
			return;
		}
		if (activePlayer._inEventCTF && CTF.is_started())
		{
			activePlayer.sendMessage("You cannot use this skill in Event.");
			return;
		}
		if (activePlayer._inEventDM && DM.is_started())
		{
			activePlayer.sendMessage("You cannot use this skill in Event.");
			return;
		}
		if (activePlayer._inEventTvT && TvT.is_started())
		{
			activePlayer.sendMessage("You cannot use this skill in Event.");
			return;
		}
		if (activePlayer._inEventVIP && VIP._started)
		{
			activePlayer.sendMessage("You cannot use this skill in Event.");
			return;
		}
		
		// Checks summoner not in siege zone
		if (activeChar.isInsideZone(L2Character.ZONE_SIEGE))
		{
			((L2PcInstance) activeChar).sendMessage("You cannot summon in siege zone.");
			return;
		}
		
		// Checks summoner not in arenas, siege zones, jail
		if (activePlayer.isInsideZone(L2Character.ZONE_PVP))
		{
			activePlayer.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT));
			return;
		}
		
		if (GrandBossManager.getInstance().getZone(activePlayer) != null && !activePlayer.isGM())
		{
			activePlayer.sendPacket(new SystemMessage(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION));
			return;
		}
		
		try
		{
			L2Party pp  = activeChar.getParty();
			if(pp == null  || !pp.getLeader().equals(activePlayer))
				return;
				
			for (final L2Object target1 : pp.getPartyMembers() )
			{
				if (!(target1 instanceof L2Character))
					continue;
				
				L2Character target = (L2Character) target1;
				if (activeChar == target)
					continue;
				
				if (target instanceof L2PcInstance)
				{
					L2PcInstance targetChar = (L2PcInstance) target;
					
					if (!L2PcInstance.checkSummonTargetStatus(targetChar, activePlayer))
						continue;
					
					if (targetChar.isAlikeDead())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					
					if (targetChar._inEvent)
					{
						targetChar.sendMessage("You cannot use this skill in a Event.");
						return;
					}
					if (targetChar._inEventCTF)
					{
						targetChar.sendMessage("You cannot use this skill in a Event.");
						return;
					}
					if (targetChar._inEventDM)
					{
						targetChar.sendMessage("You cannot use this skill in a Event.");
						return;
					}
					if (targetChar._inEventTvT)
					{
						targetChar.sendMessage("You cannot use this skill in a Event.");
						return;
					}
					if (targetChar._inEventVIP)
					{
						targetChar.sendMessage("You cannot use this skill in a Event.");
						return;
					}
					
					if (targetChar.isInStoreMode())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					
					// Target cannot be in combat (or dead, but that's checked by TARGET_PARTY)
					if (targetChar.isRooted() || targetChar.isInCombat())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED);
						sm.addString(targetChar.getName());
						activeChar.sendPacket(sm);
						sm = null;
						continue;
					}
					
					if (GrandBossManager.getInstance().getZone(targetChar) != null && !targetChar.isGM())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}
					// Check for the the target's festival status
					if (targetChar.isInOlympiadMode())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD));
						continue;
					}
					
					// Check for the the target's festival status
					if (targetChar.isFestivalParticipant())
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}
					
					// Check for the target's jail status, arenas and siege zones
					if (targetChar.isInsideZone(L2Character.ZONE_PVP))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING));
						continue;
					}
					
					activePlayer = null;
					
					// Requires a Summoning Crystal
					/* if (targetChar.getInventory().getItemByItemId(8615) == null) */
					if ((targetChar.getInventory().getItemByItemId(ITEM_FOR_SERVICE) == null)) // KidZor
					{
						((L2PcInstance) activeChar).sendMessage("El Tarjet No tiene DC");
						targetChar.sendMessage("No tienes DC");
						continue;
					}
					
					if (!Util.checkIfInRange(0, activeChar, target, false))
					{
						// Check already summon
						if (!targetChar.teleportRequest(activeChar, null))
						{
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_SUMMONED);
							sm.addString(target.getName());
							activeChar.sendPacket(sm);
							continue;
						}
						
						
							// Send message
							final ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
							confirm.addString("Leader Party Summon"+activeChar.getName());
							confirm.addZoneName(activeChar.getX(), activeChar.getY(), activeChar.getZ());
							confirm.addTime(30000);
							confirm.addRequesterId(activeChar.getObjectId());
							targetChar.sendPacket(confirm);
						
					}
					
					target = null;
					targetChar = null;
				}
			}
		}
		catch (final Throwable e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
		}
		
	}


	@Override
	public String[] getVoicedCommandList()
	{
		
		return _voicedCommands;
	}
	
}
