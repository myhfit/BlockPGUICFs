package bp.ui.actions;

public enum BPActionConstCFs implements BPActionConst
{
	TXT_CLIPBOARD,
	TXT_REGEXP,
	TNAME_MSGDIGEST,
	TNAME_IDGEN,
	TNAME_IMGGEN,
	TNAME_RANDOMGEN,
	TNAME_URLENCODING,
	
	CTX_MNUBOLD,
	CTX_MNUBOLD_ACC,
	CTX_MNUITALIC,
	;

	public String getPackName()
	{
		return "ac_cfs";
	}
}
