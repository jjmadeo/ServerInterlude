/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
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
package com.l2jfrozen.gameserver.network.serverpackets;

/**
 * @author Kerberos
 */
public class ExShowScreenMessage extends L2GameServerPacket
{
	private final int _type;
	private final int _sysMessageId;
	private final int _unk1;
	private final boolean _hide;
	private final boolean _fade;
	private final int _unk2;
	private final int _unk3;
	private final int _unk4;
	private final int _size;
	private final int _position;
	private final boolean _effect;
	private final String _text;
	private final int _time;
	
	public static enum SMPOS
	{
		DUMMY,
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		MIDDLE_LEFT,
		MIDDLE_CENTER,
		MIDDLE_RIGHT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT,
	}

	
	
	public ExShowScreenMessage( String text,  int time)
	{
		_type = 1;
		_sysMessageId = -1;
		_unk1 = 0;
		_unk2 = 0;
		_unk3 = 0;
		_unk4 = 0;
		_position = 0x02;
		_text = text;
		_time = time;
		_size = 0;
		_effect = false;
		_hide = false;
		_fade = false;
	}
	
	
	public ExShowScreenMessage(String text, int time, SMPOS pos, boolean effect)
	{
		this(text, time, pos.ordinal(), effect);
	}

	public ExShowScreenMessage(final int type,boolean fade,boolean hide, final int messageId, final int position, final int unk1, final int size, final int unk2, final int unk3, final boolean showEffect, final int time, final int unk4, final String text)
	{
		_type = type;
		_sysMessageId = messageId;
		_unk1 = unk1;
		_unk2 = unk2;
		_unk3 = unk3;
		_unk4 = unk4;
		_position = position;
		_text = text;
		_time = time;
		_size = size;
		_effect = showEffect;
		_hide = hide;
		_fade = fade;
	}
	
	public ExShowScreenMessage(String text, int time, int pos, boolean effect)
	{
		_type = 1;
		_sysMessageId = -1;
		this._unk1 = 0;
		_hide = false;
		_unk2 = 0;
		_unk3 = 0;
		_fade = false;
		this._unk4 = 0;
		_position = pos;
		_text = text;
		_time = time;
		_size = 0;
		_effect = effect;
	}

	
	
	@Override
	public String getType()
	{
		return "[S]FE:39 ExShowScreenMessage";
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x38);
		writeD(_type); // 0 - system messages, 1 - your defined text
		writeD(_sysMessageId); // system message id (_type must be 0 otherwise no effect)
		writeD(_position); // message position
		//writeD(_unk1); // ?
		writeD(_hide ? 1 : 0); // hide
		writeD(_size); // font size 0 - normal, 1 - small
		writeD(_unk2); // ?
		writeD(_unk3); // ?
		writeD(_effect ? 1 : 0); // upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
		writeD(_time); // time
		//writeD(_unk4); // ?
		writeD(_fade ? 1 : 0); // fade effect (0 - disabled, 1 enabled)
		writeS(_text); // your text (_type must be 1, otherwise no effect)
	}
}