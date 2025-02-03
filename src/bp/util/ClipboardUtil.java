package bp.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ClipboardUtil
{
	public final static boolean[] checkClassFromClipboard(Clipboard cl)
	{
		Transferable tf = cl.getContents(null);
		DataFlavor[] dfs = tf.getTransferDataFlavors();
		return checkClassFromDataFlavers(dfs);
	}

	public final static boolean[] checkClassFromDataFlavers(DataFlavor[] dfs)
	{
		boolean hastext = false;
		boolean hasimage = false;
		boolean hasfile = false;
		for (DataFlavor df : dfs)
		{
			Class<?> cls = df.getRepresentationClass();
			if (cls == String.class)
			{
				hastext = true;
			}
			else if (cls == Image.class)
			{
				hasimage = true;
			}
		}
		return new boolean[] { hastext, hasimage, hasfile };
	}

	public final static void setImage(Image img)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new BPImageTransferable(img), null);
	}

	public final static Image getImage()
	{
		Clipboard cl = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tf = cl.getContents(null);
		DataFlavor[] dfs = tf.getTransferDataFlavors();
		boolean[] dfcls = checkClassFromDataFlavers(dfs);
		BufferedImage rc = null;
		if (dfcls[1])
		{
			try
			{
				rc = (BufferedImage) tf.getTransferData(DataFlavor.imageFlavor);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	protected static class BPImageTransferable implements Transferable
	{
		private Image m_img;

		public BPImageTransferable(Image img)
		{
			m_img = img;
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return DataFlavor.imageFlavor.equals(flavor);
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			return m_img;
		}
	}
}
