package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.IOUtil;
import bp.util.SecUtil;

public class BPToolGUIMessageDigest extends BPToolGUIBase<BPToolGUIMessageDigest.BPToolGUIContextMessageDigest>
{
	public String getName()
	{
		return "Message Digest";
	}

	protected BPToolGUIContextMessageDigest createToolContext()
	{
		return new BPToolGUIContextMessageDigest();
	}

	protected static class BPToolGUIContextMessageDigest implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_src;
		protected BPCodePane m_dest;
		protected BPTextField m_encoding;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;
		protected BPComboBox<String> m_al;
		protected BPComboBox<String> m_cbmodes;
		protected String[] m_filenames;

		public void initUI(Container par, Object... params)
		{
			m_src = new BPCodePane();
			m_dest = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			JPanel px = new JPanel();
			m_encoding = new BPTextField();
			BPLabel lblmodes = new BPLabel("Mode:");
			BPLabel lblen = new BPLabel("Encoding:");
			BPLabel lblal = new BPLabel("Algorithm:");
			m_al = new BPComboBox<String>();
			BPLabel lblsrc = new BPLabel(" Source");
			BPLabel lbldest = new BPLabel(" Result");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			lblmodes.setLabelFont();
			m_cbmodes = new BPComboBox<String>();
			initModes();
			Action actrun = BPAction.build(" Run ").callback(this::onRun).getAction();
			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actrun });
			toolbar.add(Box.createRigidArea(new Dimension(8, 4)));
			toolbar.add(lblmodes);
			toolbar.add(m_cbmodes);
			toolbar.add(Box.createRigidArea(new Dimension(8, 4)));
			toolbar.add(lblen);
			toolbar.add(m_encoding);
			toolbar.add(Box.createRigidArea(new Dimension(8, 4)));
			toolbar.add(lblal);
			toolbar.add(m_al);

			toolbar.add(px);

			px.setPreferredSize(new Dimension(5000, 0));
			m_encoding.setHorizontalAlignment(JTextField.CENTER);
			m_encoding.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_al.setMonoFont();
			m_al.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(200, 0)));
			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_src.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_encoding.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_al.replaceWBorder();

			m_src.setMonoFont();
			m_dest.setMonoFont();
			m_encoding.setMonoFont();
			m_al.setMonoFont();
			m_cbmodes.setMonoFont();
			lblmodes.setLabelFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();
			lblen.setLabelFont();
			lblal.setLabelFont();
			m_dest.setEditable(false);

			m_encoding.addMouseListener(new UIUtil.BPMouseListener(this::onSelectEncoding, null, null, null, null));

			sp.add(psrc);
			sp.add(pdest);
			psrc.setLayout(new BorderLayout());
			pdest.setLayout(new BorderLayout());
			m_dest.setBorder(new EmptyBorder(0, 0, 0, 0));
			lblsrc.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			lbldest.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			psrc.add(lblsrc, BorderLayout.NORTH);
			pdest.add(lbldest, BorderLayout.NORTH);
			psrc.add(m_scrollsrc, BorderLayout.CENTER);
			pdest.add(m_scrolldest, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);
		}

		protected void initModes()
		{
			List<String> modes = new ArrayList<String>();
			modes.add("Text>Text");
			modes.add("File>Text");
			modes.add("Text>File");
			modes.add("File>File");
			BPComboBoxModel<String> model = new BPComboBoxModel<String>();
			model.setDatas(modes);
			m_cbmodes.setModel(model);
			m_cbmodes.setSelectedIndex(0);

			m_cbmodes.addItemListener(this::onModeChanged);
		}

		protected void onModeChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				int mode = m_cbmodes.getSelectedIndex();
				if (mode == 1 || mode == 3)
				{
					if (m_filenames == null || m_filenames.length == 0)
					{
						String[] filenames = CommonUIOperations.showOpenFilesDialog((Window) m_src.getFocusCycleRootAncestor());
						setFilenames(filenames);
					}
				}
				else
				{
					m_filenames = null;
				}
			}
		}

		public void setFilenames(String[] filenames)
		{
			m_filenames = filenames;
			if (filenames != null && filenames.length == 0)
			{
				m_filenames = null;
			}
			if (m_filenames == null)
				m_src.setText("");
			else
				m_src.setText("[" + String.join(",", filenames) + "]");
		}

		public void initDatas(Object... params)
		{
			m_encoding.setText("utf-8");
			List<String> als = new ArrayList<String>();
			Set<String> mds = Security.getAlgorithms("MessageDigest");
			for (String md : mds)
			{
				als.add(md);
			}
			Collections.sort(als);
			BPComboBox.BPComboBoxModel<String> model = new BPComboBox.BPComboBoxModel<String>();
			model.setDatas(als);
			m_al.setModel(model);
			int seli = als.indexOf("MD5");
			if (seli >= 0)
				m_al.setSelectedIndex(seli);
			if (params != null && params.length > 0)
			{
				Object p0 = params[0];
				if (p0 != null && p0 instanceof BPResource[])
				{
					BPResource[] ress = (BPResource[]) p0;
					String[] fs = new String[ress.length];
					for (int i = 0; i < ress.length; i++)
					{
						BPResourceFileSystem res = (BPResourceFileSystem) ress[i];
						fs[i] = res.getFileFullName();
					}
					setFilenames(fs);
					m_cbmodes.setSelectedIndex(1);
				}
			}
		}

		protected void onRun(ActionEvent e)
		{
			int mode = m_cbmodes.getSelectedIndex();
			switch (mode)
			{
				case 0:
				{
					onT2T(e);
					break;
				}
				case 1:
				{
					onF2T(e);
					break;
				}
				case 2:
				{
					onT2F(e);
					break;
				}
				case 3:
				{
					onF2F(e);
					break;
				}
			}
		}

		protected void onT2T(ActionEvent e)
		{
			UIStd.wrapSegE(() ->
			{
				calc(0, 0);
			});
		}

		protected void onF2T(ActionEvent e)
		{
			UIStd.wrapSegE(() ->
			{
				calc(1, 0);
			});
		}

		protected void onT2F(ActionEvent e)
		{
			UIStd.wrapSegE(() ->
			{
				calc(0, 1);
			});
		}

		protected void onF2F(ActionEvent e)
		{
			UIStd.wrapSegE(() ->
			{
				calc(1, 1);
			});
		}

		protected void onSelectEncoding(MouseEvent e)
		{
			SortedMap<String, Charset> charsetmap = Charset.availableCharsets();
			List<String> charsetnames = new ArrayList<String>(charsetmap.keySet());
			String en = UIStd.select(charsetnames, BPGUICore.S_BP_TITLE + " - Select Encoding", null);
			if (en != null)
				m_encoding.setText(en);
		}

		protected String getTextEncoding()
		{
			String en = m_encoding.getText();
			if (en == null)
				return "utf-8";
			en = en.trim();
			return en.length() == 0 ? "utf-8" : en;
		}

		protected String getAL()
		{
			return (String) m_al.getSelectedItem();
		}

		protected void calc(int srctype, int tartype) throws IOException, NoSuchAlgorithmException
		{
			String src = null;
			String en = getTextEncoding();
			byte[] bs = null;
			StringBuilder sb = null;

			if (srctype == 0)
			{
				src = m_src.getText();
				if (src != null)
				{
					bs = src.getBytes(en);
					sb = new StringBuilder();
					output(null, bs, sb);
				}
			}
			else if (srctype == 1)
			{
				String[] filenames = m_filenames;
				if (filenames == null)
					filenames = CommonUIOperations.showOpenFilesDialog((Window) m_src.getFocusCycleRootAncestor());
				if (filenames != null && filenames.length > 0)
				{
					m_filenames = filenames;
					if (tartype == 0)
					{
						sb = new StringBuilder();
						for (String filename : filenames)
						{
							bs = null;
							try (FileInputStream fis = new FileInputStream(filename))
							{
								bs = IOUtil.read(fis);
							}
							output(filename, bs, sb);
						}
					}
					else if (tartype == 1)
					{
						for (String filename : filenames)
						{
							sb = new StringBuilder();
							bs = null;
							try (FileInputStream fis = new FileInputStream(filename))
							{
								bs = IOUtil.read(fis);
							}
							output(null, bs, sb);
							try (FileOutputStream fos = new FileOutputStream(filename + getALExt()))
							{
								IOUtil.write(fos, sb.toString().getBytes("utf-8"));
							}
						}
						sb = null;
					}
				}
			}
			if (sb != null)
			{
				if (tartype == 0)
				{
					m_dest.setText(sb.toString());
				}
				else if (tartype == 1)
				{
					String filename = CommonUIOperations.showSaveFileDialog((Window) m_src.getFocusCycleRootAncestor());
					if (filename != null && filename.length() > 0)
					{
						try (FileOutputStream fos = new FileOutputStream(filename))
						{
							IOUtil.write(fos, sb.toString().getBytes("utf-8"));
						}
					}
				}
			}
		}

		private String getALExt()
		{
			String key = getAL().toLowerCase();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < key.length(); i++)
			{
				char c = key.charAt(i);
				if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))
				{
					sb.append(c);
				}
				else
				{
					break;
				}
			}
			return "." + sb.toString();
		}

		protected void output(String key, byte[] bs, StringBuilder sb) throws NoSuchAlgorithmException, IOException
		{
			String al = getAL();
			if (bs != null)
			{
				byte[] bsnew = SecUtil.md(bs, al);
				if (bsnew != null)
				{
					if (sb.length() > 0)
						sb.append("\n");
					if (key != null)
					{
						sb.append(key);
						sb.append(":");
					}
					sb.append(byte2str(bsnew));
				}
			}
		}

		protected final static String byte2str(byte[] bytes)
		{
			int len = bytes.length;
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < len; i++)
			{
				byte b = bytes[i];
				result.append(hex(b >>> 4 & 0xf));
				result.append(hex(b & 0xf));
			}
			return result.toString();
		}

		private static char hex(int index)
		{
			if (index < 10)
				return (char) ((byte) '0' + (byte) index);
			else
				return (char) ((byte) 'A' + (byte) (index - 10));
		}
	}
}
