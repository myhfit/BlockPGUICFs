package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;
import bp.ui.actions.BPActionConstCommon;

public class BPToolFactoryGUICFs implements BPToolFactory
{
	public String getName()
	{
		return "GUICFs";
	}

	public boolean canRunAt(BPPlatform platform)
	{
		return platform == BPPlatform.GUI_SWING;
	}

	public void install(BiConsumer<String, BPTool> installfunc, BPPlatform platform)
	{
		String pmcommon = BPActionConstCommon.TXT_COMMON.text();
		String pmdeen = BPActionConstCommon.TXT_DE_EN.text();
		String pmhash = BPActionConstCommon.TXT_HASH.text();
		String pmgen = BPActionConstCommon.TXT_GENERATOR.text();
		installfunc.accept(pmdeen, new BPToolGUIStringEscape());
		installfunc.accept(pmdeen, new BPToolGUIBase64());
		installfunc.accept(pmdeen, new BPToolGUIURLEncoding());
		installfunc.accept(pmdeen, new BPToolGUIJSON());
		installfunc.accept(pmhash, new BPToolGUIMessageDigest());
		installfunc.accept(pmcommon, new BPToolGUIClipboard());
		installfunc.accept(pmcommon, new BPToolGUIRegEx());
		installfunc.accept(pmcommon, new BPToolGUITime());
		installfunc.accept(pmgen, new BPToolGUIImageGenerator());
		installfunc.accept(pmgen, new BPToolGUIRandomGenerator());
		installfunc.accept(pmgen, new BPToolGUIIDGenerator());
	}
}
