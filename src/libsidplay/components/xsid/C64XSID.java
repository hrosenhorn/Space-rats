/**
 *                               ReSid Wrapper
 *                               -------------
 *  begin                : Fri Apr 4 2001
 *  copyright            : (C) 2001 by Simon White
 *  email                : s_a_white@email.com
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 * @author Ken Händel
 *
 */
package libsidplay.components.xsid;

import resid_builder.resid.ISIDDefs.sampling_method;
import libsidplay.common.C64Env;
import libsidplay.common.SIDEmu;

/**
 * This file could be a specialisation of a sid implementation. However since
 * the sid emulation is not part of this project we are actually creating a
 * wrapper instead.
 */
public class C64XSID extends XSID {

	private C64Env m_env;

	private SIDEmu m_sid;

	public short fData;

	protected  short /* uint8_t */readMemByte(int /* uint_least16_t */addr) {
		short /* uint8_t */data = m_env.readMemRamByte(addr);
		m_env.sid2crc(data);
		return data;
	}

	protected void writeMemByte(short /* uint8_t */data) {
		fData = data;
		m_sid.write((short) 0x18, data);
	}

	public C64XSID(C64Env env, SIDEmu sid) {
		super(env.context());
		m_env = (env);
		m_sid = (sid);
	}

	//
	// Standard component interface
	//
	
	/**
	 * {@inheritDoc}
	 */
	public final String error() {
		return "";
	}

	public void reset() {
		super.reset();
	}

	public void reset(short /* uint8_t */volume) {
		super.reset(volume);
		m_sid.reset(volume);
	}

	public short /* uint8_t */read(short /* uint_least8_t */addr) {
		return m_sid.read(addr);
	}

	public void write(short /* uint_least8_t */addr, short /* uint8_t */data) {
		if (addr == 0x18)
			super.storeSidData0x18(data);
		else
			m_sid.write(addr, data);
	}

	public void write16(int /* uint_least16_t */addr, short /* uint8_t */data) {
		super.write(addr, data);
	}

	//
	// Standard SID interface
	//
	
	public void clock() { m_sid.clock(); }

	public void voice(int /* uint_least8_t */num, boolean mute) {
		if (num == 3)
			super.mute(mute);
		else
			m_sid.voice(num, mute);
	}

    public short[] buffer() {
		return m_sid.buffer();
	}

	public int bufferpos() {
		return m_sid.bufferpos();
	}

	public void bufferpos(int val) {
		m_sid.bufferpos(val);
	}

	public void optimisation(short /* uint_least8_t */level) {
		m_sid.optimisation(level);
	}

	public void sampling(long /* uint_least32_t */systemclock,
			long /* uint_least32_t */freq, sampling_method method) {
		m_sid.sampling(systemclock, freq, method);
	}


	//
	// Xsid specific
	//
	
	public void emulation(SIDEmu sid) {
		m_sid = sid;
	}

	public SIDEmu emulation() {
		return m_sid;
	}

}
