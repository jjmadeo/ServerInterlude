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
package com.l2jfrozen.gameserver.skills.effects;

import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Effect;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

import com.l2jfrozen.gameserver.skills.Env;

final class EffectHide extends L2Effect
{
	public EffectHide(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	/** Notify started */
	@Override
	public void onStart()
	{
		super.onStart();
		
		
			L2PcInstance activeChar = (L2PcInstance) getEffected();
			activeChar.getAppearance().setInvisible();
			activeChar.decayMe();
			activeChar.render();
			activeChar.spawnMe();	
			
			for (L2Character target : activeChar.getKnownList().getKnownCharacters()) {
				if ((target != null) && (target.getTarget() == activeChar)) {
					target.setTarget(null);
					target.abortAttack();
					target.abortCast();
					target.getAI().setIntention(com.l2jfrozen.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE);
				}
			}


	}
	
	/** Notify exited */
	@Override
	public void onExit()
	{
		super.onExit();
		
	
			L2PcInstance activeChar = (L2PcInstance) getEffected();
			
				activeChar.getAppearance().setVisible();
				activeChar.render();
			
			
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HIDE;
	}
	
	@Override
	public boolean onActionTime()
	{
//		// Only cont skills shouldn't end
//		if (getSkill().getSkillType() != SkillType.CONT)
//			return false;
//		
//		if (getEffected().isDead())
//			return false;
//		
//		final double manaDam = calc();
//		
//		if (manaDam > getEffected().getCurrentMp())
//		{
//			final SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
//			getEffected().sendPacket(sm);
//			return false;
//		}
//		
//		getEffected().reduceCurrentMp(manaDam);
		return false;
	}
}