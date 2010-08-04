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
 * The SID filter is modeled with a two-integrator-loop biquadratic filter,
 * which has been confirmed by Bob Yannes to be the actual circuit used in the
 * SID chip.
 * <P>
 * Measurements show that excellent emulation of the SID filter is achieved,
 * except when high resonance is combined with high sustain levels. In this case
 * the SID op-amps are performing less than ideally and are causing some
 * peculiar behavior of the SID filter. This however seems to have more effect
 * on the overall amplitude than on the color of the sound.
 * <P>
 * The theory for the filter circuit can be found in "Microelectric Circuits" by
 * Adel S. Sedra and Kenneth C. Smith. The circuit is modeled based on the
 * explanation found there except that an additional inverter is used in the
 * feedback from the bandpass output, allowing the summer op-amp to operate in
 * single-ended mode. This yields inverted filter outputs with levels
 * independent of Q, which corresponds with the results obtained from a real
 * SID.
 * <P>
 * We have been able to model the summer and the two integrators of the circuit
 * to form components of an IIR filter. Vhp is the output of the summer, Vbp is
 * the output of the first integrator, and Vlp is the output of the second
 * integrator in the filter circuit.
 * <P>
 * According to Bob Yannes, the active stages of the SID filter are not really
 * op-amps. Rather, simple NMOS inverters are used. By biasing an inverter into
 * its region of quasi-linear operation using a feedback resistor from input to
 * output, a MOS inverter can be made to act like an op-amp for small signals
 * centered around the switching threshold.
 * <P>
 * Qualified guesses at SID filter schematics are depicted below.
 * 
 * <pre>
 * SID filter
 * ----------
 * 
 *     -----------------------------------------------
 *    |                                               |
 *    |            ---Rq--                            |
 *    |           |       |                           |
 *    |  ------------&lt;A]-----R1---------              |
 *    | |                               |             |
 *    | |                        ---C---|      ---C---|
 *    | |                       |       |     |       |
 *    |  --R1--    ---R1--      |---Rs--|     |---Rs--| 
 *    |        |  |       |     |       |     |       |
 *     ----R1--|-----[A&gt;--|--R-----[A&gt;--|--R-----[A&gt;--|
 *             |          |             |             |
 * vi -----R1--           |             |             |
 * 
 *                       vhp           vbp           vlp
 * 
 * 
 * vi  - input voltage
 * vhp - highpass output
 * vbp - bandpass output
 * vlp - lowpass output
 * [A&gt; - op-amp
 * R1  - summer resistor
 * Rq  - resistor array controlling resonance (4 resistors)
 * R   - NMOS FET voltage controlled resistor controlling cutoff frequency
 * Rs  - shunt resitor
 * C   - capacitor
 * 
 * 
 * 
 * SID integrator
 * --------------
 * 
 *                                   V+
 * 
 *                                   |
 *                                   |
 *                              -----|
 *                             |     |
 *                             | ||--
 *                              -||
 *                   ---C---     ||-&gt;
 *                  |       |        |
 *                  |---Rs-----------|---- vo
 *                  |                |
 *                  |            ||--
 * vi ----     -----|------------||
 *        |   &circ;     |            ||-&gt;
 *        |___|     |                |
 *        -----     |                |
 *          |       |                |
 *          |---R2--                 |
 *          |
 *          R1                       V-
 *          |
 *          |
 * 
 *          Vw
 * ----------------------------------------------------------------------------
 * </pre>
 * 
 * @author Ken H�ndel
 */
public class Filter {

	/**
	 * #define SPLINE_BRUTE_FORCE false
	 */
	public static final boolean SPLINE_BRUTE_FORCE = false;

	/**
	 * Filter enabled.
	 */
	protected boolean enabled;

	/**
	 * Filter cutoff frequency.
	 */
	public int /* reg12 */fc;

	/**
	 * Filter resonance.
	 */
	public int /* reg8 */res;

	/**
	 * Selects which inputs to route through filter.
	 */
	protected int /* reg8 */filt;

	/**
	 * Switch voice 3 off.
	 */
	protected int /* reg8 */voice3off;

	/**
	 * Highpass, bandpass, and lowpass filter modes.
	 */
	protected int /* reg8 */hp_bp_lp;

	/**
	 * Output master volume.
	 */
	protected int /* reg4 */vol;

	protected float volf; /* to avoid integer-to-float conversion at output */

	/**
	 * Mixer DC offset.
	 */
	protected float /* sound_sample */mixer_DC;
	
	// clock
	float clock_frequency;

	/* Distortion params: the external params */
	public boolean distortion_enable;
	float distortion_rate, distortion_point;

	/* Type 3 params. */
	float baseresistance, offset, steepness, minimumfetresistance;
	
	float Vhp, Vbp, Vlp, Vnf, gate_hp, gate_bp;

	// Cutoff frequency, resonance, distortion helpers.
	float w0, w0_ceil_1, w0_ceil_dt, _1_div_Q;
	 
	/* Distortion helpers. */
	float fc_kink;
	
	/**
	 * Cutoff frequency tables. FC is an 11 bit register.
	 */
	protected int /* sound_sample */f0_6581[] = new int[2048];

	/**
	 * Cutoff frequency tables. FC is an 11 bit register.
	 */
	protected int /* sound_sample */f0_8580[] = new int[2048];

	public int /* sound_sample */f0[];

	
	static final float pi = 3.141592654f;
	
	/**
	 * 
	 * Maximum cutoff frequency is specified as FCmax = 2.6e-5/C =
	 * 2.6e-5/2200e-12 = 11818.
	 * <P>
	 * Measurements indicate a cutoff frequency range of approximately 220Hz -
	 * 18kHz on a MOS6581 fitted with 470pF capacitors. The function mapping FC
	 * to cutoff frequency has the shape of the tanh function, with a
	 * discontinuity at FCHI = 0x80. In contrast, the MOS8580 almost perfectly
	 * corresponds with the specification of a linear mapping from 30Hz to
	 * 12kHz.
	 * <P>
	 * The mappings have been measured by feeding the SID with an external
	 * signal since the chip itself is incapable of generating waveforms of
	 * higher fundamental frequency than 4kHz. It is best to use the bandpass
	 * output at full resonance to pick out the cutoff frequency at any given FC
	 * setting.
	 * <P>
	 * The mapping function is specified with spline interpolation points and
	 * the function values are retrieved via table lookup.
	 * <P>
	 * NB! Cutoff frequency characteristics may vary, we have modeled two
	 * particular Commodore 64s.
	 */
	public static int[] /* fc_point */f0_points_6581[] = {
	// -----FC----f-------FCHI-FCLO
			// ----------------------------
			{ 0, 220 }, // 0x00 - repeated end point
			{ 0, 220 }, // 0x00
			{ 128, 230 }, // 0x10
			{ 256, 250 }, // 0x20
			{ 384, 300 }, // 0x30
			{ 512, 420 }, // 0x40
			{ 640, 780 }, // 0x50
			{ 768, 1600 }, // 0x60
			{ 832, 2300 }, // 0x68
			{ 896, 3200 }, // 0x70
			{ 960, 4300 }, // 0x78
			{ 992, 5000 }, // 0x7c
			{ 1008, 5400 }, // 0x7e
			{ 1016, 5700 }, // 0x7f
			{ 1023, 6000 }, // 0x7f 0x07
			{ 1023, 6000 }, // 0x7f 0x07 - discontinuity
			{ 1024, 4600 }, // 0x80 -
			{ 1024, 4600 }, // 0x80
			{ 1032, 4800 }, // 0x81
			{ 1056, 5300 }, // 0x84
			{ 1088, 6000 }, // 0x88
			{ 1120, 6600 }, // 0x8c
			{ 1152, 7200 }, // 0x90
			{ 1280, 9500 }, // 0xa0
			{ 1408, 12000 }, // 0xb0
			{ 1536, 14500 }, // 0xc0
			{ 1664, 16000 }, // 0xd0
			{ 1792, 17100 }, // 0xe0
			{ 1920, 17700 }, // 0xf0
			{ 2047, 18000 }, // 0xff 0x07
			{ 2047, 18000 } // 0xff 0x07 - repeated end point
	};

	/**
	 * 
	 * Maximum cutoff frequency is specified as FCmax = 2.6e-5/C =
	 * 2.6e-5/2200e-12 = 11818.
	 * 
	 * Measurements indicate a cutoff frequency range of approximately 220Hz -
	 * 18kHz on a MOS6581 fitted with 470pF capacitors. The function mapping FC
	 * to cutoff frequency has the shape of the tanh function, with a
	 * discontinuity at FCHI = 0x80. In contrast, the MOS8580 almost perfectly
	 * corresponds with the specification of a linear mapping from 30Hz to
	 * 12kHz.
	 * 
	 * The mappings have been measured by feeding the SID with an external
	 * signal since the chip itself is incapable of generating waveforms of
	 * higher fundamental frequency than 4kHz. It is best to use the bandpass
	 * output at full resonance to pick out the cutoff frequency at any given FC
	 * setting.
	 * 
	 * The mapping function is specified with spline interpolation points and
	 * the function values are retrieved via table lookup.
	 * 
	 * NB! Cutoff frequency characteristics may vary, we have modeled two
	 * particular Commodore 64s.
	 */
	public static int[] /* fc_point */f0_points_8580[] = {
	// -----FC----f-------FCHI-FCLO
			// ----------------------------
			{ 0, 0 }, // 0x00 - repeated end point
			{ 0, 0 }, // 0x00
			{ 128, 800 }, // 0x10
			{ 256, 1600 }, // 0x20
			{ 384, 2500 }, // 0x30
			{ 512, 3300 }, // 0x40
			{ 640, 4100 }, // 0x50
			{ 768, 4800 }, // 0x60
			{ 896, 5600 }, // 0x70
			{ 1024, 6500 }, // 0x80
			{ 1152, 7500 }, // 0x90
			{ 1280, 8400 }, // 0xa0
			{ 1408, 9200 }, // 0xb0
			{ 1536, 9800 }, // 0xc0
			{ 1664, 10500 }, // 0xd0
			{ 1792, 11000 }, // 0xe0
			{ 1920, 11700 }, // 0xf0
			{ 2047, 12500 }, // 0xff 0x07
			{ 2047, 12500 } // 0xff 0x07 - repeated end point
	};

	protected int[] /* fc_point */f0_points[];

	protected int f0_count;

	public float resMultiplier = 1.0f;

	final float opmax = 4e6f;
	final float opmin = -4e6f;
	static final float kinkiness = 0.95f;
	final float sidcaps = 470e-12f;

	float inOutPrev;
	float estimate_distorted_w0(float source, float /* & IN/OUT*/ prevsource)
	{
	    source *= distortion_rate;
	    source += distortion_point;
	    source = source < 0.f ? 0.f : source;
	
	    prevsource = prevsource * 0.9965f + source * 0.0035f + 1e-12f;
	    inOutPrev = prevsource;
	
	    /* approximate the FET */
	    float gate = offset / (fc_kink * (1.f + prevsource));
	    
	    /* 2 parallel resistors, FET approximated by the gate expression. */
	    float dynamic_resistance = minimumfetresistance + gate;
	    float resistance = baseresistance * dynamic_resistance / (baseresistance + dynamic_resistance);
	    return 1.f / (sidcaps * resistance * clock_frequency);
	}

	void filter_distorted(float Vi)
	{
		/* Distortion tends to hardclip easily, so allocate some headroom.
		* this unfortunately causes trouble during sped up play because this code
		* is not run then. Maybe ReSID has some kind of global gain toggle
		* somewhere? */
		Vnf *= 0.707107f;
		Vi *= 0.707107f;
		
		// delta_t = 1 is converted to seconds given a 1MHz clock by dividing
		// with 1 000 000.
		
		float w0_eff_hp = estimate_distorted_w0(Vhp, gate_hp);
		gate_hp = inOutPrev;
		float w0_eff_bp = estimate_distorted_w0(Vbp, gate_bp);
		gate_bp = inOutPrev;
		
		/* This procedure runs the filter with -3 dB attenuation, resulting from
		* the mixing. Filter input should probably be lowpass filtered by a 2-10 kHz
		* 6 dB/oct filter. */
		Vhp = (Vbp*_1_div_Q) - Vlp * 0.707107f - Vi * 0.5f;
		Vlp -= w0_eff_bp * Vbp * 1.189207f;
		Vbp -= w0_eff_hp * Vhp * 1.189207f;
	}

	/**
	 * SID clocking - 1 cycle
	 * 
	 * @param voice1
	 * @param voice2
	 * @param voice3
	 * @param ext_in
	 */
	public final void clock(float /* sound_sample */voice1,
			float /* sound_sample */voice2, float /* sound_sample */voice3,
			float /* sound_sample */ext_in) {

		// NB! Voice 3 is not silenced by voice3off if it is routed through
		// the filter.
		if ((voice3off != 0) && ((filt & 0x04) == 0)) {
			voice3 = 0;
		}

		// This is handy for testing.
		if (!enabled) {
			Vnf = voice1 + voice2 + voice3 + ext_in;
			Vhp = Vbp = Vlp = 0.f;
			return;
		}

		float /* sound_sample */Vi = Vnf = 0f;

		// Route voices into or around filter.
		if ((filt & 1) != 0) Vi += voice1; else Vnf += voice1;
		if ((filt & 2) != 0) Vi += voice2; else Vnf += voice2;
		if ((filt & 4) != 0) Vi += voice3; else Vnf += voice3;
		if ((filt & 8) != 0) Vi += ext_in; else Vnf += ext_in;

		/* Avoid denormal numbers */
		Vlp += 1e-12;
		Vhp += 1e-12;
		Vbp += 1e-12;
		  
		if (distortion_enable) {
		  filter_distorted(Vi);
		  return;
		}

		// delta_t = 1 is converted to seconds given a 1MHz clock by dividing
		// with 1 000 000.

		Vhp = (Vbp*_1_div_Q) - Vlp - Vi;
		Vlp -= w0*Vbp;
		Vbp -= w0*Vhp;
	}

	/**
	 * SID clocking - delta_t cycles.
	 * 
	 * @param delta_t
	 * @param voice1
	 * @param voice2
	 * @param voice3
	 * @param ext_in
	 */
	public final void clock(int /* cycle_count */delta_t,
			float /* sound_sample */voice1, float /* sound_sample */voice2,
			float /* sound_sample */voice3, float /* sound_sample */ext_in) {

		// NB! Voice 3 is not silenced by voice3off if it is routed through
		// the filter.
		if ((voice3off != 0) && ((filt & 0x04) == 0)) {
			voice3 = 0;
		}

		// Enable filter on/off.
		// This is not really part of SID, but is useful for testing.
		// On slow CPUs it may be necessary to bypass the filter to lower the
		// CPU load.
		if (!enabled) {
			Vnf = voice1 + voice2 + voice3 + ext_in;
			Vhp = Vbp = Vlp = 0.f;
			return;
		}

		float /* sound_sample */ Vi = Vnf = 0f;

		// Route voices into or around filter.
		// The code below is expanded to a switch for faster execution.
		if ((filt & 1) != 0) Vi += voice1; else Vnf += voice1;
		if ((filt & 2) != 0) Vi += voice2; else Vnf += voice2;
		if ((filt & 4) != 0) Vi += voice3; else Vnf += voice3;
		if ((filt & 8) != 0) Vi += ext_in; else Vnf += ext_in;

		/* Avoid denormal numbers */
		Vlp += 1e-12;
		Vhp += 1e-12;
		Vbp += 1e-12;

		// Maximum delta cycles for the filter to work satisfactorily under current
		// cutoff frequency and resonance constraints is approximately 3.
		int /* cycle_count */ delta_t_flt = 4;

		while (delta_t != 0) {
			if (delta_t < delta_t_flt) {
				delta_t_flt = delta_t;
			}

			// delta_t is converted to seconds given a 1MHz clock by dividing
			// with 1 000 000. This is done in two operations to avoid integer
			// multiplication overflow.
			float w0_delta_t = w0_ceil_dt * delta_t_flt;
			Vhp = (Vbp * _1_div_Q) - Vlp - Vi;
			Vlp -= w0_delta_t * Vbp;
			Vbp -= w0_delta_t * Vhp;

			delta_t -= delta_t_flt;
		}
	}

	/**
	 * sid audio output, -32768 .. 32767
	 * 
	 * @return
	 */
	public final float /* sound_sample */output() {
		float Vf = 0.f;
		if ((hp_bp_lp & 1)!=0)
		   Vf += Vlp;
		if ((hp_bp_lp & 2)!=0)
		   Vf += Vbp;
		if ((hp_bp_lp & 4)!=0)
		   Vf += Vhp;

		// Sum non-filtered and filtered output, scale one voice to 13 bits
		// Multiply the sum with volume.
		return ((Vnf + Vf) / (float) (1 << 7) + mixer_DC) * volf;
	}

	// ----------------------------------------------------------------------------
	// END Inline functions.
	// ----------------------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public Filter() {
		enable_filter(true);

		// Create mappings from FC to cutoff frequency.
		interpolate(f0_points_6581, 0, f0_points_6581.length - 1,
				new PointPlotter(f0_6581), 1.0);
		interpolate(f0_points_8580, 0, f0_points_8580.length - 1,
				new PointPlotter(f0_8580), 1.0);

		set_chip_model(chip_model.MOS6581);
		set_clock_frequency(1e6f);
		/* No distortion by default. */
		set_distortion_properties(false, 0.f, 0.f);
		reset();
	}

	/**
	 * Enable filter.
	 * 
	 * @param enable
	 */
	public void enable_filter(boolean enable) {
		enabled = enable;
	}

	/**
	 * Set chip model.
	 * 
	 * @param model
	 */
	public void set_chip_model(chip_model model) {
		if (model == chip_model.MOS6581) {
			// The mixer has a small input DC offset. This is found as follows:
			//
			// The "zero" output level of the mixer measured on the SID audio
			// output pin is 5.50V at zero volume, and 5.44 at full
			// volume. This yields a DC offset of (5.44V - 5.50V) = -0.06V.
			//
			// The DC offset is thus -0.06V/1.05V ~ -1/18 of the dynamic range
			// of one voice. See Voice.java for measurement of the dynamic
			// range.

			mixer_DC = -0xfff * 0xff / 18 >> 7;

			f0 = f0_6581;
			f0_points = f0_points_6581;
			f0_count = f0_points_6581.length;
		} else {
			// No DC offsets in the MOS8580.
			mixer_DC = 0;

			f0 = f0_8580;
			f0_points = f0_points_8580;
			f0_count = f0_points_8580.length;
		}

		set_w0();
		set_Q();
	}

	void set_clock_frequency(float clock) {
		clock_frequency = clock;
	}
		 
	public void set_distortion_properties(boolean en, float r, float p)
	{
	  distortion_enable = en;
	  distortion_rate = r;
	  distortion_point = p;
	}
	 
	public void set_type3_properties(float br, float o, float s, float mfr)
	{
	    baseresistance = br;
	    offset = o;
	    steepness = (float) Math.log(s); /* s^x to e^(x*ln(s)) */
	    minimumfetresistance = mfr;
	}
	
	public float[] get_distortion_properties() {
		return new float[] { distortion_enable?1.0f:0.0f, distortion_rate, distortion_point};
	}

	/**
	 * SID reset.
	 */
	public void reset() {
		fc = res = filt = voice3off = hp_bp_lp = vol = 0;
		volf = gate_hp = gate_bp = Vhp = Vbp = Vlp = 0f;
		set_w0();
		set_Q();
	}

	/**
	 * Register functions.
	 * 
	 * @param fc_lo
	 */
	public void writeFC_LO(int /* reg8 */fc_lo) {
		fc = fc & 0x7f8 | fc_lo & 0x007;
		set_w0();
	}

	/**
	 * Register functions.
	 * 
	 * @param fc_hi
	 */
	public void writeFC_HI(int /* reg8 */fc_hi) {
		fc = (fc_hi << 3) & 0x7f8 | fc & 0x007;
		set_w0();
	}

	/**
	 * Register functions.
	 * 
	 * @param res_filt
	 */
	public void writeRES_FILT(int /* reg8 */res_filt) {
		res = (res_filt >> 4) & 0x0f;
		set_Q();

		filt = res_filt & 0x0f;
	}

	/**
	 * Register functions.
	 * 
	 * @param mode_vol
	 */
	public void writeMODE_VOL(int /* reg8 */mode_vol) {
		voice3off = mode_vol & 0x80;

		hp_bp_lp = (mode_vol >> 4) & 0x07;

		volf = vol = mode_vol & 0x0f;
	}

//	float get_derivate(int fc_min, int fc_max)
//	{
//	  /* the derivate is used to scale the distortion factor, because the
//	   * true shape of the distortion is related to the fc curve itself. By and
//	   * large the distortion acts as if the FC value itself was changed. */
//	  if (fc_min < 0)
//	    fc_min = 0;
//	  if (fc_max > 2047)
//	    fc_max = 2047;
//	
//	  if (fc_min == fc_max)
//	    return 0;
//	
//	  /* If this estimate passes the largest kinks, cancel them out. */
//	  int /* sound_sample */ kinkfix = 0;
//	  if (fc_min <= 1023 && fc_max >= 1024)
//	    kinkfix = f0[1024] - f0[1023];
//	  if (fc_min <= 511 && fc_max >= 512)
//	    kinkfix = f0[512] - f0[511];
//	  if (fc_min <= 1535 && fc_max >= 1536)
//	    kinkfix = f0[1536] - f0[1535];
//	
//	  return (float) (f0[fc_max] - f0[fc_min] - kinkfix) / (fc_max - fc_min) / (1 << 20);
//	}
	
	/* This is Type3-II, a redefined (and much simpler!) calculation, also avoiding
	 * the ad-hocery of the earlier code.
	 *
	 * Sadly, all old type3 curves must be refitted, as it is not possible to
	 * trivially fix old type 3 curves to the new definition. */
	static float kinked_dac(int /* reg12 */ fc)
	{
	    float bits = 0.f;
	    float max = 0.f;
	    for (int i = 0; i < 11; i += 1) {
	        float weight = (float) Math.pow(2.d, i * kinkiness);
	        if ((fc & (1 << i))!=0)
	            bits += weight;
	        max += weight;
	    }
	
	    return bits / max * 2048.f;
	}

	// Set filter cutoff frequency.
	private void set_w0() {
		final float pi = 3.141592654f;
		 
		fc_kink = (float) Math.exp(kinked_dac(fc) * steepness);
		
		/* calculate the true value and estimate its derivate */
		w0 = 2.f * pi * f0[fc] / clock_frequency;
		 
		/* limit dt mode to 4 kHz */
		final float w0_max_dt = 2.f * pi * 4000.f / clock_frequency;
		w0_ceil_dt = w0 > w0_max_dt ? w0_max_dt : w0;
	}

	/**
	 * Set filter resonance.
	 */
	 private void set_Q() {
		// Q is controlled linearly by res. Q has approximate range [0.707, 1.7].
		// As resonance is increased, the filter must be clocked more often to keep
		// stable.
		_1_div_Q = 1.f / (0.707f + 1.0f * res / 15.f);
	}

	// ----------------------------------------------------------------------------
	// Spline functions.
	// ----------------------------------------------------------------------------

	/**
	 * Return the array of spline interpolation points used to map the FC
	 * register to filter cutoff frequency.
	 * 
	 * @param fcp
	 *            IN/OUT parameter points and count
	 */
	public void fc_default(SID.FCPoints fcp) {
		fcp.points = f0_points;
		fcp.count = f0_count;
	}

	// ----------------------------------------------------------------------------
	// Given an array of interpolation points p with n points, the following
	// statement will specify a new FC mapping:
	// interpolate(p, p + n - 1, filter.fc_plotter(), 1.0);
	// Note that the x range of the interpolation points *must* be [0, 2047],
	// and that additional end points *must* be present since the end points
	// are not interpolated.
	// ----------------------------------------------------------------------------
	public PointPlotter fc_plotter() {
		return new PointPlotter(f0);
	}

	// Our objective is to construct a smooth interpolating single-valued
	// function
	// y = f(x).
	//
	// Catmull-Rom splines are widely used for interpolation, however these are
	// parametric curves [x(t) y(t) ...] and can not be used to directly
	// calculate
	// y = f(x).
	// For a discussion of Catmull-Rom splines see Catmull, E., and R. Rom,
	// "A Class of Local Interpolating Splines", Computer Aided Geometric
	// Design.
	//
	// Natural cubic splines are single-valued functions, and have been used in
	// several applications e.g. to specify gamma curves for image display.
	// These splines do not afford local control, and a set of linear equations
	// including all interpolation points must be solved before any point on the
	// curve can be calculated. The lack of local control makes the splines
	// more difficult to handle than e.g. Catmull-Rom splines, and real-time
	// interpolation of a stream of data points is not possible.
	// For a discussion of natural cubic splines, see e.g. Kreyszig, E.,
	// "Advanced
	// Engineering Mathematics".
	//
	// Our approach is to approximate the properties of Catmull-Rom splines for
	// piecewice cubic polynomials f(x) = ax^3 + bx^2 + cx + d as follows:
	// Each curve segment is specified by four interpolation points,
	// p0, p1, p2, p3.
	// The curve between p1 and p2 must interpolate both p1 and p2, and in
	// addition
	// f'(p1.x) = k1 = (p2.y - p0.y)/(p2.x - p0.x) and
	// f'(p2.x) = k2 = (p3.y - p1.y)/(p3.x - p1.x).
	//
	// The constraints are expressed by the following system of linear equations
	//
	// [ 1 xi xi^2 xi^3 ] [ d ] [ yi ]
	// [ 1 2*xi 3*xi^2 ] * [ c ] = [ ki ]
	// [ 1 xj xj^2 xj^3 ] [ b ] [ yj ]
	// [ 1 2*xj 3*xj^2 ] [ a ] [ kj ]
	//
	// Solving using Gaussian elimination and back substitution, setting
	// dy = yj - yi, dx = xj - xi, we get
	//	 
	// a = ((ki + kj) - 2*dy/dx)/(dx*dx);
	// b = ((kj - ki)/dx - 3*(xi + xj)*a)/2;
	// c = ki - (3*xi*a + 2*b)*xi;
	// d = yi - ((xi*a + b)*xi + c)*xi;
	//
	// Having calculated the coefficients of the cubic polynomial we have the
	// choice of evaluation by brute force
	//
	// for (x = x1; x <= x2; x += res) {
	// y = ((a*x + b)*x + c)*x + d;
	// plot(x, y);
	// }
	//
	// or by forward differencing
	//
	// y = ((a*x1 + b)*x1 + c)*x1 + d;
	// dy = (3*a*(x1 + res) + 2*b)*x1*res + ((a*res + b)*res + c)*res;
	// d2y = (6*a*(x1 + res) + 2*b)*res*res;
	// d3y = 6*a*res*res*res;
	//	     
	// for (x = x1; x <= x2; x += res) {
	// plot(x, y);
	// y += dy; dy += d2y; d2y += d3y;
	// }
	//
	// See Foley, Van Dam, Feiner, Hughes, "Computer Graphics, Principles and
	// Practice" for a discussion of forward differencing.
	//
	// If we have a set of interpolation points p0, ..., pn, we may specify
	// curve segments between p0 and p1, and between pn-1 and pn by using the
	// following constraints:
	// f''(p0.x) = 0 and
	// f''(pn.x) = 0.
	//
	// Substituting the results for a and b in
	//
	// 2*b + 6*a*xi = 0
	//
	// we get
	//
	// ki = (3*dy/dx - kj)/2;
	//
	// or by substituting the results for a and b in
	//
	// 2*b + 6*a*xj = 0
	//
	// we get
	//
	// kj = (3*dy/dx - ki)/2;
	//
	// Finally, if we have only two interpolation points, the cubic polynomial
	// will degenerate to a straight line if we set
	//
	// ki = kj = dy/dx;
	//

	public class Coefficients {

		public double a;

		public double b;

		public double c;

		public double d;
	}

	/**
	 * Calculation of coefficients.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param k1
	 * @param k2
	 * @param coeff
	 */
	protected void cubic_coefficients(double x1, double y1, double x2,
			double y2, double k1, double k2, Coefficients coeff) {
		double dx = x2 - x1, dy = y2 - y1;

		coeff.a = ((k1 + k2) - 2 * dy / dx) / (dx * dx);
		coeff.b = ((k2 - k1) / dx - 3 * (x1 + x2) * coeff.a) / 2;
		coeff.c = k1 - (3 * x1 * coeff.a + 2 * coeff.b) * x1;
		coeff.d = y1 - ((x1 * coeff.a + coeff.b) * x1 + coeff.c) * x1;
	}

	/**
	 * Evaluation of cubic polynomial by brute force.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param k1
	 * @param k2
	 * @param plotter
	 * @param res
	 */
	protected void interpolate_brute_force(double x1, double y1, double x2,
			double y2, double k1, double k2, PointPlotter plotter, double res) {
		Coefficients coeff = new Coefficients();
		cubic_coefficients(x1, y1, x2, y2, k1, k2, coeff);

		// Calculate each point.
		for (double x = x1; x <= x2; x += res) {
			double y = ((coeff.a * x + coeff.b) * x + coeff.c) * x + coeff.d;
			plotter.plot(x, y);
		}
	}

	/**
	 * Evaluation of cubic polynomial by forward differencing.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param k1
	 * @param k2
	 * @param plotter
	 * @param res
	 */
	protected void interpolate_forward_difference(double x1, double y1,
			double x2, double y2, double k1, double k2, PointPlotter plotter,
			double res) {
		Coefficients coeff = new Coefficients();
		cubic_coefficients(x1, y1, x2, y2, k1, k2, coeff);

		double y = ((coeff.a * x1 + coeff.b) * x1 + coeff.c) * x1 + coeff.d;
		double dy = (3 * coeff.a * (x1 + res) + 2 * coeff.b) * x1 * res
				+ ((coeff.a * res + coeff.b) * res + coeff.c) * res;
		double d2y = (6 * coeff.a * (x1 + res) + 2 * coeff.b) * res * res;
		double d3y = 6 * coeff.a * res * res * res;

		// Calculate each point.
		for (double x = x1; x <= x2; x += res) {
			plotter.plot(x, y);
			y += dy;
			dy += d2y;
			d2y += d3y;
		}
	}

	protected double x(int[] /* fc_point */f0_base[], int p) {
		return (f0_base[p])[0];
	}

	protected double y(int[] /* fc_point */f0_base[], int p) {
		return (f0_base[p])[1];
	}

	/**
	 * Evaluation of complete interpolating function. Note that since each curve
	 * segment is controlled by four points, the end points will not be
	 * interpolated. If extra control points are not desirable, the end points
	 * can simply be repeated to ensure interpolation. Note also that points of
	 * non-differentiability and discontinuity can be introduced by repeating
	 * points.
	 * 
	 * @param p0
	 * @param pn
	 * @param plotter
	 * @param res
	 */
	public void interpolate(int[] /* fc_point */f0_base[], int p0, int pn,
			PointPlotter plotter, double res) {
		double k1, k2;

		// Set up points for first curve segment.
		int p1 = p0;
		++p1;
		int p2 = p1;
		++p2;
		int p3 = p2;
		++p3;

		// Draw each curve segment.
		for (; p2 != pn; ++p0, ++p1, ++p2, ++p3) {
			// p1 and p2 equal; single point.
			if (x(f0_base, p1) == x(f0_base, p2)) {
				continue;
			}
			// Both end points repeated; straight line.
			if (x(f0_base, p0) == x(f0_base, p1)
					&& x(f0_base, p2) == x(f0_base, p3)) {
				k1 = k2 = (y(f0_base, p2) - y(f0_base, p1))
						/ (x(f0_base, p2) - x(f0_base, p1));
			}
			// p0 and p1 equal; use f''(x1) = 0.
			else if (x(f0_base, p0) == x(f0_base, p1)) {
				k2 = (y(f0_base, p3) - y(f0_base, p1))
						/ (x(f0_base, p3) - x(f0_base, p1));
				k1 = (3 * (y(f0_base, p2) - y(f0_base, p1))
						/ (x(f0_base, p2) - x(f0_base, p1)) - k2) / 2;
			}
			// p2 and p3 equal; use f''(x2) = 0.
			else if (x(f0_base, p2) == x(f0_base, p3)) {
				k1 = (y(f0_base, p2) - y(f0_base, p0))
						/ (x(f0_base, p2) - x(f0_base, p0));
				k2 = (3 * (y(f0_base, p2) - y(f0_base, p1))
						/ (x(f0_base, p2) - x(f0_base, p1)) - k1) / 2;
			}
			// Normal curve.
			else {
				k1 = (y(f0_base, p2) - y(f0_base, p0))
						/ (x(f0_base, p2) - x(f0_base, p0));
				k2 = (y(f0_base, p3) - y(f0_base, p1))
						/ (x(f0_base, p3) - x(f0_base, p1));
			}

			if (SPLINE_BRUTE_FORCE) {
				interpolate_brute_force(x(f0_base, p1), y(f0_base, p1), x(
						f0_base, p2), y(f0_base, p2), k1, k2, plotter, res);
			} else {
				interpolate_forward_difference(x(f0_base, p1), y(f0_base, p1),
						x(f0_base, p2), y(f0_base, p2), k1, k2, plotter, res);
			}
		}
	}

	// ----------------------------------------------------------------------------
	// END Spline functions.
	// ----------------------------------------------------------------------------

}
