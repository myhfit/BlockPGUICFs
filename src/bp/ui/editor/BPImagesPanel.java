package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import bp.BPGUICore;
import bp.config.BPConfig;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerArchive;
import bp.data.BPDataContainerBase;
import bp.data.BPDataContainerFileSystem;
import bp.format.BPFormat;
import bp.format.BPFormatBMP;
import bp.format.BPFormatFeature;
import bp.format.BPFormatGIF;
import bp.format.BPFormatJPEG;
import bp.format.BPFormatManager;
import bp.format.BPFormatPNG;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceIO;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPEditors.BPEventUIEditors;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPImage;
import bp.util.ClipboardUtil;
import bp.util.FileUtil;
import bp.util.IOUtil;
import bp.util.Std;

public class BPImagesPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7009889832393872251L;

	protected BPDataContainer m_con;
	protected List<BPResource> m_children;
	protected int m_seli;
	protected BPImage m_ctl;
	protected WeakReference<Consumer<String>> m_dynainfo;
	protected String m_info;
	protected int m_channelid;
	protected BPToolBarSQ m_toolbar;
	protected Action[] m_acts;

	protected final static String[] S_SUPPORTED_FORMATS = new String[] { BPFormatBMP.FORMAT_BMP, BPFormatGIF.FORMAT_GIF, BPFormatJPEG.FORMAT_JPEG, BPFormatPNG.FORMAT_PNG };

	private String m_id;

	static
	{
		ImageIO.setUseCache(false);
	}

	public BPImagesPanel()
	{
		init();
	}

	protected void init()
	{
		m_seli = -1;
		setLayout(new BorderLayout());
		m_ctl = new BPImage();
		m_toolbar = new BPToolBarSQ(true);
		m_toolbar.setBorderVertical(0);

		BPAction actprev = BPAction.build(">").callback((e) -> prev()).vIcon(BPIconResV.TOLEFT()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)).tooltip("Prev").getAction();
		BPAction actnext = BPAction.build("<").callback((e) -> next()).vIcon(BPIconResV.TORIGHT()).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)).tooltip("Next").getAction();
		BPAction actzoomin = BPAction.build("+").callback((e) -> zoomin()).vIcon(BPIconResV.ADD()).tooltip("Zoom In").getAction();
		BPAction actzoomout = BPAction.build("-").callback((e) -> zoomout()).vIcon(BPIconResV.DEL()).tooltip("Zoom Out").getAction();
		m_toolbar.setActions(new Action[] { BPAction.separator(), actprev, actnext, actzoomin, actzoomout }, this);

		add(m_ctl, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
		initActions();
	}

	protected void initActions()
	{
		m_acts = new Action[] {};
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.IMAGEEDITOR;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void prev()
	{
		showImageDelta(-1);
	}

	public void next()
	{
		showImageDelta(1);
	}

	public void zoomin()
	{
		m_ctl.zoomDelta(1);
	}

	public void zoomout()
	{
		m_ctl.zoomDelta(-1);
	}

	protected void onCopy(ActionEvent e)
	{
		copy();
	}

	public void copy()
	{
		ClipboardUtil.setImage(m_ctl.getImage());
	}

	public void bind(BPDataContainer con, boolean noread)
	{
		m_con = con;
		List<String> formats = Arrays.asList(S_SUPPORTED_FORMATS);
		List<BPResource> children = new ArrayList<BPResource>();
		if (!noread)
		{
			BPResource res = con.getResource();
			if (res.isFileSystem())
			{
				BPResourceFileSystem fres = (BPResourceFileSystem) res;
				if (fres.isDirectory())
				{
					BPResource[] subfs = fres.listResources();
					for (BPResource subf : subfs)
					{
						if (subf.isFileSystem())
						{
							BPResourceFileSystem f = (BPResourceFileSystem) subf;
							if (f.isFile())
							{
								String ext = f.getExt();
								BPFormat format = BPFormatManager.getFormatByExt(ext);
								if (format != null && formats.contains(format.getName()))
								{
									children.add(f);
								}
							}
						}
					}
				}
				else if (con instanceof BPDataContainerFileSystem)
				{
					con.open();
					BPDataContainerFileSystem confs = (BPDataContainerFileSystem) con;
					confs.readFull(this::checkEntry);
					BPResource[] subfs = confs.listResources();
					for (BPResource subf : subfs)
					{
						children.add(subf);
					}
				}
			}
		}
		initImageList(children);
	}

	protected boolean checkEntry(String name, boolean isdir)
	{
		if (isdir)
			return false;
		String ext = FileUtil.getExt(name);
		BPFormat format = BPFormatManager.getFormatByExt(ext);
		boolean flag = false;
		if (format != null)
		{
			for (int i = 0; i < S_SUPPORTED_FORMATS.length; i++)
			{
				if (S_SUPPORTED_FORMATS[i].equals(format.getName()))
				{
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	protected void initImageList(List<BPResource> list)
	{
		m_children = list;
		int seli = -1;
		if (list.size() > 0)
		{
			seli = 0;
		}
		showImage(seli);
	}

	protected void showImageDelta(int delta)
	{
		int s = m_children.size();
		int seli = m_seli + delta;
		if (seli < 0)
			seli = 0;
		if (seli >= s)
			seli = s - 1;
		showImage(seli);
	}

	protected void showImage(int index)
	{
		if (index == m_seli)
			return;
		m_seli = index;
		BPResource res = null;
		if (index > -1)
		{
			res = m_children.get(index);
		}
		showImage(res);
	}

	protected void showImage(BPResource res)
	{
		byte[] bs = null;
		if (res.isIO())
		{
			BPResourceIO io = (BPResourceIO) res;
			if (io.canOpen())
			{
				bs = io.useInputStream(in -> IOUtil.read(in));
			}
		}
		setImageData(bs);
	}

	protected void setImageData(byte[] bs)
	{
		if (bs != null)
		{
			try (ByteArrayInputStream bis = new ByteArrayInputStream(bs))
			{
				Image img = ImageIO.read(bis);
				int h = img.getHeight(null);
				int w = img.getWidth(null);
				m_info = w + "x" + h + " @ " + m_children.get(m_seli).getName() + " " + (m_seli + 1) + "/" + m_children.size();
				m_ctl.resetAndSetImage(img);
				BPEventUIEditors event = new BPEventUIEditors(BPEventUIEditors.EDITOR_STATUS_CHANGED, m_id, this, m_info);
				BPGUICore.EVENTS_UI.trigger(m_channelid, event);
			}
			catch (IOException e)
			{
				Std.err(e);
			}
		}
		else
			m_ctl.resetAndSetImage(null);
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

	public void unbind()
	{
		m_con.close();
		m_con = null;
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
		this.requestFocus();
	}

	public void save()
	{
	}

	public String getImageFormat()
	{
		String ext = m_con.getResource().getExt();
		Std.info(ext);
		if (ext != null)
		{
			return BPFormatManager.getFormatByExt(ext).getName();
		}
		return "png";
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

	public final static class BPEditorFactoryImages implements BPEditorFactory
	{
		public String[] getFormats()
		{
			List<BPFormat> fs = BPFormatManager.getFormatsByFeature(BPFormatFeature.PATHTREE);
			String[] fnames = new String[fs.size()];
			for (int i = 0; i < fs.size(); i++)
			{
				fnames[i] = fs.get(i).getName();
			}
			return fnames;
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPImagesPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				if (res.isFileSystem())
				{
					BPResourceFileSystem resfs = (BPResourceFileSystem) res;
					BPDataContainer con = null;
					if (resfs.isDirectory())
					{
						con = new BPDataContainerBase();
					}
					else
					{
						if (format.checkFeature(BPFormatFeature.ARCHIVE))
						{
							con = new BPDataContainerArchive();
						}
					}
					if (con != null)
					{
						con.bind(res);
						((BPImagesPanel) editor).bind(con, false);
					}
				}
			}
		}

		public String getName()
		{
			return "Images Viewer";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}
	}
}