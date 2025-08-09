package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPLabel;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.util.IOUtil;

public class BPToolGUIBase64 extends BPToolGUIBase<BPToolGUIBase64.BPToolGUIContextBase64>
{
	public String getName()
	{
		return "Base64";
	}

	public String getSubTitle()
	{
		return "Decode/Encode Base64";
	}

	protected BPToolGUIContextBase64 createToolContext()
	{
		return new BPToolGUIContextBase64();
	}

	protected static class BPToolGUIContextBase64 implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_src;
		protected BPCodePane m_dest;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;
		protected BPComboBox<String> m_comborfc;

		public void initUI(Container par, Object... params)
		{
			m_src = new BPCodePane();
			m_dest = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			m_comborfc = new BPComboBox<String>();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			BPLabel lblsrc = new BPLabel(" Raw");
			BPLabel lbldest = new BPLabel(" Encoded");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actdecode = BPAction.build("Decode").callback(this::onDecode).getAction();
			Action actencode = BPAction.build("Encode").callback(this::onEncode).getAction();
			Action actencodefile = BPAction.build("Encode File").callback(this::onEncodeFile).getAction();
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actdecode, BPAction.separator(), actencode, BPAction.separator(), actencodefile, BPAction.separator() });
			toolbar.add(m_comborfc);

			m_comborfc.setListFont();
			m_src.setMonoFont();
			m_dest.setMonoFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();

			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

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

			initCombo();
			m_src.resizeDoc();
		}

		protected void initCombo()
		{
			List<String> rfcs = new ArrayList<String>();
			rfcs.add("RFC4648");
			rfcs.add("RFC4648_URLSAFE");
			rfcs.add("RFC2045");
			BPComboBoxModel<String> model = new BPComboBoxModel<String>();
			model.setDatas(rfcs);
			m_comborfc.setModel(model);
			m_comborfc.setSelectedIndex(0);
		}

		public void initDatas(Object... params)
		{
		}

		protected void onEncode(ActionEvent e)
		{
			UIStd.wrapSeg(() -> m_dest.setText(new String(getEncoder().encode(m_src.getText().getBytes()))));
		}

		protected Encoder getEncoder()
		{
			int seli = m_comborfc.getSelectedIndex();
			switch (seli)
			{
				case 0:
					return Base64.getEncoder();
				case 1:
					return Base64.getUrlEncoder();
				case 2:
					return Base64.getMimeEncoder();
			}
			return null;
		}

		protected Decoder getDecoder()
		{
			int seli = m_comborfc.getSelectedIndex();
			switch (seli)
			{
				case 0:
					return Base64.getDecoder();
				case 1:
					return Base64.getUrlDecoder();
				case 2:
					return Base64.getMimeDecoder();
			}
			return null;
		}

		protected void onEncodeFile(ActionEvent e)
		{
			String f = CommonUIOperations.showOpenFileDialog((Window) m_src.getFocusCycleRootAncestor(), "");
			if (f != null)
			{
				try (FileInputStream fis = new FileInputStream(f))
				{
					byte[] bs = IOUtil.read(fis);
					UIStd.wrapSeg(() -> m_dest.setText(new String(getEncoder().encode(bs))));
				}
				catch (IOException e1)
				{
					UIStd.err(e1);
				}
			}
		}

		protected void onDecode(ActionEvent e)
		{
			UIStd.wrapSeg(() -> m_src.setText(new String(getDecoder().decode(m_dest.getText().getBytes()))));
		}
	}
}
