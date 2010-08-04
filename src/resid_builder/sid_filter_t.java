package resid_builder;

public class sid_filter_t {
	public int /* sid_fc_t */cutoff[][] = new int[0x800][2];

	public int /* uint_least16_t */points;

	/* distortion tunables */
	public boolean distortion_enable;
	public float rate, point;
	/* type 3 tunables */
	public float baseresistance, offset, steepness, minimumfetresistance;

	public float resonanceFactor;

	public int type;
}