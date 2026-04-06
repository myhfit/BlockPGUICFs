package bp.tool.locale;

import bp.locale.BPLocaleConstDirect;

public enum BPLocaleConstTGTime implements BPLocaleConstDirect
{
	RAWTIME("Raw Time"), LOCALTIME("Local Time"), TARTIME("Target Time"), TIMEZONE("TimeZone"),
	ACT_BTNGETCUR("Get Current")
	;

	private String m_value;

	public final static String PACK_TGTIME = "tg_time";

	public String getPackName()
	{
		return PACK_TGTIME;
	}

	public String getValue(int flag)
	{
		return m_value;
	}

	private BPLocaleConstTGTime(String v)
	{
		m_value = v;
	}
}
