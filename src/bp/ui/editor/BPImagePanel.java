package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatBMP;
import bp.format.BPFormatGIF;
import bp.format.BPFormatJPEG;
import bp.format.BPFormatManager;
import bp.format.BPFormatPNG;
import bp.res.BPResource;
import bp.tool.BPToolGUIDataPipe;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPImage;
import bp.ui.util.UIUtil;
import bp.util.ClipboardUtil;
import bp.util.Std;

public class BPImagePanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7009889832393872251L;
	protected BPDataContainer m_con;
	protected BPImage m_ctl;
	protected WeakReference<Consumer<String>> m_dynainfo = null;
	protected String m_info;
	protected int m_channelid;
	protected BPToolBarSQ m_toolbar;
	protected Action m_actcopy;
	protected Action m_actpaste;
	protected Action[] m_acts;
	protected String m_id;

	static
	{
		ImageIO.setUseCache(false);
	}

	public BPImagePanel()
	{
		init();
	}

	protected void init()
	{
		setFocusable(true);
		setLayout(new BorderLayout());
		m_ctl = new BPImage();
		m_toolbar = new BPToolBarSQ(true);
		m_toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(1, 1, 1, 1)));
		BPAction actzoomin = BPAction.build("+").callback((e) -> zoomin()).vIcon(BPIconResV.ADD()).tooltip("Zoom In").getAction();
		BPAction actzoomout = BPAction.build("-").callback((e) -> zoomout()).vIcon(BPIconResV.DEL()).tooltip("Zoom Out").getAction();
		BPAction actdatapipe = BPAction.build("pipe").callback(this::sendToDataPipe).vIcon(BPIconResV.PRJSTREE()).tooltip("Send to Data Pipe").getAction();
		m_toolbar.setActions(new Action[] { actzoomin, actzoomout, actdatapipe });
		add(m_ctl, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
		initActions();
	}

	protected void initActions()
	{
		m_actcopy = BPAction.build("Copy").callback(this::onCopy).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)).mnemonicKey(KeyEvent.VK_C).getAction();
		m_actpaste = BPAction.build("Paste").callback(this::onPaste).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)).mnemonicKey(KeyEvent.VK_P).getAction();
		m_acts = new Action[] { m_actcopy, m_actpaste };
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.IMAGEEDITOR;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void zoomin()
	{
		m_ctl.zoomDelta(1);
	}

	public void zoomout()
	{
		m_ctl.zoomDelta(-1);
	}

	protected void sendToDataPipe(ActionEvent e)
	{
		BPToolGUIDataPipe tool = new BPToolGUIDataPipe();
		tool.showTool(m_ctl.getImage(), getImageFormat());
	}

	protected void onPaste(ActionEvent e)
	{
		paste();
	}

	protected void onCopy(ActionEvent e)
	{
		copy();
	}

	public void copy()
	{
		ClipboardUtil.setImage(m_ctl.getImage());
	}

	public void paste()
	{
		Image img = ClipboardUtil.getImage();
		if (m_ctl.getImage() == null)
		{
			m_ctl.setImage(img);
		}
		else
		{
			m_ctl.pasteImage(img);
		}
	}

	public void bind(BPDataContainer con, boolean noread)
	{
		m_con = con;
		if (!noread && con.canOpen())
		{
			m_con.open();
			m_con.useInputStream(bis ->
			{
				try
				{
					Image img = ImageIO.read(bis);
					int h = img.getHeight(null);
					int w = img.getWidth(null);
					m_info = w + "x" + h;
					UIUtil.laterUI(() -> m_ctl.setImage(img));
					return true;
				}
				catch (IOException e)
				{
					Std.err(e);
					return false;
				}
			});
		}
	}

	public BPImage getImageComponent()
	{
		return m_ctl;
	}

	protected void sendDynamicInfo(String info)
	{
		WeakReference<Consumer<String>> dynainfo = m_dynainfo;
		if (dynainfo != null)
		{
			Consumer<String> cb = dynainfo.get();
			if (cb != null)
			{
				cb.accept(info);
			}
		}
	}

	protected boolean onUpdateImage(Image img, int infoflags, int x, int y, int width, int height)
	{
		return true;
	}

	public void unbind()
	{
		BPDataContainer con = m_con;
		m_con = null;
		try
		{
			con.close();
		}
		catch (Exception e)
		{
		}
	}

	public void clearResource()
	{
		if (m_con != null)
		{
			m_con.close();
			m_con = null;
		}
	}

	public BPDataContainer getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
		requestFocus();
	}

	public void save()
	{
		if (m_con == null)
			return;
		Image img = m_ctl.getImage();
		if (img == null)
			return;
		BPDataContainer con = m_con;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		con.open();
		try
		{
			ImageIO.write((RenderedImage) img, getImageFormat(), bos);
			con.writeAll(bos.toByteArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			con.close();
		}
	}

	public String getImageFormat()
	{
		String ext = m_con.getResource().getExt();
		if (ext != null)
		{
			return BPFormatManager.getFormatByExt(ext).getName();
		}
		return "PNG";
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return false;
	}

	public void setNeedSave(boolean needsave)
	{
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public String getEditorInfo()
	{
		return m_info;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	public Action[] getEditMenuActions()
	{
		return m_acts;
	}

	public String[] getExts()
	{
		return new String[] { ".png", ".jpg", ".jpeg", ".gif", ".bmp" };
	}

	public final static class BPEditorFactoryImage implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatJPEG.FORMAT_JPEG, BPFormatPNG.FORMAT_PNG, BPFormatGIF.FORMAT_GIF, BPFormatBMP.FORMAT_BMP };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPImagePanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				BPDataContainer con = new BPDataContainerBase();
				con.bind(res);
				((BPImagePanel) editor).bind(con, false);
			}
		}

		public String getName()
		{
			return "Image Editor";
		}
	}
}
