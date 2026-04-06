package bp.ui.actions;

import java.util.Map;

public class BPActionHelperCFs extends BPActionHelperBase<BPActionConstCFs>
{
	public final static String ACTIONHELPER_PACK_CFS = "cfs";

	public String getPackName()
	{
		return ACTIONHELPER_PACK_CFS;
	}

	public void initDefaults(Map<Integer, Object> actmap)
	{
		putAction(actmap, BPActionConstCFs.TXT_CLIPBOARD, "Clipboard", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TXT_REGEXP, "Regular Expression", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TNAME_MSGDIGEST, "Message Digest", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TNAME_IDGEN, "ID Generator", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TNAME_IMGGEN, "Image Generator", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TNAME_RANDOMGEN, "Random Number Generator", null, null, null, null);
		putAction(actmap, BPActionConstCFs.TNAME_URLENCODING, "URL Encoding", null, null, null, null);
	}

	protected Class<BPActionConstCFs> getConstClass()
	{
		return BPActionConstCFs.class;
	}
}
