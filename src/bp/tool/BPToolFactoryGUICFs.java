package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;

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
		installfunc.accept("Decode/Encode", new BPToolGUIStringEscape());
		installfunc.accept("Decode/Encode", new BPToolGUIBase64());
		installfunc.accept("Decode/Encode", new BPToolGUIURLEncoding());
		installfunc.accept("Decode/Encode", new BPToolGUIJSON());
		installfunc.accept("Hash", new BPToolGUIMessageDigest());
		installfunc.accept("Common", new BPToolGUIClipboard());
		installfunc.accept("Common", new BPToolGUIRegEx());
		installfunc.accept("Common", new BPToolGUITime());
		installfunc.accept("Generator", new BPToolGUIImageGenerator());
		installfunc.accept("Generator", new BPToolGUIRandomGenerator());
		installfunc.accept("Generator", new BPToolGUIIDGenerator());
	}
}
