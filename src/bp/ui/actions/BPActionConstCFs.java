package bp.ui.actions;

public enum BPActionConstCFs implements BPActionConst
{
	TXT_CLIPBOARD,
	TXT_REGEXP,
	TNAME_MSGDIGEST,
	TNAME_IDGEN,
	TNAME_IMGGEN,
	TNAME_RANDOMGEN,
	TNAME_URLENCODING
	
	;

	public String getPackName()
	{
		return "cfs";
	}
}
