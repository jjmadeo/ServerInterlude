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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jfrozen.util.database.L2DatabaseFactory;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author iAndre
 * @description This npc is a players Ranking npc.
 *
 */
public class L2RankingInstance extends L2NpcInstance
{
 	public L2RankingInstance(int objectId, L2NpcTemplate template)
 	{
 		super(objectId, template);
 	}
 	
 	@Override
 	public void onBypassFeedback(L2PcInstance player, String command)
 	{
 		if (player.isProcessingTransaction())
 		{
 			player.sendPacket(SystemMessageId.ALREADY_TRADING);
 			return;
 		}
 		
 		else if (command.startsWith("topPvp"))
 		{
 			NpcHtmlMessage htm = new NpcHtmlMessage(0);
 			StringBuilder sb = new StringBuilder("<html><body>");
 			sb.append("<center><br>Top 15 PvP:<br><br1>");
 			Connection con = null;
             try
             {
             	con = L2DatabaseFactory.getInstance().getConnection();
             	PreparedStatement stm = con.prepareStatement("SELECT char_name,pvpkills,accesslevel,online FROM characters ORDER BY pvpkills DESC LIMIT 15");
             	ResultSet rSet = stm.executeQuery();
             	while (rSet.next())
             	{
             		int accessLevel = rSet.getInt("accesslevel");
             		if (accessLevel > 0)
             		{
             			continue;
             		}
             		int pvpKills = rSet.getInt("pvpkills");
             		if (pvpKills == 0)
             		{
             			continue;
             		}
             		String pl = rSet.getString("char_name");
             		int online = rSet.getInt("online");
             		String status = online == 1 ? "<font color=\"00FF00\">Online</font>" : "<font color=\"FF0000\">Offline</font>";
             		sb.append("Player: <font color=\"LEVEL\">"+pl+"</font> PvPs: <font color=\"LEVEL\">"+pvpKills+"</font> Status: <font color=\"LEVEL\">"+status+"</font><br1>");
             	}
             }
             catch (Exception e)
             {
             	System.out.println("Error while selecting top 15 pvp from database.");
             }
             finally
             {
             	try
             	{
             		if (con != null)
             			con.close();
             	}
             	catch (Exception e)
             	{}
             }
             sb.append("</body></html>");
             htm.setHtml(sb.toString());
             player.sendPacket(htm);
 		}
 		else if (command.startsWith("topPk"))
 		{
 			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder sb = new StringBuilder("<html><body>");
			sb.append("<center><br>Top 15 PK:<br><br1>");
			Connection con = null;
            try
            {
             	con = L2DatabaseFactory.getInstance().getConnection();
             	PreparedStatement stm = con.prepareStatement("SELECT char_name,pkkills,accesslevel,online FROM characters ORDER BY pkkills DESC LIMIT 15");
             	ResultSet rSet = stm.executeQuery();
             	while (rSet.next())
             	{
             		int accessLevel = rSet.getInt("accesslevel");
             		if (accessLevel > 0)
             		{
             			continue;
             		}
             		int pkKills = rSet.getInt("pkkills");
             		if (pkKills == 0)
             		{
             			continue;
             		}
             		String pl = rSet.getString("char_name");
             		int online = rSet.getInt("online");
             		String status = online == 1 ? "<font color=\"00FF00\">Online</font>" : "<font color=\"FF0000\">Offline</font>";
             		sb.append("Player: <font color=\"LEVEL\">"+pl+"</font> PKs: <font color=\"LEVEL\">"+pkKills+"</font> Status: <font color=\"LEVEL\">"+status+"</font><br1>");
             	}
             }
             catch (Exception e)
             {
             	System.out.println("Error while selecting top 15 pk from database.");
             }
             finally
             {
             	try
             	{
             		if (con != null)
             			con.close();
             	}
             	catch (Exception e)
             	{}
             }
             sb.append("</body></html>");
             htm.setHtml(sb.toString());
             player.sendPacket(htm);
 		}
 	}
 	@Override
 	public void showChatWindow(L2PcInstance player, int val)
 	{
 		player.sendPacket(ActionFailed.STATIC_PACKET);
 		String filename = "data/html/npc/ranking-no.htm";
 		
 		filename = "data/html/npc/ranking.htm";
 		
 		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
 		html.setFile(filename);
 		html.replace("%objectId%", String.valueOf(getObjectId()));
 		html.replace("%playerName%", player.getName());
 		player.sendPacket(html);
 	}
}