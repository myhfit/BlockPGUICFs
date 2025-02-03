package bp.ext;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.ui.editor.BPEditorActionManager;
import bp.ui.editor.BPImagePanel;
import bp.util.Std;

public class BPExtensionLoaderGUICFs implements BPExtensionLoaderGUISwing
{
	public String getName()
	{
		return "CommonFormats GUI-Swing";
	}

	public String[] getParentExts()
	{
		return new String[] { "GUI-Swing", "CommonFormats" };
	}

	public String[] getDependencies()
	{
		return null;
	}

	public final static Action[] getBarActions(BPImagePanel panel)
	{
		List<Action> acts = new ArrayList<Action>();
		try
		{
			acts.add(BPExtensionActionZXing.getQRCodeAction(panel));
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return acts.toArray(new Action[acts.size()]);
	}

	public void preload()
	{
		BPEditorActionManager.registerBarActionFactories(BPImagePanel.class, BPExtensionLoaderGUICFs::getBarActions);
	}
}
