/**
 *                           Sid Builder Classes
 *                           -------------------
 *  begin                : Sat May 6 2001
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
package libsidplay.common;

import resid_builder.resid.ISIDDefs.sampling_method;

public abstract class SIDEmu implements IComponent {

	private SIDBuilder m_builder;

	protected int      m_bufferpos;
	protected short    m_buffer[];

	public SIDEmu(SIDBuilder builder) {
		m_builder = (builder);
	}

	//
	// Standard component functions
	//

	public void reset() {
		reset((short) 0);
	}

	public abstract void reset(short /* uint8_t */volume);

	public abstract short /* uint8_t */read(short /* uint_least8_t */addr);

	public abstract void write(short /* uint_least8_t */addr,
			short /* uint8_t */data);

	public void clock() {}

	public abstract String credits();

	//
	// Standard SID functions
	//

	public abstract void voice(int /* uint_least8_t */num, boolean mute);

	public void optimisation(short level) {
		;
	}

	final public SIDBuilder builder() {
		return m_builder;
	}

	public int bufferpos() { return m_bufferpos; }
	public void bufferpos(int pos) { m_bufferpos = pos; }
	public short[] buffer() { return m_buffer; }
	
	public void sampling(long /* uint_least32_t */ systemfreq, long /* uint_least32_t */ outputfreq, sampling_method method) { return; }

	public void digiBoost(boolean digiBoost) {}

}
