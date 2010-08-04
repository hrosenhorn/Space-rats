/**
 *                           ReSid Emulation
 *                           ---------------
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
package resid_builder;

import static resid_builder.resid.ISIDDefs.resid_version_string;

import java.util.logging.Level;
import java.util.logging.Logger;

import libsidplay.common.C64Env;
import libsidplay.common.IEventContext;
import libsidplay.common.SIDBuilder;
import libsidplay.common.SIDEmu;
import libsidplay.common.Event.event_phase_t;
import libsidplay.common.ISID2Types.sid2_model_t;
import resid_builder.resid.SID;
import resid_builder.resid.ISIDDefs.chip_model;
import resid_builder.resid.ISIDDefs.sampling_method;

public class ReSID extends SIDEmu {
	private static final Logger RESID = Logger.getLogger(ReSID.class.getName());

	private static final String VERSION = "0.0.2";

	private static final int OUTPUTBUFFERSIZE = 32768;

	private IEventContext m_context;

	private event_phase_t m_phase;

	private SID m_sid;

	private long /* event_clock_t */m_accessClk;

	private static String m_credit;

	private String m_error;

	private boolean m_status;

	private boolean m_locked;

	private short /* uint_least8_t */m_optimisation;

	public ReSID(SIDBuilder builder, int count) {
		super(builder);
		m_context = (null);
		m_phase = (event_phase_t.EVENT_CLOCK_PHI1);
		m_sid = (new SID(count));
		m_status = (true);
		m_locked = (false);
		m_optimisation = (0);

		// Setup credits
		m_credit = "ReSID V" + VERSION + " Engine:\n";
		m_credit += "\t(C) 1999-2002 Simon White <sidplay2@yahoo.com>\n";
		m_credit += "MOS6581 (SID) Emulation (ReSID V" + resid_version_string
				+ "):\n";
		m_credit += "\t(C) 1999-2002 Dag Lem <resid@nimrod.no>\n";

		if (m_sid == null) {
			m_error = "RESID ERROR: Unable to create sid object";
			m_status = false;
			return;
		} else {
			m_error = "N/A";
		}
		m_buffer = new short[OUTPUTBUFFERSIZE];
		m_bufferpos = 0;
		reset((short) 0);
	}

	// Standard component functions

	public final String credits() {
		return m_credit;
	}

	public void reset() {
		super.reset();
	}

	public void reset(short /* uint8_t */volume) {
		m_accessClk = 0;
		m_sid.reset();
		m_sid.write(0x18, volume);
	}

	public short /* uint8_t */read(short /* uint_least8_t */addr) {
		clock();
		return (short) m_sid.read(addr);
	}

	public void write(short /* uint_least8_t */addr, short /* uint8_t */data) {
		if (RESID.isLoggable(Level.FINE)) {
			RESID.fine(String.format("write 0x%02x=0x%02x", addr, data));
			RESID.fine("\n");
		}
		clock();
		m_sid.write(addr, data);
	}

	public final String error() {
		return m_error;
	}

	// Standard SID functions

	public void clock()
	 {
	    long /* cycle_count */ cycles = m_context.getTime(m_accessClk, m_phase);
	     m_accessClk += cycles;
	     if (m_optimisation != 0)
	    	 m_bufferpos += m_sid.clock_fast((int)cycles, m_buffer, m_bufferpos, OUTPUTBUFFERSIZE - m_bufferpos, 1);
	     else
	    	 m_bufferpos += m_sid.clock((int)cycles, m_buffer, m_bufferpos, OUTPUTBUFFERSIZE - m_bufferpos, 1);
	 }

	public void filter(boolean enable) {
		m_sid.enable_filter(enable);
	}

	public void voice (int /* uint_least8_t */ num, boolean mute)
	{
	     m_sid.mute (num, mute);
	 }

	/**
	 * Set optimisation level
	 */
	public void optimisation(short /* uint_least8_t */level) {
		m_optimisation = level;
	}

	public boolean bool() {
		return m_status;
	}

	// Specific to ReSID

	public void sampling(long /* uint_least32_t */ systemclock, long /* uint_least32_t */freq, sampling_method method) {
	    if (! m_sid.set_sampling_parameters (systemclock, method, freq, -1, 0.97)) {
	        m_status = false;
	        m_error = "Unable to set desired output frequency.";
	    }
	}

	public boolean filter(final sid_filter_t filter) {
		int fc[][] = new int[0x802][2] /* fc_point */;
		int f0[] /* fc_point */[] = fc;
		int points = 0;

		/* disable distortion by default */
		m_sid.get_filter().set_distortion_properties(false, 0.f, 0.f);
		
		if (filter == null) {
			// Choose default filter

			// m_sid.fc_default(f0, points);
			SID.FCPoints fcp = m_sid.new FCPoints();
			m_sid.fc_default(fcp);
			f0 = fcp.points;
			points = fcp.count;
		} else {
			/* Type 1 or Type 3 */
			if (filter.distortion_enable) {
				/* Must have Type 3. */
				if (filter.baseresistance == 0.f)
					return false;
				m_sid.get_filter().set_type3_properties(filter.baseresistance,
						filter.offset, filter.steepness,
						filter.minimumfetresistance);
				/*
				 * We don't stop here because we also want the type 1
				 * approximation for the fast modes.
				 */
			}

			/* Check that the Type 1 definition looks valid */
			points = filter.points;
			if ((points < 2) || (points > 0x800))
				return false;
			{
				final int /* sid_fc_t */[] fstart = {
						-1, 0 };
				int /* sid_fc_t */[] fprev = fstart;
				int fin = 0;
				int fout = 0;
				// Last check, make sure they are list in numerical order
				// for both axis
				while (points-- > 0) {
					if ((fprev)[0] >= filter.cutoff[fin][0])
						return false;
					fout++;
					f0[fout][0] = filter.cutoff[fin][0];
					f0[fout][1] = filter.cutoff[fin][1];
					fprev = filter.cutoff[fin++];
				}
				// Updated ReSID interpolate requires we
				// repeat the end points
				f0[fout + 1][0] = f0[fout][0];
				f0[fout + 1][1] = f0[fout][1];
				f0[0][0] = f0[1][0];
				f0[0][1] = f0[1][1];
				points = filter.points + 2;
			}
			m_sid.get_filter().set_distortion_properties(
					filter.distortion_enable, filter.rate, filter.point);
			m_sid.filter.resMultiplier = filter.resonanceFactor;
		}

		// function from reSID
		points--;
		m_sid.filter.interpolate(f0, 0, points, m_sid.fc_plotter(), 1.0);

		return true;
	}

	/**
	 * Set the emulated SID model
	 * 
	 * @param model
	 */
	public void model(sid2_model_t model) {
		if (model == sid2_model_t.SID2_MOS8580)
			m_sid.set_chip_model(chip_model.MOS8580);
		else
			m_sid.set_chip_model(chip_model.MOS6581);
	}

	/**
	 * digi boost.
	 * @param digiBoosted enable/disable
	 */
	public void digiBoost(boolean digiBoosted) {
		if (digiBoosted)
			m_sid.input(-32768);
		else
			m_sid.input(0);
	}

	public boolean getDigiBoost8580() {
		return m_sid.fDigiBoost;
	}

	// Must lock the SID before using the standard functions

	/**
	 * Set execution environment and lock sid to it
	 * 
	 * @param env
	 * @return
	 */
	public boolean lock(C64Env env) {
		if (env == null) {
			if (!m_locked)
				return false;
			m_locked = false;
			m_context = null;
		} else {
			if (m_locked)
				return false;
			m_locked = true;
			m_context = env.context();
		}
		return true;
	}

}
