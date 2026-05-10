package bp.ext;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;
import javax.swing.Action;

import bp.context.BPFileContext;
import bp.data.BPDataHolder;
import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPActionHelperCFs;
import bp.ui.editor.BPEditorActionManager;
import bp.ui.editor.BPImagePanel;
import bp.ui.editor.BPJSONPanel;
import bp.ui.editor.BPRawEditor;
import bp.ui.util.CommonDataUIProcs;
import bp.ui.util.UIStd;
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

	public final static Action[] getImageBarActions(BPImagePanel panel)
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
	
	public void install(BPFileContext context)
	{
		BPLocaleHelpers.registerHelper(new BPActionHelperCFs());
		CommonDataUIProcs.registerProc(CommonDataUIProcs.MODE_DATA_IMAGE, BPExtensionLoaderGUICFs::createImagePanel, (BiConsumer<BPImagePanel, Object>) BPExtensionLoaderGUICFs::initImagePanel);
	}

	private final static BPImagePanel createImagePanel(Object data)
	{
		return new BPImagePanel();
	}

	private static void initImagePanel(BPImagePanel p, Object imgobj)
	{
		Image img = (Image) imgobj;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
		{
			ImageIO.write((RenderedImage) img, "PNG", bos);
			BPDataHolder dh = new BPDataHolder();
			dh.setData(bos.toByteArray());
			p.bind(dh);
		}
		catch (IOException e)
		{
			UIStd.err(e);
		}
		finally
		{
		}
	}

	public void preload()
	{
		BPEditorActionManager.registerBarActionFactories(BPImagePanel.class, BPExtensionLoaderGUICFs::getImageBarActions);
		BPEditorActionManager.registerBarActionFactories(BPJSONPanel.class, BPExtensionActionJSON::getCloneActions);
		BPEditorActionManager.registerBarActionFactories(BPRawEditor.class, BPExtensionActionRawCFs::getRawActions);
	}
}
