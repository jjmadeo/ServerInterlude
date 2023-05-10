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

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.datatables.xml.ExperienceData;
import com.l2jfrozen.gameserver.network.serverpackets.*;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.util.CacheManager;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import javolution.text.TextBuilder;
import org.python.parser.ast.Str;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan
 *
 */
public class L2TopInstance extends L2NpcInstance
{

	/**
	 * @param objectId
	 * @param template
	 */


	public L2TopInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void onAction(L2PcInstance player)
	{
		if (this != player.getTarget())
		{
			player.setTarget(this);
			
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
			
			player.sendPacket(new ValidateLocation(this));
		}
		else if (!canInteract(player))
		{
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
		}
		else
		{
			showMessageWindow(player);
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		
		LOGGER.info("Commando Bypass: "+command + "--Player :"+player.getName());

		if (command.startsWith("tpvp"))
		{

		}
		if (command.startsWith("tpk"))
		{

		}
		
		if (command.startsWith("tclan"))
		{

		}

		
		
	}
	
	
	
	public void showMessageWindow(L2PcInstance player)
	{
			
	
		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II - Rank Panel</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table bgcolor=000000 width=300 height=40>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Welcome " + player.getName() + " in Lineage II</font><font color=\"FF9900\"> - Legends-</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">Rank </font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Participa por Recompensas</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");	
		tb.append("<table>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"FF5555\">Top 3 [PvP]</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\"><font color=\"FF5555\">#N°--Nombre--Score--Clan</font></td>");
		tb.append("</tr>");
		try {
			tb.append(getPvPRank());
		} catch (Exception e) {
			throw new RuntimeException("e");
		}
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"FF5555\">Top 3 [PK]</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\"><font color=\"FF5555\">#N°--Nombre--Score--Clan</font></td>");
		tb.append("</tr>");
		try {
			tb.append(getPKRank());
		} catch (Exception e) {
			throw new RuntimeException("e");
		}
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"FF5555\">Top 3 [CLAN]</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\"><font color=\"FF5555\">#N°--Nombre--Score</font></td>");
		tb.append("</tr>");

		try {
			tb.append(getClanRank());
		} catch (Exception e) {
			throw new RuntimeException("e");
		}
		tb.append("</table>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> URL WEB</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("</body></html>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(this.getObjectId()));
		
		player.sendPacket(msg);
	}
	

	private String getPvPRank() throws Exception {
		TextBuilder tb = new TextBuilder();

		List<String> rows = (List<String>) CacheManager.getInstance().get("pvpRank");

		if(rows == null ){
			rows = ObtenerRank("pvp");
			CacheManager.getInstance().put("pvpRank",rows);

		}

		for (String row : rows){
			tb.append(row);
		}
		return tb.toString();
	}

	private String getPKRank() throws Exception {
		TextBuilder tb = new TextBuilder();

		List<String> rows = (List<String>) CacheManager.getInstance().get("pkRank");

		if(rows == null ){
			rows = ObtenerRank("pk");
			CacheManager.getInstance().put("pkRank",rows);

		}

		for (String row : rows){
			tb.append(row);
		}
		return tb.toString();
	}
	private String getClanRank() throws Exception {

		TextBuilder tb = new TextBuilder();

		List<String> rows = (List<String>) CacheManager.getInstance().get("clanRank");

		if(rows == null ){
			rows = ObtenerRank("clan");
			CacheManager.getInstance().put("clanRank",rows);

		}

		for (String row : rows){
			tb.append(row);

		}
		return tb.toString();
	}






	private List<String> ObtenerRank(String rankType) throws Exception {




		String  pvp=" SELECT     f.char_name,     f.pvpkills,     COALESCE((SELECT c.clan_name FROM gameserver.clan_data as c WHERE c.clan_id = f.clanid), 'Sin Clan') as nombreClan FROM     gameserver.characters as f ORDER BY     pvpkills DESC LIMIT 3;";
		String  pk="SELECT     f.char_name,     f.pkkills,     COALESCE((SELECT c.clan_name FROM gameserver.clan_data as c WHERE c.clan_id = f.clanid), 'Sin Clan') as nombreClan FROM     gameserver.characters as f ORDER BY     pkkills DESC LIMIT 3;";
		String  clan=" SELECT     b.clan_name,     SUM(a.pvpkills) as Score FROM     gameserver.characters a,     gameserver.clan_data b WHERE      a.clanid = b.clan_id GROUP BY     b.clan_name ORDER BY     Score DESC LIMIT 3;";
		String sql = "";
		List<String> result = new ArrayList<>();
		Connection con = null;

		switch (rankType){
			case "pvp":
				sql =pvp;
				break;
			case "pk":
				sql =pk;
				break;
			case "clan":
				sql =clan;
				break;

			default:
				throw new Exception("no se especifico el tipo de rank");
		}


		try
		{


			con = L2DatabaseFactory.getInstance().getConnection(false);

			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rset;

			rset = stmt.executeQuery();

			while (rset.next()){

				if (rankType.equals("pvp")){
					result.add(String.format("<tr><td align=\"left\"><font color=\"227093\">#%s--[%s]--[%s]--[%s]</font></td></tr>",
							rset.getRow(), rset.getString(1), rset.getString(2),rset.getString(3)));
				}
				if (rankType.equals("pk")){
					result.add(String.format("<tr><td align=\"left\"><font color=\"227093\">#%s--[%s]--[%s]--[%s]</font></td></tr>",
							rset.getRow(), rset.getString(1), rset.getString(2),rset.getString(3)));
				}
				if (rankType.equals("clan")){
					result.add(String.format("<tr><td align=\"left\"><font color=\"227093\">#%s--[%s]--[%s]</font></td></tr>",
							rset.getRow(), rset.getString(1), rset.getString(2)));
				}



			}



			DatabaseUtils.close(rset);
			DatabaseUtils.close(stmt);



		}
		catch (Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();

			LOGGER.error("Error: al consultar el Rank ", e);
		}
		finally
		{
			CloseUtil.close(con);

		}

		return result;

	}
	
	
	
	
}
