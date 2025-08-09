package bp.ext;

import javax.swing.Action;

import bp.ui.actions.BPAction;
import bp.ui.editor.BPJSONPanel;
import bp.ui.res.icon.BPIconResV;

public class BPExtensionActionJSON
{
	public final static Action[] getCloneActions(BPJSONPanel panel)
	{
		// WeakReference<BPJSONPanel> ref = new
		// WeakReference<BPJSONPanel>(panel);
		Action act = BPAction.build("Clone").callback((e) ->
		{
			// Image img = ref.get().getImageComponent().getImage();
			// LuminanceSource source = new
			// BufferedImageLuminanceSource((BufferedImage) img);
			// Binarizer binarizer = new HybridBinarizer(source);
			// BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			// Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType,
			// Object>();
			// hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			// List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
			// formats.add(BarcodeFormat.QR_CODE);
			// hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
			// try
			// {
			// Result result = new MultiFormatReader().decode(binaryBitmap,
			// hints);
			// String txt = result.getText();
			// UIStd.textarea(txt, "Source");
			// }
			// catch (NotFoundException nfe)
			// {
			// UIStd.info("Not Found");
			// }
		}).vIcon(BPIconResV.CLONE()).name("Clone").tooltip("Clone").getAction();
		return new Action[] { act };
	}
}
