package bp.ext;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;

import javax.swing.Action;

import bp.BPGUICore;
import bp.data.BPDataContainerRandomAccess;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.editor.BPRawEditor;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPImage;
import bp.ui.util.UIStd;
import bp.util.ObjUtil;

public class BPExtensionActionRawCFs
{
	public final static Action[] getRawActions(BPRawEditor panel)
	{
		WeakReference<BPRawEditor> ref = new WeakReference<BPRawEditor>(panel);
		Action act = BPAction.build("Overview(Image)").callback((e) -> onShowOverviewImage(ref.get())).vIcon(BPIconResV.IMG()).name("Overview(Image)").tooltip("Overview(Image)").getAction();
		return new Action[] { act };
	}

	protected final static void onShowOverviewImage(BPRawEditor editor)
	{
		BPDataContainerRandomAccess con = editor.getDataContainer();
		long l = con.length();
		int rowsize = ObjUtil.toInt(UIStd.input("16", "Row size:", "Input row size"), 16);
		int scale = ObjUtil.toInt(UIStd.input("16", "Scale:", "Input scale"), 1);
		int h = (int) Math.ceil((double) l / (double) rowsize / scale);
		BufferedImage img = new BufferedImage(rowsize, h, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster r = img.getRaster();
		long pos = 0;
		int blocksize = rowsize * scale;
		byte[] bs = new byte[blocksize];
		int bi;
		long sum;
		while (pos < l)
		{
			for (int ri = 0; ri < h; ri++)
			{
				int c = con.read(pos, bs, 0, blocksize);
				for (int x = 0; x < rowsize; x++)
				{
					bi = x;
					sum = 0;
					for (int j = 0; j < scale; j++, bi += rowsize)
					{
						if (bi >= c)
							break;
						sum += bs[bi];
					}
					r.setPixel(x, ri, new int[] { 255 - (int) ((float) sum / (float) scale) });
				}
				pos += blocksize;
			}
		}
		BPImage imgcomp = new BPImage();
		imgcomp.resetAndSetImage(img);
		imgcomp.setPreferredSize(new Dimension(800, 600));
		imgcomp.setSize(new Dimension(800, 600));
		imgcomp.zoom(1f);
		imgcomp.setZoomIntMode(true);
		imgcomp.setSize(new Dimension());
		BPDialogSimple.showComponent(imgcomp, BPDialogSimple.COMMANDBAR_OKESCAPE, null, BPGUICore.S_BP_TITLE + " - Overview(Image)", editor.getFocusCycleRootAncestor());
	}
}