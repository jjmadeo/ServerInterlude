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
package com.l2jfrozen.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.model.L2CommandChannel;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.L2Radar;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.Location;
import com.l2jfrozen.gameserver.model.L2Radar.RadarOnPlayer;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfrozen.gameserver.model.actor.position.L2CharPosition;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.EnchantResult;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.network.serverpackets.RadarControl;
import com.l2jfrozen.gameserver.network.serverpackets.StopMove;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.TaskPriority;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.util.IllegalPlayerAction;
import com.l2jfrozen.gameserver.util.Util;
import com.l2jfrozen.util.random.Rnd;

@SuppressWarnings("unused")
public class MoveBackwardToLocation extends L2GameClientPacket
{
	private static final Logger LOGGER = Logger.getLogger(MoveBackwardToLocation.class);

	private int _targetX, _targetY, _targetZ, _originX, _originY, _originZ, _moveMovement;
	private int _curX, _curY, _curZ; // for geodata
	private L2Spawn _npcSpawn;
		
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}
	
	@Override
	protected void readImpl()
	{
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		
		try
		{
			_moveMovement = readD(); // is 0 if cursor keys are used 1 if mouse is used
		}
		catch (final BufferUnderflowException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			// Ignore for now
			if (Config.L2WALKER_PROTEC)
			{
				final L2PcInstance activeChar = getClient().getActiveChar();
				activeChar.sendPacket(SystemMessageId.HACKING_TOOL);
				Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " trying to use L2Walker!", IllegalPlayerAction.PUNISH_KICK);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		// Move flood protection
		if (!getClient().getFloodProtectors().getMoveAction().tryPerformAction("MoveBackwardToLocation"))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Like L2OFF movements prohibited when char is sitting
		if (activeChar.isSitting())
		{
			getClient().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Like L2OFF movements prohibited when char is teleporting
		if (activeChar.isTeleporting())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Like L2OFF the enchant window will close
		if (activeChar.getActiveEnchantItem() != null)
		{
			activeChar.sendPacket(new EnchantResult(0));
			activeChar.setActiveEnchantItem(null);
		}
		
		if (_targetX == _originX && _targetY == _originY && _targetZ == _originZ)
		{
			activeChar.sendPacket(new StopMove(activeChar));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.setLocDestino(_targetX, _targetY, _targetZ);
		
		if(activeChar.getPlocCaptureLocation()) {
			plocGoLoc(activeChar,activeChar.getLocDestino());
			activeChar.setPlocCaptureLocation(false);
		}
		

		/*
		 * // Correcting targetZ from floor level to head level (?) // Client is giving floor level as targetZ but that floor level doesn't // match our current geodata and teleport coords as good as head level! // L2J uses floor, not head level as char coordinates. This is some // sort of
		 * incompatibility fix. // Validate position packets sends head level. _targetZ += activeChar.getTemplate().collisionHeight;
		 */
		
		_curX = activeChar.getX();
		_curY = activeChar.getY();
		_curZ = activeChar.getZ();
		
		if (activeChar.getTeleMode() > 0)
		{
			if (activeChar.getTeleMode() == 1)
				activeChar.setTeleMode(0);
			
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.teleToLocation(_targetX, _targetY, _targetZ, false);
			return;
		}
		
		if (_moveMovement == 0 && !Config.ALLOW_USE_CURSOR_FOR_WALK)
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		else
		{
			final double dx = _targetX - _curX;
			final double dy = _targetY - _curY;
			
			// Can't move if character is confused, or trying to move a huge distance
			if (activeChar.isOutOfControl() || dx * dx + dy * dy > 98010000) // 9900*9900
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// This is to avoid exploit with Hit + Fast movement
			if ((activeChar.isMoving() && activeChar.isAttackingNow()))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(_targetX, _targetY, _targetZ, 0));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 01 MoveBackwardToLoc";
	}
	
	
	
	public static void plocGoLoc(final L2PcInstance active, Location loc)	{
		
		L2Party pp = active.getParty();		
		L2CommandChannel cc = null;
		if(pp==null)
			return;
		
		 //cc = pp.getCommandChannel();
		
//		if(cc == null) {
		if(true) {
			
			if(!pp.getLeader().equals(active) )
				return;
			
			for (L2PcInstance pm : pp.getPartyMembers())
			{	
				pm.sendPacket(new RadarControl(0, 1, loc.getX(), loc.getY(), loc.getZ()));	
				
			}
			
			
			
		}
//		else {
//			
//			if(!cc.getChannelLeader().equals(active) && !pp.getLeader().equals(active) )
//				return;
//			
//			for (L2Party pm : cc.getPartys())
//			{	
//				for (L2PcInstance pmcc : pm.getPartyMembers())
//				{	
//					pmcc.sendPacket(new RadarControl(0, 1, loc.getX(), loc.getY(), loc.getZ()));	
//					
//				}	
//				
//			}
//			
//			
//		}
			
			
		
		
		
		
		
		
						
					
		
	}
	
}