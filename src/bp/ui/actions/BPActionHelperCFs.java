package bp.ui.actions;

public class BPActionHelperCFs extends BPActionHelperBase<BPActionConstCFs>
{
	public final static String ACTIONHELPER_PACK_CFS = "cfs";

	public String getPackName()
	{
		return ACTIONHELPER_PACK_CFS;
	}

	public void initDefaults()
	{
		putAction(BPActionConstCFs.TXT_CLIPBOARD, "Clipboard", null, null, null, null);
		putAction(BPActionConstCFs.TXT_REGEXP, "Regular Expression", null, null, null, null);
	}

	protected Class<BPActionConstCFs> getConstClass()
	{
		return BPActionConstCFs.class;
	}
}
