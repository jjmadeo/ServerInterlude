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
package com.l2jfrozen.gameserver.skills.effects;

import org.apache.log4j.Logger;

import com.l2jfrozen.gameserver.handler.skillhandlers.PartyRescude;
import com.l2jfrozen.gameserver.model.L2Effect;
import com.l2jfrozen.gameserver.skills.Env;

/**
 * @author Juan
 *
 */
class EffectPartyRescude extends L2Effect
{

	protected static final Logger log = Logger.getLogger(EffectPartyRescude.class);

	
	
	/**
	 * @param env
	 * @param template
	 */
	public EffectPartyRescude(Env env, EffectTemplate template)
	{
		super(env, template);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EffectType getEffectType()
	{
		return L2Effect.EffectType.PARTY_RESCUDE;
	}
	
	@Override
	public void onStart()
	{
//		log.info("Valor OnStartActual:"+getEffected().isCaptureDamage());
//		
//		getEffected().setCaptureDamage(true);
//		
//		log.info("Valor OnStartDespuesDel Set:"+getEffected().isCaptureDamage());
//		
	}
	
	@Override
	public boolean onActionTime()
	{
//		int sumdamages = 0;
//		int i = 0;
//		for (int damage : getEffected().getDamageListIneffect())
//		{
//			i++;
//			sumdamages += damage;
//		}
//		
//		log.info("DamageSumado:"+sumdamages + "Recorder"+i);
//		log.info("Efected:"+getEffected().getName());
//		
//		
//		log.info("Efector:"+getEffector().getName());
//		log.info("Efected:"+getEffected().getName());
//		
		// Commented. But I'm not really sure about this, could cause some bugs.
		// getEffected().setIsInvul(false);
		return false;
	}
	
	@Override
	public void onExit()
	{
//		log.info("Valor OnExitActual:"+getEffected().isCaptureDamage());
		
//		getEffected().setCaptureDamage(false);
		
//		log.info("Valor OnExitDespuesDel Set:"+getEffected().isCaptureDamage());
		
	}
	
}
