/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.GmListTable;
import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.database.L2DatabaseFactory;

public class AdminDonator implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS =
	{
		"admin_setdonator"
	};
	
	protected static final Logger LOGGER = Logger.getLogger(AdminDonator.class);
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		/*
		 * if(!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel())){ return false; } if(Config.GMAUDIT) { Logger _logAudit = Logger.getLogger("gmaudit"); LogRecord record = new LogRecord(Level.INFO, command); record.setParameters(new Object[] { "GM: " +
		 * activeChar.getName(), " to target [" + activeChar.getTarget() + "] " }); _logAudit.LOGGER(record); }
		 */
		
		if (activeChar == null)
			return false;
		
		if (command.startsWith("admin_setdonator"))
		{
			
			final StringTokenizer st = new StringTokenizer(command);
	
	
				boolean no_token = false;
				
				if (st.hasMoreTokens())
				{ // char_name not specified
				
					final String char_name = st.nextToken();
					
					final L2PcInstance player = L2World.getInstance().getPlayer(char_name);
					
					if (player != null)
					{
						
						if (st.hasMoreTokens()) // time
						{
							final String time = st.nextToken();
							
							try
							{
								final int value = Integer.parseInt(time);
								
								if (value > 0)
								{
									
									doDonator(activeChar, player, char_name, time);
									
									if (player.isAio())
										return true;
									
								}
								else
								{
									activeChar.sendMessage("Time must be bigger then 0!");
									return false;
								}
								
							}
							catch (final NumberFormatException e)
							{
								activeChar.sendMessage("Time must be a number!");
								return false;
							}
							
						}
						else
						{
							no_token = true;
						}
						
					}
					else
					{
						activeChar.sendMessage("Player must be online to set AIO status");
						no_token = true;
					}
					
				}
				else
				{
					
					no_token = true;
					
				}
				
				if (no_token)
				{
					activeChar.sendMessage("Usage: //setdonator <char_name> [time](in days)");
					return false;
				}
				
			
			
			
			
//			
//			
//			
//			
//			L2Object target = activeChar.getTarget();
//			
//			if (target instanceof L2PcInstance)
//			{
//				L2PcInstance targetPlayer = (L2PcInstance) target;
//				final boolean newDonator = !targetPlayer.isDonator();
//				
//				if (newDonator)
//				{
//					targetPlayer.setDonator(true);
//					targetPlayer.updateNameTitleColor();
//					updateDatabase(targetPlayer, true);
//					sendMessages(true, targetPlayer, activeChar, false, true);
//					targetPlayer.broadcastPacket(new SocialAction(targetPlayer.getObjectId(), 16));
//					targetPlayer.broadcastUserInfo();
//				}
//				else
//				{
//					targetPlayer.setDonator(false);
//					targetPlayer.updateNameTitleColor();
//					updateDatabase(targetPlayer, false);
//					sendMessages(false, targetPlayer, activeChar, false, true);
//					targetPlayer.broadcastUserInfo();
//				}
//				
//				targetPlayer = null;
//			}
//			else
//			{
//				activeChar.sendMessage("Impossible to set a non Player Target as Donator.");
//				LOGGER.info("GM: " + activeChar.getName() + " is trying to set a non Player Target as Donator.");
//				
//				return false;
//			}
//			
//			target = null;
		}
		return true;
	}
	
	private void sendMessages(final boolean forNewDonator, final L2PcInstance player, final L2PcInstance gm, final boolean announce, final boolean notifyGmList)
	{
		if (forNewDonator)
		{
			player.sendMessage(gm.getName() + " has granted Donator Status for you!");
			gm.sendMessage("You've granted Donator Status for " + player.getName());
			
			if (announce)
			{
				Announcements.getInstance().announceToAll(player.getName() + " has received Donator Status!");
			}
			
			if (notifyGmList)
			{
				GmListTable.broadcastMessageToGMs("Warn: " + gm.getName() + " has set " + player.getName() + " as Donator !");
			}
		}
		else
		{
			player.sendMessage(gm.getName() + " has revoked Donator Status from you!");
			gm.sendMessage("You've revoked Donator Status from " + player.getName());
			
			if (announce)
			{
				Announcements.getInstance().announceToAll(player.getName() + " has lost Donator Status!");
			}
			
			if (notifyGmList)
			{
				GmListTable.broadcastMessageToGMs("Warn: " + gm.getName() + " has removed Donator Status of player" + player.getName());
			}
		}
	}
	
	/**
	 * @param player
	 * @param newDonator
	 */
	private void updateDatabase(final L2PcInstance player, final boolean newDonator)
	{
		Connection con = null;
		try
		{
			// prevents any NPE.
			// ----------------
			if (player == null)
				return;
			
			// Database Connection
			// --------------------------------
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement stmt = con.prepareStatement(newDonator ? INSERT_DATA : DEL_DATA);
			
			// if it is a new donator insert proper data
			// --------------------------------------------
			if (newDonator)
			{
				stmt.setInt(1, player.getObjectId());
				stmt.setString(2, player.getName());
				stmt.setInt(3, player.isHero() ? 1 : 0);
				stmt.setInt(4, player.isNoble() ? 1 : 0);
				stmt.setInt(5, 1);
				stmt.execute();
				stmt.close();
				stmt = null;
			}
			else
			// deletes from database
			{
				stmt.setInt(1, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;
			}
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			LOGGER.error("Error: could not update database: ", e);
		}
		finally
		{
			CloseUtil.close(con);
		}
	}
	
	public void doDonator(final L2PcInstance activeChar, final L2PcInstance _player, final String _playername, final String _time)
	{
		final int days = Integer.parseInt(_time);
		if (_player == null)
		{
			activeChar.sendMessage("not found char" + _playername);
			return;
		}
		
		if (days > 0)
		{
			_player.setDonator(true);
			_player.setEndTime("donator", days);			
			Connection connection = null;
			try
			{
				connection = L2DatabaseFactory.getInstance().getConnection(false);
				
				final PreparedStatement statement = connection.prepareStatement("REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator,donator_end_date) VALUES (?,?,?,?,?,?)");
				statement.setInt(1, _player.getObjectId());
				statement.setString(2, _player.getName());
				statement.setInt(3, _player.isHero() ? 1 : 0);
				statement.setInt(4,_player.isNoble() ? 1 : 0);
				statement.setInt(5, 1);
				statement.setLong(6, _player.getDonator_end_date());
				statement.execute();
				statement.close();
				DatabaseUtils.close(statement);
				connection.close();			
								
				_player.broadcastUserInfo();
				_player.sendPacket(new EtcStatusUpdate(_player));
				_player.sendMessage("Felicidades ya eres vip durante =>"+_time);
				_player.broadcastUserInfo();
			}
			catch (final Exception e)
			{
				if (Config.DEBUG)
					e.printStackTrace();
				
				LOGGER.warn("could not set VIP  stats to char:", e);
			}
			finally
			{
				CloseUtil.close(connection);
			}
		}
		else
		{
			removeDonator(activeChar, _player, _playername);
		}
	}
	
	public void removeDonator(final L2PcInstance activeChar, final L2PcInstance _player, final String _playername)
	{
		_player.setDonator(false);
		_player.setDonator_end_date(0);
		
		Connection connection = null;
		try
		{
			connection = L2DatabaseFactory.getInstance().getConnection(false);
			
			final PreparedStatement statement = connection.prepareStatement("UPDATE characters_custom_data SET donator=0, donator_end_date=0 WHERE obj_id=?");
			statement.setInt(1, _player.getObjectId());
			statement.execute();
			DatabaseUtils.close(statement);
			connection.close();
			
			_player.broadcastUserInfo();
			_player.sendPacket(new EtcStatusUpdate(_player));
			_player.sendMessage("Tu estado de VIP ah finalizado.");
			_player.broadcastUserInfo();
		}
		catch (final Exception e)
		{
			if (Config.DEBUG)
				e.printStackTrace();
			
			LOGGER.warn("could not remove VIP stats of char:", e);
		}
		finally
		{
			CloseUtil.close(connection);
		}
	}
	
	
	
	
	// Updates That Will be Executed by MySQL
	// ----------------------------------------
	String INSERT_DATA = "REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator) VALUES (?,?,?,?,?)";
	String DEL_DATA = "UPDATE characters_custom_data SET donator = 0 WHERE obj_Id=?";
	
	/**
	 * @return
	 */
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
