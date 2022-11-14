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
package com.l2jfrozen.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.l2jfrozen.Config;
import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.database.L2DatabaseFactory;

/**
 * @author Juan
 *
 */
public class PushNotificationManagerImpl
{
	
	
//	public  void enviarNotificacion(String token, String mensaje) {
//		
//	}
//	
//	public  void enviarNotificacion(List<String> tokens,String mensaje) {
//		
//	}
//	
//	
//	public  void enviarNotificacionClan(int clanID,String titulo, String mensaje) {
//		
//	}
//	
//	public  void enviarNotificacionAlianza(int clanID,String titulo, String mensaje) {
//		
//	}
//	
//	public  void enviarNotificacionSpawnEpico(List<String> tokens) {
//		
//	}
//	public  void enviarNotificacionSpawnRB(List<String> tokens) {
//		
//	}
//	
//	public  void enviarNotificacionRecordatorioAsedio(List<String> tokens,List<Integer> idClanes) {
//		
//	}
//	
//	public  void enviarNotificacionAsedioNuevoAtacante(List<String> tokens,int clanIdOwner) {
//		
//	}
//	public  void enviarNotificacionAsedioNuevoDefensor(List<String> tokens,int clanIdOwner) {
//		
//	}
//	public  void enviarNotificacionDelAdmin(String mensaje) {
//		
//	}
	
	
	
	
	
	
	
	
	public static void sendMessagePush(String account, Map<String,String> map,String titulo, String Mensaje ) {
		
		String 	dataString = "";
		if(map!= null) {
			dataString	  = "{"+map.entrySet().stream()
			    .map(e -> "\""+ e.getKey() + "\":\"" + String.valueOf(e.getValue()) + "\"")
			    .collect(Collectors.joining(", "))+"}";
		}
		
		
		
		
		
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("INSERT INTO push_notification_sender(`account`,`data`,`title`,`body`)VALUES(?,?,?,?)");
			statement.setString(1, account);	
			statement.setString(2, dataString);
			statement.setString(3, titulo);
			statement.setString(4, Mensaje);
			statement.execute();
			DatabaseUtils.close(statement);
			
			statement = null;
			
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			
		}
		finally
		{
			CloseUtil.close(con);
		}
		
		
	}
	
	
public static  void sendMessagePush(ArrayList<String> accounts, Map<String,String> map,String titulo, String mensaje ) {
		
			String 	dataString = "";
			if(map!= null) {
				dataString	  = "{"+map.entrySet().stream()
				    .map(e -> "\""+ e.getKey() + "\":\"" + String.valueOf(e.getValue()) + "\"")
				    .collect(Collectors.joining(", "))+"}";
			}
	
			String sqlInserts = "";
				
			
			
			sqlInserts= "INSERT INTO push_notification_sender(`account`,`data`,`title`,`body`)VALUES";
			
			StringBuilder sb = new StringBuilder(sqlInserts);
			
			for (String account : accounts)
			{
				sb.append(String.format("('%s','%s','%s','%s'),",account,dataString,titulo,mensaje));
				
			}
			
			
			
			
			
			sb.delete(sb.length()-1, sb.length());
			
			sqlInserts = sb.toString();
			
	
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement(sqlInserts);			
			statement.execute();
			
			
			DatabaseUtils.close(statement);
			
			statement = null;
			
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			
		}
		finally
		{
			CloseUtil.close(con);
		}
		
		
	}

		public static  void sendMessagePushAllAccouns( Map<String,String> map,String titulo, String mensaje ) {
			
			ArrayList<String> accounts = new ArrayList<String>();
			String 	dataString = "";
			if(map!= null) {
				dataString	  = "{"+map.entrySet().stream()
				    .map(e -> "\""+ e.getKey() + "\":\"" + String.valueOf(e.getValue()) + "\"")
				    .collect(Collectors.joining(", "))+"}";
			}
		
			String sqlInserts = "";
				
			
			Connection con1 = null;
			try
			{
				con1 = L2DatabaseFactory.getInstance().getConnection(false);
				PreparedStatement statement1 = con1.prepareStatement("SELECT distinct account_name FROM gameserver.characters;");			
				ResultSet rs = statement1.executeQuery();
				
				
				while (rs.next())
				{
					accounts.add(rs.getString(1));
					
				}
				
				DatabaseUtils.close(statement1);
				
				statement1 = null;
				
			}
			catch (final Exception e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
				
			}
			finally
			{
				CloseUtil.close(con1);
			}
			
			
			
			
			
			
			sqlInserts= "INSERT INTO push_notification_sender(`account`,`data`,`title`,`body`)VALUES";
			
			StringBuilder sb = new StringBuilder(sqlInserts);
			
			for (String account : accounts)
			{
				sb.append(String.format("('%s','%s','%s','%s'),",account,dataString,titulo,mensaje));
				
			}
			
			
			
			
			
			sb.delete(sb.length()-1, sb.length());
			
			sqlInserts = sb.toString();
			
		
			
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(false);
				PreparedStatement statement = con.prepareStatement(sqlInserts);			
				statement.execute();
				
				
				DatabaseUtils.close(statement);
				
				statement = null;
				
			}
			catch (final Exception e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
				
			}
			finally
			{
				CloseUtil.close(con);
			}
		
		
		}
			
	
	
	
	
	
}
