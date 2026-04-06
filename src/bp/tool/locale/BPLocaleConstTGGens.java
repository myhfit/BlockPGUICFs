package bp.tool.locale;

import bp.locale.BPLocaleConstDirect;

public enum BPLocaleConstTGGens implements BPLocaleConstDirect
{
	ACT_BTNONELINE("One Line"),
	ACT_BTNCLEAR("Clear"),
	ACT_BTNGEN("Generate"),
	ACT_BTNCOUNT("Count"),
	;

	private String m_value;

	public final static String PACK_TGGENS = "tg_gens";

	public String getPackName()
	{
		return PACK_TGGENS;
	}

	public String getValue(int flag)
	{
		return m_value;
	}

	private BPLocaleConstTGGens(String v)
	{
		m_value = v;
	}
}
