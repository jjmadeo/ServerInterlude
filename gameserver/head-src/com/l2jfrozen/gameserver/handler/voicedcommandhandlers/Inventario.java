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
/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
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

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.clientpackets.L2GameClientPacket;
import com.l2jfrozen.gameserver.network.serverpackets.GMViewCharacterInfo;
import com.l2jfrozen.gameserver.network.serverpackets.GMViewItemList;
import com.l2jfrozen.gameserver.network.serverpackets.GMViewSkillInfo;

public class Inventario  implements IVoicedCommandHandler
{
	private static String[] _voicedCommands =
	{
		"inventario",
		"estado",
		"skills"
		
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		

		final L2PcInstance player = (L2PcInstance) activeChar.getTarget();	
		
		if(activeChar.getTarget() instanceof L2PcInstance && !player.equals(activeChar)) {
			
			switch (command)
			{
				case "inventario":
					activeChar.sendPacket(new GMViewItemList(player));

					break;
				case "estado":
					activeChar.sendPacket(new GMViewCharacterInfo(player));

					break;
				case "skills":
					activeChar.sendPacket(new GMViewSkillInfo(player));
					break;
				
				default:
					break;
			}

		}else {
			
			activeChar.sendMessage("El target es incorrecto.");
			
			return false;
		}
			
		
		
		
		
		
		

		
		
		
		
		return true;
		
	}
	
	/**
	 * @see com.l2jfrozen.gameserver.handler.IVoicedCommandHandler#getVoicedCommandList()
	 */
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}

	
}
