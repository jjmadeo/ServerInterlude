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
package com.l2jfrozen.gameserver.taskmanager.tasks;

/**
 * @author Juan
 *
 */
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

import java.util.Vector;

import com.l2jfrozen.gameserver.model.L2Skill;

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

public class CustomCancelTaskManager implements Runnable

{
	
	private L2PcInstance player = null;
	
	private Vector<L2Skill> buffsCanceled = null;
	
	public CustomCancelTaskManager(L2PcInstance p, Vector<L2Skill> skill)
	
	{
		
		player = p;
		
		buffsCanceled = skill;
	}
	
	@Override
	
	public void run()
	
	{
		
		if (player == null)
			
			return;
		
		for (L2Skill skill : buffsCanceled)
		
		{
			
			if (skill == null)
				
				continue;
			
			skill.getEffects(player, player);
			
		}
		
	}
}