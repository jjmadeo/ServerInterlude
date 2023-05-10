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
public class L2ItemExistInstance extends L2NpcInstance
{

	/**
	 * @param objectId
	 * @param template
	 */


	public L2ItemExistInstance(int objectId, L2NpcTemplate template)
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
			try {
				showMessageWindow(player);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}


	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{

	}



	public void showMessageWindow(L2PcInstance player) throws Exception {


		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II </title></head><body>");
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
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">Items en Circulacion</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Revisa los items en circulacion</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<table>");

		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\">");
		tb.append("<table>");
		tb.append("<tr>");
		tb.append("<td width=40><button action=\"bypass -h npc_%objectId%_Chat 5\" width=32 height=32 back=\"icon.etc_pig_adena_i01\" fore=\"icon.etc_pig_adena_i01\"></td>");
		tb.append("<td width=220>");
		tb.append("<table>");
		tb.append("<tr><td>Donate Coin</td></tr>");
		tb.append(String.format("<tr><td><font color=\"ae9977\">Cantidad en circulacion:</font><font color=\"27ae60\">%s</font></td></tr>",gettotalDonate()));
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");


		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\">");
		tb.append("<table>");
		tb.append("<tr>");
		tb.append("<td width=40><button action=\"bypass -h npc_%objectId%_Chat 5\" width=32 height=32 back=\"l2gaia2.gaia_tvt\" fore=\"l2gaia2.gaia_tvt\"></td>");
		tb.append("<td width=220>");
		tb.append("<table>");
		tb.append("<tr><td>TvT</td></tr>");
		tb.append(String.format("<tr><td><font color=\"ae9977\">Cantidad en circulacion:</font><font color=\"27ae60\">%s</font></td></tr>",gettotaltvt()));
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");


		tb.append("<tr>");
		tb.append("<td align=\"left\" width=\"300\">");
		tb.append("<table>");
		tb.append("<tr>");
		tb.append("<td width=40><button action=\"bypass -h npc_%objectId%_Chat 5\" width=32 height=32 back=\"icon.etc_coda_i07\" fore=\"icon.etc_coda_i07\"></td>");
		tb.append("<td width=220>");
		tb.append("<table>");
		tb.append("<tr><td>Farm</td></tr>");
		tb.append(String.format("<tr><td><font color=\"ae9977\">Cantidad en circulacion:</font><font color=\"27ae60\">%s</font></td></tr>",gettotalfarm()));
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("</td>");
		tb.append("</tr>");



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


	private String gettotalDonate() throws Exception {


		String val = (String) CacheManager.getInstance().get("dcTotal");

		if(val == null ){
			val = ObtenerRank("dc");
			CacheManager.getInstance().put("dcTotal",val);

		}


		return val;
	}

	private String gettotaltvt() throws Exception {


		String val = (String) CacheManager.getInstance().get("tvtTotal");

		if(val == null ){
			val = ObtenerRank("tvt");
			CacheManager.getInstance().put("tvtTotal",val);

		}


		return val;
	}
	private String gettotalfarm() throws Exception {


		String val = (String) CacheManager.getInstance().get("farmTotal");

		if(val == null ){
			val = ObtenerRank("farm");
			CacheManager.getInstance().put("farmTotal",val);

		}


		return val;
	}






	private String ObtenerRank(String rankType) throws Exception {



		String  dc="SELECT sum(count) as 'Donate Coin' FROM gameserver.items i inner join gameserver.characters c on c.obj_Id = i.owner_id  where c.accesslevel = 0 and i.item_id = 9522";
		String  tvt="SELECT sum(count) as 'TvT' FROM gameserver.items i inner join gameserver.characters c on c.obj_Id = i.owner_id  where c.accesslevel = 0 and i.item_id = 9516";
		String  farm="SELECT sum(count) as 'Farm' FROM gameserver.items i inner join gameserver.characters c on c.obj_Id = i.owner_id  where c.accesslevel = 0 and i.item_id = 9530";
		String sql = "";
		long result = 0;
		Connection con = null;

		switch (rankType){
			case "dc":
				sql =dc;
				break;
			case "tvt":
				sql =tvt;
				break;
			case "farm":
				sql =farm;
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

			
				if (rset.next()){
					result = rset.getLong(1);
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

		return String.valueOf(result);

	}




}

