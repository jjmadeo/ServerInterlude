package com.l2jfrozen.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javolution.text.TextBuilder;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.communitybbs.Manager.RegionBBSManager;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.datatables.sql.CharNameTable;
import com.l2jfrozen.gameserver.datatables.sql.ClanTable;
import com.l2jfrozen.gameserver.model.Inventory;
import com.l2jfrozen.gameserver.model.L2Clan;
import com.l2jfrozen.gameserver.model.L2ClanMember;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.LeaveWorld;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.PartySmallWindowAll;
import com.l2jfrozen.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import com.l2jfrozen.gameserver.network.serverpackets.PledgeShowMemberListAll;
import com.l2jfrozen.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.PledgeSkillListAdd;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.util.Util;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import com.l2jfrozen.gameserver.model.base.ClassId;

public class L2ServiceInstance extends L2NpcInstance
{
	//private final static int Config.ITEM_ID = 9522;
	String INSERT_DATA = "REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator, hero_end_date) VALUES (?,?,?,?,?,?)";
	String INSERT_DATAA = "REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator,donator_end_date) VALUES (?,?,?,?,?,?)";
	L2Skill arryClanSkill[] =
	{
		SkillTable.getInstance().getInfo(370, 3),
		SkillTable.getInstance().getInfo(371, 3),
		SkillTable.getInstance().getInfo(372, 3),
		SkillTable.getInstance().getInfo(373, 3),
		SkillTable.getInstance().getInfo(374, 3),
		SkillTable.getInstance().getInfo(375, 3),
		SkillTable.getInstance().getInfo(376, 3),
		SkillTable.getInstance().getInfo(377, 3),
		SkillTable.getInstance().getInfo(378, 3),
		SkillTable.getInstance().getInfo(379, 3),
		SkillTable.getInstance().getInfo(380, 3),
		SkillTable.getInstance().getInfo(381, 3),
		SkillTable.getInstance().getInfo(382, 3),
		SkillTable.getInstance().getInfo(383, 3),
		SkillTable.getInstance().getInfo(384, 3),
		SkillTable.getInstance().getInfo(385, 3),
		SkillTable.getInstance().getInfo(386, 3),
		SkillTable.getInstance().getInfo(387, 3),
		SkillTable.getInstance().getInfo(388, 3),
		SkillTable.getInstance().getInfo(389, 3),
		SkillTable.getInstance().getInfo(390, 3),
		SkillTable.getInstance().getInfo(391, 1)
	};
	
	public L2ServiceInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (player.isArenaProtection()){
			player.sendMessage("No puedes operar mientras estas anotado en el Torunament");
			return;
		}


		Races races = null;
		
		if (player == null)
		{
			return;
		}
		else if (command.startsWith("changeSkin"))
		{
			showChangeSkinWindow(player, 0);
		}
		else if (command.startsWith("HumanFighter") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			
			races = Races.FIGHTER;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
		}
		
		else if (command.startsWith("HumanMage")  && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.MAGE;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("ElfFighter") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.ELVENFIGHTER;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("ElfMage") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.ELVENMAGE;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("DarkElfFighter") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.DARKFIGHTER;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("DarkElfMage") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.DARKMAGE;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("OrcFighter") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.ORCFIGHTER;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("OrcMage") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.ORCMAGE;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		
		else if (command.startsWith("Dwarven") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			races = Races.DWARVEN;
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		else if (command.startsWith("BackMainSkin") && validarYCobrar(Config.ITEM_ID, Config.SKIN_CHANGE_PRICE, player))
		{
			player.setCustomClassSkin(-1);
			player.setCustomRaceSkin(-1);
			setRaceCustomSkin(player, races);
			player.broadcastUserInfo();
			
		}
		 else if (command.startsWith("active"))
		 {
		 showActiveWindow(player, 0);
		 }
		 else if (command.startsWith("passive"))
		 {
		 showPassiveWindow(player, 0);
		 }
		 else if (command.startsWith("back"))
		 {
		 showChatWindow(player, 0);
		 }
		 else if (command.startsWith("Refresh"))
		 {
		 addAugment(player, 16287, 3202, 3);
		 }
		 else if (command.startsWith("Ritual"))
		 {
		 addAugment(player, 16183, 3130, 10);
		 }
		 else if (command.startsWith("Heal"))
		 {
		 addAugment(player, 16195, 3123, 10);
		 }
		 else if (command.startsWith("Recharge"))
		 {
		 addAugment(player, 16204, 3127, 10);
		 }
		 else if (command.startsWith("Cheer"))
		 {
		 addAugment(player, 16197, 3131, 10);
		 }
		 else if (command.startsWith("Celestial"))
		 {
		 addAugment(player, 15047, 3158, 1);
		 }
		 else if (command.startsWith("BlessedSoul"))
		 {
		 addAugment(player, 16200, 3128, 10);
		 }
		 else if (command.startsWith("BlessedBody"))
		 {
		 addAugment(player, 16199, 3124, 10);
		 }
		 else if (command.startsWith("Empower"))
		 {
		 addAugment(player, 16281, 3241, 10);
		 }
		 else if (command.startsWith("MagicBarrier"))
		 {
		 addAugment(player, 16282, 3245, 10);
		 }
		 else if (command.startsWith("Might"))
		 {
		 addAugment(player, 16283, 3240, 10);
		 }
		 else if (command.startsWith("Shield"))
		 {
		 addAugment(player, 16284, 3244, 10);
		 }
		 else if (command.startsWith("DuelMight"))
		 {
		 addAugment(player, 16285, 3243, 10);
		 }
		 else if (command.startsWith("Focus"))
		 {
		 addAugment(player, 16333, 3249, 10);
		 }
		 else if (command.startsWith("WildMagic"))
		 {
		 addAugment(player, 16336, 3250, 10);
		 }
		 else if (command.startsWith("Agility"))
		 {
		 addAugment(player, 16332, 3247, 10);
		 }
		 else if (command.startsWith("Guidance"))
		 {
		 addAugment(player, 16335, 3248, 10);
		 }
//		else if (command.startsWith("changeName2"))
//		{
//			showNameWindow(player, 0);
//		}
//		else if (command.startsWith("changeClanName2"))
//		{
//			showClanNameWindow(player, 0);
//		}
//		else if (command.startsWith("changeName"))
//		{
//			String _name = command.substring(11);
//			String errorMsg = null;
//			boolean proceed = true;
//			if (_name.length() < 3)
//			{
//				errorMsg = "Names have to be at least 3 characters";
//				proceed = false;
//				showNameWindow(player, 0);
//			}
//			if (_name.length() > 16)
//			{
//				errorMsg = "Names cannot be longer than 16 characters";
//				proceed = false;
//				showNameWindow(player, 0);
//			}
//			if ((!Util.isAlphaNumeric(_name)) || (!isValidName(_name)))
//			{
//				errorMsg = "Invalid name";
//				proceed = false;
//				showNameWindow(player, 0);
//			}
//			if (CharNameTable.getInstance().doesCharNameExist(_name))
//			{
//				if ((!player.getName().equalsIgnoreCase(_name)) || (player.getName().equals(_name)))
//				{
//					errorMsg = "Name already exists";
//					proceed = false;
//					showNameWindow(player, 0);
//				}
//			}
//			if (!proceed)
//			{
//				player.sendMessage(errorMsg);
//				showNameWindow(player, 0);
//				return;
//			}
//			if (player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true))
//			{
//				try (
//					Connection con = L2DatabaseFactory.getInstance().getConnection();
//					PreparedStatement statement = con.prepareStatement("UPDATE characters SET char_name=? WHERE obj_Id=?"))
//				{
//					statement.setString(1, _name);
//					statement.setInt(2, player.getObjectId());
//					statement.execute();
//					statement.close();
//				}
//				catch (Exception e)
//				{
//					LOGGER.info("Error updating name for player " + player.getName() + ". Error: " + e);
//				}
//				L2World.getInstance().removeFromAllPlayers(player);
//				player.setName(_name);
//				player.store();
//				L2World.getInstance().addToAllPlayers(player);
//				player.sendMessage("Your new character name is " + _name);
//				player.broadcastUserInfo();
//				player.sendMessage("Thank you for helping our server!");
//				if (player.isInParty())
//				{
//					// Delete party window for other party members
//					player.getParty().broadcastToPartyMembers(player, new PartySmallWindowDeleteAll());
//					for (final L2PcInstance member : player.getParty().getPartyMembers())
//					{
//						// And re-add
//						if (member != player)
//						{
//							member.sendPacket(new PartySmallWindowAll(player, player.getParty()));
//						}
//					}
//				}
//				
//				if (player.getClan() != null)
//				{
//					player.getClan().updateClanMember(player);
//					player.getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
//					player.sendPacket(new PledgeShowMemberListAll(player.getClan(), player));
//				}
//				
//				RegionBBSManager.getInstance().changeCommunityBoard();
//			}
//		}
//		else if (command.startsWith("changeClanName"))
//		{
			// String _name = command.substring(15);
			// String errorMsg = null;
			// boolean proceed = true;
			// if (_name.length() < 2)
			// {
			// errorMsg = "Clan Names have to be at least 2 characters";
			// proceed = false;
			// showClanNameWindow(player, 0);
			// }
			// if (_name.length() > 16)
			// {
			// errorMsg = "Clan Names cannot be longer than 16 characters";
			// proceed = false;
			// showClanNameWindow(player, 0);
			// }
			// if ((!Util.isAlphaNumeric(_name)) || (!isValidClanName(_name)))
			// {
			// errorMsg = "Invalid name";
			// proceed = false;
			// showClanNameWindow(player, 0);
			// }
			//// if (ClanTable.getClanByName(_name) != null)
			//// {
			//// errorMsg = "Name already exists";
			//// proceed = false;
			//// showClanNameWindow(player, 0);
			//// }
			// if (!proceed)
			// {
			// player.sendMessage(errorMsg);
			// showClanNameWindow(player, 0);
			// return;
			// }
			// if (player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true))
			// {
			// try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			// PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_name=? WHERE clan_id=?"))
			// {
			// statement.setString(1, _name);
			// statement.setInt(2, player.getClan().getClanId());
			// statement.execute();
			// statement.close();
			// }
			// catch (Exception e)
			// {
			// LOGGER.info("Error updating clan name for player " + player.getName() + ". Error: " + e);
			// }
			// player.getClan().setName(_name);
			// player.sendMessage("Your new clan name is " + _name);
			// player.sendMessage("Thank you for helping our server!");
			// player.getClan().broadcastClanStatus();
			// }
		//
		else if (command.startsWith("setNoble"))
		{
			if (!player.isNoble())
			{
				if (  validarYCobrar(Config.ITEM_ID, Config.NOBLESS_PRICE, player))
				{
					
					player.getInventory().addItem("Tiara", 7694, 1, player, null);
					player.setNoble(true);
					player.setTarget(player);
					player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					player.broadcastUserInfo();
					player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
					player.sendMessage("Felicidades, ahora sos noble, no olvides dar nobles al momento de revivir a alguien.");
				}
				
			}
			else
			{
				player.sendMessage("Ya sos noble.");
			}
		}
		else if (command.startsWith("setDonator"))
		{
			String days = command.substring(10);
			
			int valor;
			
			if("7".equals(days)){
				valor = Config.VIP_7_PRICE;
			}else if("15".equals(days)) {
				valor = Config.VIP_15_PRICE;
			}else if("30".equals(days)) {
				valor = Config.VIP_30_PRICE;
			}else if("90".equals(days)) {
				valor = Config.VIP_90_PRICE;
			}else{
				return;
			}
			
						
			player.sendMessage("Inicio del Comando NPC =>>" + command);
			if (!player.isDonator())
			{
				if ( validarYCobrar(Config.ITEM_ID, valor, player))
				{					
					player.setDonator(true);
					player.setEndTime("donator", Integer.parseInt(days));
					player.setTarget(player);
					updateDatabasex(player, true);
					player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					player.broadcastUserInfo();
					player.sendMessage("Genial!!! Ya sos VIP  por " + days + " Dias");
				}
				
			}
			else
			{
				player.sendMessage("Ya eres VIP");
			}
		}
//		else if (command.startsWith("reducePks2"))
//		{
//			showPksWindow(player, 0);
//		}
//		else if (command.startsWith("reducePks"))
//		{
//			try
//			{
//				String pkReduceString = command.substring(10);
//				int pkReduceCount = Integer.parseInt(pkReduceString);
//				
//				if (player.getPkKills() != 0)
//				{
//					if (pkReduceCount == 0)
//					{
//						player.sendMessage("Please, put a higher value.");
//						showPksWindow(player, 0);
//					}
//					else
//					{
//						if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) >= 1)
//						{
//							player.destroyItemByItemId("Consume", Config.ITEM_ID, 1 * pkReduceCount, player, true);
//							player.setPkKills(player.getPkKills() - pkReduceCount);
//							player.sendMessage("You have successfuly cleaned " + pkReduceCount + " PKs.");
//							player.broadcastUserInfo();
//						}
//						else
//						{
//							player.sendMessage("Not enough items.");
//						}
//					}
//				}
//				else
//				{
//					player.sendMessage("Not enough PKs.");
//				}
//			}
//			catch (Exception e)
//			{
//				player.sendMessage("Incorrect value. Please try again.");
//				showPksWindow(player, 0);
//			}
//		}
		else if (command.startsWith("levelUpClan"))
		{
			if (!player.isClanLeader())
			{
				player.sendMessage("Usted no es lider de ningun clan.");
			}
			else
			{
				if (player.getClan().getLevel() == 8)
				{
					player.sendMessage("Su clan ya es Nivel 8.");
				}
				else
				{
					
					if(!validarYCobrar(Config.ITEM_ID, Config.CLAN_FULL_PRICE, player))
						return; 
					
					player.getClan().setLevel(8);
					player.setTarget(player);
					player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					player.getClan().setReputationScore(player.getClan().getReputationScore() + 10000, true);
					
					// int newCLanLevel = player.getClan().getLevel() +1;
					LOGGER.info("Updating clan ID " + player.getClan().getClanId() + " to level " + 8 + " for player " + player.getName() + ".");
					try (
						Connection con = L2DatabaseFactory.getInstance().getConnection();
						PreparedStatement statement = con.prepareStatement("CALL clanFull(?,?)"))
					{
						statement.setInt(1, player.getClan().getLevel());
						statement.setInt(2, player.getClan().getClanId());
						statement.execute();
						
						for (L2Skill skill : arryClanSkill)
							player.getClan().addNewSkill(skill);
						
						player.getClan().broadcastClanStatus();
						player.sendMessage("Ya tienes clan Full + Skills!!!");
						
						// player.deleteMe();
						// player.sendPacket(new LeaveWorld());
						
						statement.close();
					}
					catch (Exception e)
					{
						LOGGER.info("Error updating clan level for player " + player.getName() + ". Error: " + e);
					}
					player.sendMessage("Your clan level have been increased successfully.");
					
					//
					// if (((player.getClan().getLevel() <= 1) || (player.getClan().getLevel() == 2) || (player.getClan().getLevel() == 3) || (player.getClan().getLevel() == 4)))
					// {
					// player.getClan().setLevel(player.getClan().getLevel() + 1);
					// player.getClan().broadcastClanStatus();
					// player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
					// player.setTarget(player);
					// player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					// int newCLanLevel = player.getClan().getLevel() +1;
					// LOGGER.info("Updating clan ID " + player.getClan().getClanId() +" to level " + newCLanLevel + " for player " + player.getName() + ".");
					// try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					// PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_level=? WHERE clan_id=?"))
					// {
					// statement.setInt(1, player.getClan().getLevel());
					// statement.setInt(2, player.getClan().getClanId());
					// statement.execute();
					// statement.close();
					// }
					// catch (Exception e)
					// {
					// LOGGER.info("Error updating clan level for player " + player.getName() + ". Error: " + e);
					// }
					// player.sendMessage("Your clan level have been increased successfully.");
					// }
					// else if (player.getClan().getLevel() == 5)
					// {
					// if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) >= 10)
					// {
					// player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true);
					// player.getClan().setLevel(player.getClan().getLevel() + 1);
					// player.getClan().broadcastClanStatus();
					// player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
					// player.setTarget(player);
					// player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					// int newCLanLevel = player.getClan().getLevel() +1;
					// LOGGER.info("Updating clan ID " + player.getClan().getClanId() +" to level " + newCLanLevel + " for player " + player.getName() + ".");
					// try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					// PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_level=? WHERE clan_id=?"))
					// {
					// statement.setInt(1, player.getClan().getLevel());
					// statement.setInt(2, player.getClan().getClanId());
					// statement.execute();
					// statement.close();
					// }
					// catch (Exception e)
					// {
					// LOGGER.info("Error updating clan level for player " + player.getName() + ". Error: " + e);
					// }
					// player.sendMessage("Your clan level have been increased successfully.");
					// }
					// else
					// {
					// player.sendMessage("Not enough items.");
					// }
					// }
					// else if (player.getClan().getLevel() == 6)
					// {
					// if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) >= 10)
					// {
					// player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true);
					// player.getClan().setLevel(player.getClan().getLevel() + 1);
					// player.getClan().broadcastClanStatus();
					// player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
					// player.setTarget(player);
					// player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					// int newCLanLevel = player.getClan().getLevel() +1;
					// LOGGER.info("Updating clan ID " + player.getClan().getClanId() +" to level " + newCLanLevel + " for player " + player.getName() + ".");
					// try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					// PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_level=? WHERE clan_id=?"))
					// {
					// statement.setInt(1, player.getClan().getLevel());
					// statement.setInt(2, player.getClan().getClanId());
					// statement.execute();
					// statement.close();
					// }
					// catch (Exception e)
					// {
					// LOGGER.info("Error updating clan level for player " + player.getName() + ". Error: " + e);
					// }
					// player.sendMessage("Your clan level have been increased successfully.");
					// }
					// else
					// {
					// player.sendMessage("Not enough items.");
					// }
					// }
					// else if (player.getClan().getLevel() == 7)
					// {
					// if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) >= 10)
					// {
					// player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true);
					// player.getClan().setLevel(player.getClan().getLevel() + 1);
					// player.getClan().broadcastClanStatus();
					// player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
					// player.setTarget(player);
					// player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
					// int newCLanLevel = player.getClan().getLevel() +1;
					// LOGGER.info("Updating clan ID " + player.getClan().getClanId() +" to level " + newCLanLevel + " for player " + player.getName() + ".");
					// try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					// PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_level=? WHERE clan_id=?"))
					// {
					// statement.setInt(1, player.getClan().getLevel());
					// statement.setInt(2, player.getClan().getClanId());
					// statement.execute();
					// statement.close();
					// }
					// catch (Exception e)
					// {
					// LOGGER.info("Error updating clan level for player " + player.getName() + ". Error: " + e);
					// }
					// player.sendMessage("Your clan level have been increased successfully.");
					// }
					// else
					// {
					// player.sendMessage("Not enough items.");
					// }
					// }
					// player.getClan().broadcastClanStatus();
				}
			}
		}
		else if (command.startsWith("changeGender"))
		{
			if (validarYCobrar(Config.ITEM_ID, Config.SEX_CHANGE_PRICE, player))
			{
				player.getAppearance().setSex(player.getAppearance().getSex() ? false : true);
				player.setTarget(player);
				player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
				L2PcInstance.setSexDB(player, 1);
				player.sendMessage("You have successfully changed your sex.");
				player.decayMe();
				player.spawnMe(player.getX(), player.getY(), player.getZ());
				player.broadcastUserInfo();
			}
			else
			{
				player.sendMessage("You don't have enough items.");
			}
		}
		else if (command.startsWith("hero"))
		{
//			if (!player.isHero())
//			{
//				if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) >= 10)
//				{
//					player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
//					player.setHero(true);
//					updateDatabase(player, 1 * 24L * 60L * 60L * 1000L);
//					player.sendMessage("You have been hero for 1 day. Have a nice time!");
//					player.broadcastUserInfo();
//					player.destroyItemByItemId("Consume", Config.ITEM_ID, 10, player, true);
//				}
//				else
//				{
//					player.sendMessage("You don't have enough items.");
//				}
//			}
//			else
//			{
//				player.sendMessage("You already are a hero.");
//			}
		}
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
			showChatWindow(player, 0);
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void showChatWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II Addicted - Donate Panel</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table bgcolor=000000 width=300 height=40>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Welcome " + player.getName() + " in Lineage II</font><font color=\"FF9900\">-WarLegend-</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Donate Panel}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Help us to improve our server by donating!</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<table>");
		if(Config.ARGUMENT_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_active\">Active Augment ("+Config.ARGUMENT_PRICE+" Donate coins)</a></font></td></tr>");
		
		if(Config.ARGUMENT_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_passive\">Passive Augment ("+Config.ARGUMENT_PRICE+" Donate coins)</a></font></td></tr>");
		// tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_hero\">Become Hero 24h (20 Donate coins)</a></font></td></tr>");
		
		if(Config.NOBLESS_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_setNoble\">Noblesse ("+Config.NOBLESS_PRICE+" Donate coins)</a></font></td></tr>");
		
		if(Config.VIP_7_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_setDonator7\">VIP 7 Dias ("+Config.VIP_7_PRICE+" Donate coins)</a></font></td></tr>");
		
		if(Config.VIP_15_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_setDonator15\">VIP 15 Dias ("+Config.VIP_15_PRICE+" Donate coins)</a></font></td></tr>");
		
		if(Config.VIP_30_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_setDonator30\">VIP 30 Dias ("+Config.VIP_30_PRICE+" Donate coins)</a></font></td></tr>");
		
		if(Config.VIP_90_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_setDonator90\">VIP 90 Dias ("+Config.VIP_90_PRICE+" Donate coins)</a></font></td></tr>");
		
		// tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_changeName2\">Change Name (10 Donate coins)</a></font></td></tr>");
		if(Config.SEX_CHANGE_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_changeGender\">Cambiar Sexo ("+Config.SEX_CHANGE_PRICE+" Donate coins)</a></font></td></tr>");
		// tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_reducePks2\">Remove Pks (1 pk = 1 Donate coin)</a></font></td></tr>");
		// tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_changeClanName2\">Change Clan Name (10 Donate coins)</a></font></td></tr>");
		if(Config.CLAN_FULL_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_levelUpClan\">Clan Full ("+Config.CLAN_FULL_PRICE+" Donate coins per lvl)</a></font></td></tr>");
		if(Config.SKIN_CHANGE_PRICE>0)
			tb.append("<tr><td align=center><font color=\"FF9900\"><a action=\"bypass -h npc_%objectId%_changeSkin\">Cambiar Skin ("+Config.SKIN_CHANGE_PRICE+" Donate coins)</a></font></td></tr>");
		tb.append("</table>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	public void showActiveWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		
		tb.append("<html><head><title>Lineage II SOLO BETA - Active Augment</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Remove Pks}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Add a passive skill for 10 donate coins.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<button value=\"Celestial\" action=\"bypass -h npc_%objectId%_Celestial\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Refresh\" action=\"bypass -h npc_%objectId%_Refresh\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Ritual\" action=\"bypass -h npc_%objectId%_Ritual\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Heal\" action=\"bypass -h npc_%objectId%_Heal\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Recharge\" action=\"bypass -h npc_%objectId%_Recharge\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Cheer\" action=\"bypass -h npc_%objectId%_Cheer\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Blessed Soul\" action=\"bypass -h npc_%objectId%_BlessedSoul\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Blessed Body\" action=\"bypass -h npc_%objectId%_BlessedBody\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> URL</font></td>");
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
	
	public void showPassiveWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II SOLO BETA - Passive Augment</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Remove Pks}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Add a passive skill for 10 donate coins.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<button value=\"Empower\" action=\"bypass -h npc_%objectId%_Empower\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"M.Barrier\" action=\"bypass -h npc_%objectId%_MagicBarrier\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Might\" action=\"bypass -h npc_%objectId%_Might\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Shield\" action=\"bypass -h npc_%objectId%_Shield\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Duel Might\" action=\"bypass -h npc_%objectId%_DuelMight\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Focus\" action=\"bypass -h npc_%objectId%_Focus\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Wild Magic\" action=\"bypass -h npc_%objectId%_WildMagic\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Agility\" action=\"bypass -h npc_%objectId%_Agility\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Guidance\" action=\"bypass -h npc_%objectId%_Guidance\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	public void showPksWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		
		tb.append("<html><head><title>Lineage II Addicted - Remove PKs</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Remove Pks}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Clean Pks. 1 pk = 1 Donate coin.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<edit var=\"pkReduceCount\" width=80 height=15>");
		tb.append("<button value=\"Clean Pks\" action=\"bypass -h npc_%objectId%_reducePks $pkReduceCount\" back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\" width=75 height=21>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	public void showNameWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		
		tb.append("<html><head><title>Lineage II Addicted - Rename</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Rename}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Change your name for 10 donate coins.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<edit var=\"newName\" width=80 height=15>");
		tb.append("<button value=\"Rename\" action=\"bypass -h npc_%objectId%_changeName $newName\" back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\" width=75 height=21>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	public void showClanNameWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		
		tb.append("<html><head><title>Lineage II Addicted - Rename</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Rename}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Change your clan name for 10 donate coins.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<edit var=\"newClanName\" width=80 height=15>");
		tb.append("<button value=\"Rename\" action=\"bypass -h npc_%objectId%_changeClanName $newClanName\" back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\" width=75 height=21>");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	private static void addAugment(L2PcInstance player, int attribute, int skill, int level)
	{
		L2ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (item == null)
		{
			player.sendMessage("You have to equip a weapon.");
			return;
		}
		if (player.getInventory().getInventoryItemCount(Config.ITEM_ID, -1) < 20)
		{
			
			player.sendMessage("You dont have enough item.");
			return;
		}
		if (item.isAugmented())
		{
			player.sendMessage("Remove the augment first.");
			return;
		}
		
		Connection con = null;
		try
		{
			player.destroyItemByItemId("Consume", Config.ITEM_ID, 20, player, true);
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("REPLACE INTO augmentations VALUES(?,?,?,?)");
			statement.setInt(1, item.getObjectId());
			
			statement.setInt(2, attribute * 65536 + 1);
			statement.setInt(3, skill);
			statement.setInt(4, level);
			
			statement.executeUpdate();
			player.sendPacket(new SystemMessage(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED));
			statement.close();
			player.sendMessage("You will be disconnected in 3 seconds to enable the security");
			try
			{
				Thread.sleep(3000L);
			}
			catch (Exception e)
			{
			}
			
			player.deleteMe();
			
			player.sendPacket(new LeaveWorld());
		}
		catch (Exception e)
		{
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	private void updateDatabasex(L2PcInstance player, boolean newDonator)
	{
		Connection con = null;
		try
		{
			if (player == null)
				return;
			
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement stmt = con.prepareStatement(INSERT_DATAA);
			
			stmt.setInt(1, player.getObjectId());
			stmt.setString(2, player.getName());
			stmt.setInt(3, player.isHero() ? 1 : 0);
			stmt.setInt(4, player.isNoble() ? 1 : 0);
			stmt.setInt(5, 1);
			stmt.setLong(6, player.getDonator_end_date());
			stmt.execute();
			stmt.close();
			stmt = null;
		}
		catch (Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			LOGGER.error("Error: could not update database: ", e);
		}
		finally
		{
			CloseUtil.close(con);
			
			con = null;
		}
	}
	
	private void updateDatabase(L2PcInstance player, long heroTime)
	{
		Connection con = null;
		try
		{
			if (player == null)
				return;
			
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement stmt = con.prepareStatement(INSERT_DATA);
			
			stmt.setInt(1, player.getObjectId());
			stmt.setString(2, player.getName());
			stmt.setInt(3, 1);
			stmt.setInt(4, player.isNoble() ? 1 : 0);
			stmt.setInt(5, player.isDonator() ? 1 : 0);
			stmt.setLong(6, heroTime == 0 ? 0 : System.currentTimeMillis() + heroTime);
			stmt.execute();
			stmt.close();
			stmt = null;
		}
		catch (Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			LOGGER.error("Error: could not update database: ", e);
		}
		finally
		{
			CloseUtil.close(con);
			
			con = null;
		}
	}
	
	private boolean isValidName(final String text)
	{
		boolean result = true;
		final String test = text;
		Pattern pattern;
		
		try
		{
			pattern = Pattern.compile(Config.CNAME_TEMPLATE);
		}
		catch (final PatternSyntaxException e) // case of illegal pattern
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			pattern = Pattern.compile(".*");
		}
		
		final Matcher regexp = pattern.matcher(test);
		if (!regexp.matches())
			result = false;
		
		return result;
	}
	
	private boolean isValidClanName(final String text)
	{
		boolean result = true;
		final String test = text;
		Pattern pattern;
		
		try
		{
			pattern = Pattern.compile(Config.CLAN_NAME_TEMPLATE);
		}
		catch (final PatternSyntaxException e) // case of illegal pattern
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			pattern = Pattern.compile(".*");
		}
		
		final Matcher regexp = pattern.matcher(test);
		if (!regexp.matches())
			result = false;
		
		return result;
	}
	
	public void showChangeSkinWindow(L2PcInstance player, int val)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html><head><title>Lineage II Addicted - Passive Augment</title></head><body>");
		tb.append("<center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("</center>");
		tb.append("<center>");
		tb.append("<table bgcolor=000000 width=300 height=12>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"FF0000\">{Remove Pks}</font></td>");
		tb.append("</tr>");
		tb.append("<tr>");
		tb.append("<td width=\"300\" align=\"center\"><font color=\"666666\">Add a passive skill for 10 donate coins.</font></td>");
		tb.append("</tr>");
		tb.append("</table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<button value=\"Human Fighter\" action=\"bypass -h npc_%objectId%_HumanFighter\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Human Mage\" action=\"bypass -h npc_%objectId%_HumanMage\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Elf Fighter\" action=\"bypass -h npc_%objectId%_ElfFighter\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Elf Mage\" action=\"bypass -h npc_%objectId%_ElfMage\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Dark Elf Fighter\" action=\"bypass -h npc_%objectId%_DarkElfFighter\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Dark Elf Mage\" action=\"bypass -h npc_%objectId%_DarkElfMage\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Orc Fighte\" action=\"bypass -h npc_%objectId%_OrcFighter\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Orc Mage\" action=\"bypass -h npc_%objectId%_OrcMage\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Dwarven\" action=\"bypass -h npc_%objectId%_Dwarven\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<button value=\"Original\" action=\"bypass -h npc_%objectId%_BackMainSkin\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalDisable\" fore=\"L2UI_ch3.Btn1_normalDisable\">");
		tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32>");
		tb.append("<br>");
		tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
		tb.append("<table border=0 bgcolor=000000 width=300 height=20>");
		tb.append("<tr>");
		tb.append("<td align=\"center\" width=\"300\"><font color=\"666666\">Vote for us :</font><font color=\"FF5555\"> www.l2wlegend.com.ar</font></td>");
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
	
	private static void setRaceCustomSkin(L2PcInstance player, Races valor)
	{
		if ((player == null) || (valor == null))
		{
			return;
		}
		player.setCustomRaceSkin(valor.getRaceId());
		player.setCustomClassSkin(valor.getClassId());
	}
	
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
	
	public static boolean validarYCobrar(int idItem, int ValorACobrar,L2PcInstance player) {
		
				
	return player.destroyItemByItemId("Consume", idItem, ValorACobrar, player, true) ?  true :  false;
		
		
	}
	
	
	
}