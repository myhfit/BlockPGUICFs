package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIStd;

public class BPToolGUIClipboard extends BPToolGUIBase<BPToolGUIClipboard.BPToolGUIContextClipboard>
{
	public String getName()
	{
		return "Clipboard";
	}

	protected BPToolGUIContextClipboard createToolContext()
	{
		return new BPToolGUIContextClipboard();
	}

	protected static class BPToolGUIContextClipboard implements BPToolGUIBase.BPToolGUIContext
	{
		protected JScrollPane m_scrollsrc;
		protected BPComboBox<DataFlavor> m_cmbdf;
		protected BPComboBox.BPComboBoxModel<DataFlavor> m_cmbmodel;
		protected JComponent m_content;

		public void initUI(Container par, Object... params)
		{
			m_scrollsrc = new JScrollPane();
			m_cmbdf = new BPComboBox<DataFlavor>();
			m_cmbmodel = new BPComboBox.BPComboBoxModel<DataFlavor>();
			m_cmbdf.setModel(m_cmbmodel);
			JPanel sp = new JPanel();
			sp.setLayout(new BorderLayout());
			JPanel psrc = new JPanel();
			BPToolBarSQ toolbar = new BPToolBarSQ();
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			Action actget = BPAction.build("GET").callback(this::onGet).getAction();
			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actget });
			toolbar.add(m_cmbdf);
			m_cmbdf.setRenderer(new BPComboBox.BPComboBoxRenderer(this::onTransDataFlavor));
			m_cmbdf.setListFont();

			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_cmbdf.replaceWBorder();
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 0, UIConfigs.COLOR_STRONGBORDER()));

			sp.add(psrc, BorderLayout.CENTER);
			psrc.setLayout(new BorderLayout());
			psrc.add(m_scrollsrc, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);

			m_cmbdf.addItemListener(this::onDFChanged);
		}

		public void initDatas(Object... params)
		{
		}

		protected String onTransDataFlavor(Object dataflavor)
		{
			if (dataflavor == null)
				return "";
			DataFlavor df = (DataFlavor) dataflavor;
			return df.toString();
		}

		protected Transferable readClipboardTypes()
		{
			Window w = (Window) m_scrollsrc.getFocusCycleRootAncestor();
			Clipboard clip = w.getToolkit().getSystemClipboard();
			Transferable tdata = clip.getContents(null);
			if (tdata != null)
			{
				DataFlavor[] dfarr = tdata.getTransferDataFlavors();
				List<DataFlavor> dfs = new ArrayList<DataFlavor>();
				for (DataFlavor df : dfarr)
				{
					Class<?> cls = df.getRepresentationClass();
					if (cls == String.class || cls == Image.class)
						dfs.add(df);
				}
				m_cmbmodel.setDatas(dfs);
			}
			else
			{
				m_cmbmodel.setDatas(new ArrayList<DataFlavor>());
			}
			return tdata;
		}

		protected void setClipboardComp(Transferable tdata)
		{
			DataFlavor df = (DataFlavor) m_cmbdf.getSelectedItem();
			if (df == null)
			{
				if (m_cmbdf.getItemCount() > 0)
				{
					m_cmbdf.setSelectedIndex(0);
					df = m_cmbdf.getItemAt(0);
				}
				else
				{
					return;
				}
			}
			try
			{
				if (df != null)
				{
					Class<?> cls = df.getRepresentationClass();
					if (cls == String.class)
					{
						String text = (String) tdata.getTransferData(df);
						setTextComp(text);
					}
					else if (cls == Image.class)
					{
						Image img = (Image) tdata.getTransferData(df);
						setImageComp(img);
					}
				}
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				UIStd.err(e);
			}
		}

		protected void setImageComp(Image img)
		{
			JComponent comp = m_content;
			BPLabel cp = null;
			if (!(comp instanceof BPLabel))
			{
				cp = new BPLabel();
				cp.setHorizontalAlignment(SwingConstants.CENTER);
				cp.setVerticalAlignment(SwingConstants.CENTER);
				comp = cp;
				m_scrollsrc.setViewportView(comp);
			}
			else
			{
				cp = (BPLabel) comp;
			}
			cp.setIcon(new ImageIcon(img));
		}

		protected void setTextComp(String text)
		{
			JComponent comp = m_content;
			BPCodePane cp = null;
			if (!(comp instanceof BPCodePane))
			{
				cp = new BPCodePane();
				cp.setMonoFont();
				comp = cp;
				m_scrollsrc.setViewportView(comp);
			}
			else
			{
				cp = (BPCodePane) comp;
			}
			cp.setText(text);
			cp.setCaretPosition(0);
			cp.resizeDoc();
			cp.requestFocus();
		}

		protected void onDFChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				Window w = (Window) m_scrollsrc.getFocusCycleRootAncestor();
				Clipboard clip = w.getToolkit().getSystemClipboard();
				Transferable tdata = clip.getContents(null);
				UIStd.wrapSeg(() -> setClipboardComp(tdata));
			}
		}

		protected void onGet(ActionEvent e)
		{
			m_cmbdf.setSelectedIndex(-1);
			Transferable data = readClipboardTypes();
			UIStd.wrapSeg(() -> setClipboardComp(data));
		}
	}
}
