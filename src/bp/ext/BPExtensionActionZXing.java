package bp.ext;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import bp.ui.actions.BPAction;
import bp.ui.editor.BPImagePanel;
import bp.ui.res.icon.BPIconResV;
import bp.ui.util.UIStd;

public class BPExtensionActionZXing
{
	public final static Action getQRCodeAction(BPImagePanel panel)
	{
		WeakReference<BPImagePanel> ref = new WeakReference<BPImagePanel>(panel);
		return BPAction.build("QRCode").callback((e) ->
		{
			Image img = ref.get().getImageComponent().getImage();
			LuminanceSource source = new BufferedImageLuminanceSource((BufferedImage) img);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
			formats.add(BarcodeFormat.QR_CODE);
			hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
			try
			{
				Result result = new MultiFormatReader().decode(binaryBitmap, hints);
				String txt = result.getText();
				UIStd.textarea(txt, "Source");
			}
			catch (NotFoundException nfe)
			{
				UIStd.info("Not Found");
			}
		}).vIcon(BPIconResV.CLONE()).name("QRCode").tooltip("Scan QRCode").getAction();
	}
}
