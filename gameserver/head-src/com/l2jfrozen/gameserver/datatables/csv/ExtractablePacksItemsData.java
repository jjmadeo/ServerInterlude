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
package com.l2jfrozen.gameserver.datatables.csv;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2ExtractableItem;
import com.l2jfrozen.gameserver.model.L2ExtractableProductItem;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

/**
 * @author FBIagent
 */
public class ExtractablePacksItemsData
{
	private static Logger LOGGER = Logger.getLogger(ExtractablePacksItemsData.class);

	// Map<itemid, L2ExtractableItem>
	private Map<Integer, L2ExtractableItem> _items;

	private static ExtractablePacksItemsData _instance = null;

	public static ExtractablePacksItemsData getInstance()
	{
		if (_instance == null)
		{
			_instance = new ExtractablePacksItemsData();
		}

		return _instance;
	}

	public ExtractablePacksItemsData()
	{
		_items = new HashMap<>();
		
		Scanner s = null;
		try
		{
			s = new Scanner(new File(Config.DATAPACK_ROOT + "/data/extractable_pack_items.csv"));
			
			int lineCount = 0;
			while (s.hasNextLine())
			{
				lineCount++;
				
				final String line = s.nextLine();
				
				if (line.startsWith("#"))
				{
					continue;
				}
				else if (line.equals(""))
				{
					continue;
				}
				
				final String[] lineSplit = line.split(";");
				int itemID = 0;
				try
				{
					itemID = Integer.parseInt(lineSplit[0]);
				}
				catch (final Exception e)
				{
					if (Config.ENABLE_ALL_EXCEPTIONS)
						e.printStackTrace();
					
					LOGGER.info("Extractable items data: Error in line " + lineCount + " -> invalid item id or wrong seperator after item id!");
					LOGGER.info("		" + line);
					return;
				}
				
				final List<L2ExtractableProductItem> product_temp = new ArrayList<>(lineSplit.length);
				for (int i = 0; i < lineSplit.length - 1; i++)
				{
					String[] lineSplit2 = lineSplit[i + 1].split(",");
					if (lineSplit2.length != 2)
					{
						LOGGER.info("Extractable items data: Error in line " + lineCount + " -> wrong seperator!");
						LOGGER.info("		" + line);
						continue;
					}
					
					int production = 0, amount = 0;
					
					try
					{
						production = Integer.parseInt(lineSplit2[0]);
						amount = Integer.parseInt(lineSplit2[1]);
						lineSplit2 = null;
					}
					catch (final Exception e)
					{
						if (Config.ENABLE_ALL_EXCEPTIONS)
							e.printStackTrace();
						
						LOGGER.info("Extractable items data: Error in line " + lineCount + " -> incomplete/invalid production data or wrong seperator!");
						LOGGER.info("		" + line);
						continue;
					}
					
					product_temp.add(new L2ExtractableProductItem(production, amount, -1));
				}


				_items.put(itemID, new L2ExtractableItem(itemID, product_temp));
			}
			
			LOGGER.info("Extractable items data Packs: Loaded " + _items.size() + " extractable items!");
		}
		catch (final Exception e)
		{
			// if(Config.ENABLE_ALL_EXCEPTIONS)
			e.printStackTrace();
			
			LOGGER.info("Extractable items data: Can not find './data/extractable_items.csv'");
			
		}
		finally
		{


			if (s != null)
				try
				{
					s.close();
				}
				catch (final Exception e1)
				{
					e1.printStackTrace();
				}
		}
	}
	
	public L2ExtractableItem getExtractableItem(final int itemID)
	{
		return _items.get(itemID);
	}
	
	public int[] itemIDs()
	{
		final int size = _items.size();
		final int[] result = new int[size];
		int i = 0;
		for (final L2ExtractableItem ei : _items.values())
		{
			result[i] = ei.getItemId();
			i++;
		}
		return result;
	}
}
