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

import java.util.ArrayList;
import java.util.List;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.datatables.xml.ExperienceData;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.ItemList;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;


import javolution.text.TextBuilder;

/**
 * @author Juan
 *
 */
public class L2BetaInstance extends L2NpcInstance
{

	/**
	 * @param objectId
	 * @param template
	 */
	
	public static final int CRYSTAL_NONE = 0x00; // ??
	public static final int CRYSTAL_D = 0x01; // ??
	public static final int CRYSTAL_C = 0x02; // ??
	public static final int CRYSTAL_B = 0x03; // ??
	public static final int CRYSTAL_A = 0x04; // ??
	public static final int CRYSTAL_S = 0x05; // ??
	
	
	public L2BetaInstance(int objectId, L2NpcTemplate template)
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
		if (command.startsWith("lvlup")) 
		{
			if (player instanceof L2PlayableInstance && player.getStat().getLevel()<=80)
			{
				manageLevel(player, player.getStat().getLevel()+1);
			}
		}
		if (command.startsWith("lvldown")) 
		{
			if (player instanceof L2PlayableInstance)
			{
				if (player instanceof L2PlayableInstance && player.getStat().getLevel()>1 )
				{
					manageLevel(player, player.getStat().getLevel()-1);
				}
			}
		}
		
		if (command.startsWith("lvlup10")) 
		{
			if (player instanceof L2PlayableInstance && player.getStat().getLevel()<=70)
			{
				manageLevel(player, player.getStat().getLevel()+10);
			}
		}
		if (command.startsWith("lvldown10")) 
		{
			if (player instanceof L2PlayableInstance)
			{
				if (player instanceof L2PlayableInstance && player.getStat().getLevel()>11 )
				{
					manageLevel(player, player.getStat().getLevel()-10);
				}
			}
		}
		
		if (command.startsWith("getBetaPack")) 
		{
			createItem(player,player, 57, 2000000000);
			createItem(player,player, 9516, 20000000);
			createItem(player,player, 9530, 99000000);
			createItem(player,player, 9522, 99000000);
			createItem(player,player, 9513, 20000000);
			createItem(player,player, 9514, 99000000);
			createItem(player,player, 9521, 99000000);
			
//			createItem(player,player, 9523, 20);
			
			//epicas
			createItem(player,player, 6660, 1);
			createItem(player,player, 6658, 1);
			createItem(player,player, 6656, 1);
			createItem(player,player, 6659, 1);
			createItem(player,player, 6657, 1);
			
			
			createItem(player,player, 9524, 1);
			createItem(player,player, 9525, 1);
			createItem(player,player, 9526, 1);
			createItem(player,player, 9527, 1);
			createItem(player,player, 9528, 1);
			createItem(player,player, 9529, 1);
			
			
			
		}if (command.startsWith("todoMas3")) 
		{
			
			for (L2ItemInstance item : player.getInventory().getItems())
			{
				if(item.getItem().getItemGrade()!= CRYSTAL_NONE) {
					
					if(item.getEnchantLevel()<3)
						item.setEnchantLevel(3);
						item.updateDatabase();
					
				}
				
			}
			
			
			
			
			
		}
		if (command.startsWith("adena")) 
		{			
			createItem(player,player, 57, 2000000000);
				
			
		}
		
		
	}
	
	
	
	public void showMessageWindow(L2PcInstance player)
	{
			
	
		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II - Beta Panel</title></head><body>");
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
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">Torneo PvP</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Participa por Recompensas</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");	
		tb.append("<table>");		
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_lvlup\">Subir 1 Nivel</a></font></td></tr>");		
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_lvldown\">Bajar 1 Nivel</a></font></td></tr>");	
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_lvlup10\">Subir 10 Nivel</a></font></td></tr>");		
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_lvldown10\">Bajar 10 Nivel</a></font></td></tr>");
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_getBetaPack\">Obtener Beta Pack</a></font></td></tr>");
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_todoMas3\">Subime Todo +3</a></font></td></tr>");
			tb.append("<tr><td align=center><font color=\"227093\"><a action=\"bypass -h npc_%objectId%_adena\">DAME ADENA</a></font></td></tr>");
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
	
	private void createItem(final L2PcInstance activeChar,L2PcInstance target ,final int id, final int num)
	{
		if (num > 20)
		{
			L2Item template = ItemTable.getInstance().getTemplate(id);
			
			if (template != null && !template.isStackable())
			{
				activeChar.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
			
			template = null;
		}
		
		L2PcInstance Player = target;
		
		
		
		if (Player == null)
		{
			activeChar.setTarget(activeChar);
			Player = activeChar;
		}
		
		Player.getInventory().addItem("Admin", id, num, Player, null);
		ItemList il = new ItemList(Player, true);
		Player.sendPacket(il);
	
		
		Player = null;
		il = null;
	}
	
	 private boolean manageLevel(L2PcInstance targetChar,int val) {
		 try
			{
				
				
				final L2PlayableInstance targetPlayer = (L2PlayableInstance) targetChar;
				
				final byte lvl = (byte) val;
				int max_level = ExperienceData.getInstance().getMaxLevel();
				
				if (targetChar instanceof L2PcInstance && ((L2PcInstance) targetPlayer).isSubClassActive())
				{
					max_level = Config.MAX_SUBCLASS_LEVEL;
				}
				
				if (lvl >= 1 && lvl <= max_level)
				{
					final long pXp = targetPlayer.getStat().getExp();
					final long tXp = ExperienceData.getInstance().getExpForLevel(lvl);
					
					if (pXp > tXp)
					{
						targetPlayer.getStat().removeExpAndSp(pXp - tXp, 0);
					}
					else if (pXp < tXp)
					{
						targetPlayer.getStat().addExpAndSp(tXp - pXp, 0);
					}
				}
				else
				{
					targetChar.sendMessage("You must specify level between 1 and " + ExperienceData.getInstance().getMaxLevel() + ".");
					return false;
				}
			}
			catch (final NumberFormatException e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
				targetChar.sendMessage("You must specify level between 1 and " + ExperienceData.getInstance().getMaxLevel() + ".");
				return false;
			}
		return true;
	 }
	
	
	
	
}
