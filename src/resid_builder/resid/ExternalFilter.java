/**
 * This file is part of reSID, a MOS6581 SID emulator engine.
 * Copyright (C) 2004  Dag Lem <resid@nimrod.no>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * @author Ken Händel
 *
 */
package resid_builder.resid;

import resid_builder.resid.ISIDDefs.chip_model;

/**
 * 
 * The audio output stage in a Commodore 64 consists of two STC networks, a
 * low-pass filter with 3-dB frequency 16kHz followed by a high-pass filter with
 * 3-dB frequency 16Hz (the latter provided an audio equipment input impedance
 * of 1kOhm).
 * <P>
 * The STC networks are connected with a BJT supposedly meant to act as a unity
 * gain buffer, which is not really how it works. A more elaborate model would
 * include the BJT, however DC circuit analysis yields BJT base-emitter and
 * emitter-base impedances sufficiently low to produce additional low-pass and
 * high-pass 3dB-frequencies in the order of hundreds of kHz. This calls for a
 * sampling frequency of several MHz, which is far too high for practical use.
 * 
 * @author Ken Händel
 * 
 */
public class ExternalFilter {

	private static final float pi = 3.141592654f;

	/**
	 * Filter enabled.
	 */
	private boolean enabled;

	/**
	 * Maximum mixer DC offset.
	 */
	private float /* sound_sample */mixer_DC;

	/**
	 * Relevant clocks
	 */
	float clock_frequency, pass_frequency;
	
	/**
	 * State of filters. lowpass
	 */
	private float /* sound_sample */Vlp;

	/**
	 * State of filters. highpass
	 */
	private float /* sound_sample */Vhp;

	/**
	 * State of filters.
	 */
	private float /* sound_sample */Vo;

	/**
	 * Cutoff frequencies.
	 */
	private float /* sound_sample */ w0lp;

	/**
	 * Cutoff frequencies.
	 */
	private float /* sound_sample */ w0hp;

	// ----------------------------------------------------------------------------
	// Inline functions.
	// The following functions are defined inline because they are called every
	// time a sample is calculated.
	// ----------------------------------------------------------------------------

	/**
	 * SID clocking - 1 cycle.
	 * 
	 * @param Vi
	 */
	public final void clock(float /* sound_sample */Vi) {
		// This is handy for testing.
		if (!enabled) {
			// Remove maximum DC level since there is no filter to do it.
			Vlp = Vhp = 0.f;
			Vo = Vi - mixer_DC;
			return;
		}

		float /* sound_sample */ dVlp = w0lp * (Vi - Vlp) + 1e-4f;
		float /* sound_sample */ dVhp = w0hp * (Vlp - Vhp);
		Vo = Vlp - Vhp;
		Vlp += dVlp;
		Vhp += dVhp;
	}

	/**
	 * SID clocking - delta_t cycles.
	 * 
	 * @param delta_t
	 * @param Vi
	 */
	public final void clock(int /* cycle_count */delta_t, float /* sound_sample */Vi) {
		// This is handy for testing.
		if (!enabled) {
			// Remove maximum DC level since there is no filter to do it.
			Vlp = Vhp = 0.f;
			Vo = Vi - mixer_DC;
			return;
		}

		// Maximum delta cycles for the external filter to work satisfactorily
		// is approximately 8.
		int /* cycle_count */delta_t_flt = 4;

		while (delta_t != 0) {
			if (delta_t < delta_t_flt) {
				delta_t_flt = delta_t;
			}

			// delta_t is converted to seconds given a 1MHz clock by dividing
			// with 1 000 000.

			// Calculate filter outputs.
			// Vo = Vlp - Vhp;
			// Vlp = Vlp + w0lp*(Vi - Vlp)*delta_t;
			// Vhp = Vhp + w0hp*(Vlp - Vhp)*delta_t;

			float /* sound_sample */ dVlp = (w0lp * delta_t_flt) * (Vi - Vlp) + 1e-4f;
			float /* sound_sample */ dVhp = w0hp * delta_t_flt * (Vlp - Vhp);
		     Vo = Vlp - Vhp;
		     Vlp += dVlp;
		     Vhp += dVhp;

			delta_t -= delta_t_flt;
		}
	}

	/**
	 * Audio output (20 bits).
	 * 
	 * Audio output (19.5 bits).
	 * 
	 * @return Vo
	 */
	public final float /* sound_sample */output() {
		return Vo;
	}

	// ----------------------------------------------------------------------------
	// END Inline functions.
	// ----------------------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public ExternalFilter() {
		reset();
		enable_filter(true);
		set_chip_model(chip_model.MOS6581);
		set_clock_frequency(1e6f);
		set_sampling_parameter(15915.6f);
	}

	/**
	 * Enable filter.
	 * 
	 * @param enable
	 * enable filter
	 */
	public void enable_filter(boolean enable) {
		enabled = enable;
	}

	void set_clock_frequency(float clock)
	 {
	  clock_frequency = clock;
	  _set_sampling_parameter();
	}
	
	void set_sampling_parameter(float freq)
	{
	  pass_frequency = freq;
	  _set_sampling_parameter();
	}
	
	void _set_sampling_parameter()
	{
	   // Low-pass:  R = 10kOhm, C = 1000pF; w0l = 1/RC = 1/(1e4*1e-9) = 100000
	   // High-pass: R =  1kOhm, C =   10uF; w0h = 1/RC = 1/(1e3*1e-5) =    100
	  w0hp = 100.f / clock_frequency;
	  w0lp = pass_frequency * 2.f * pi / clock_frequency;
	 }

	/**
	 * Set chip model.
	 * 
	 * @param model
	 * chip model
	 */
	public void set_chip_model(chip_model model) {
		if (model == chip_model.MOS6581) {
			// Maximum mixer DC output level; to be removed if the external
			// filter is turned off: ((wave DC + voice DC) * voices + mixer DC)
			// * volume
			// See Voice.java and Filter.java for an explanation of the values.
			mixer_DC = ((((0x800 - 0x380) + 0x800) * 0xff * 3 - 0xfff * 0xff / 18) >> 7) * 0x0f;
		} else {
			// No DC offsets in the MOS8580.
			mixer_DC = 0;
		}
	}

	/**
	 * SID reset.
	 */
	public void reset() {
		// State of filter.
		Vlp = 0;
		Vhp = 0;
		Vo = 0;
	}
}
