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
package com.l2jfrozen.gameserver.handler.skillhandlers;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.handler.ISkillHandler;
import com.l2jfrozen.gameserver.handler.SkillHandler;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.L2Skill.SkillType;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.StatusUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Juan
 *
 */
public class PartyRescude implements ISkillHandler
{

	protected static final Logger log = Logger.getLogger(PartyRescude.class);

	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PARTY_RESCUDE
	};
	
	/* (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.handler.ISkillHandler#useSkill(com.l2jfrozen.gameserver.model.L2Character, com.l2jfrozen.gameserver.model.L2Skill, com.l2jfrozen.gameserver.model.L2Object[])
	 */
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets) {
		
		log.info("use el Skill Party Rescue");
		
//		try
//		{
//			final ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(SkillType.BUFF);
//			
//			if (handler != null)
//				handler.useSkill(activeChar, skill, targets);
//		}
//		catch (final Exception e)
//		{
//			if (Config.ENABLE_ALL_EXCEPTIONS)
//				e.printStackTrace();
//		}
//		
//		L2Character target = null;
//		
//		L2PcInstance player = null;
//		if (activeChar instanceof L2PcInstance)
//			player = (L2PcInstance) activeChar;
//		
//		double fullHP = 0;
//		double currentHPs = 0;
//		
//		for (final L2Object target2 : targets)
//		{
//			target = (L2Character) target2;
//			
//			// We should not heal if char is dead
//			if (target == null || target.isDead())
//				continue;
//					
//			player = null;
//			
//			fullHP += target.getMaxHp();
//			currentHPs += target.getCurrentHp();
//		}
//		target = null;
//		
//		final double percentHP = currentHPs / fullHP;
//		
//		for (final L2Object target2 : targets)
//		{
//			target = (L2Character) target2;
//			
//			if (target == null || target.isDead())
//				continue;
//			
//			final double newHP = target.getMaxHp() * percentHP;
//			final double totalHeal = newHP - target.getCurrentHp();
//			
//			target.setCurrentHp(newHP);
//			
//			if (totalHeal > 0)
//				target.setLastHealAmount((int) totalHeal);
//			
//			StatusUpdate su = new StatusUpdate(target.getObjectId());
//			su.addAttribute(StatusUpdate.CUR_HP, (int) target.getCurrentHp());
//			target.sendPacket(su);
//			su = null;
//			
//			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
//			sm.addString("HP of the party has been balanced.");
//			target.sendPacket(sm);
//			sm = null;
//			
//		}
//		target = null;
		
	}

	/* (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.handler.ISkillHandler#getSkillIds()
	 */
	@Override
	public SkillType[] getSkillIds()
	{
		// TODO Auto-generated method stub
		return SKILL_IDS;
	
	}
	
}
