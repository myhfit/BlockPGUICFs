package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

public class BPToolGUIURLEncoding extends BPToolGUIBase<BPToolGUIURLEncoding.BPToolGUIContextURLEncoding>
{
	public String getName()
	{
		return "URL Encoding";
	}

	protected BPToolGUIContextURLEncoding createToolContext()
	{
		return new BPToolGUIContextURLEncoding();
	}

	protected String getSubTitle()
	{
		return "Decode/Encode URL";
	}

	protected static class BPToolGUIContextURLEncoding implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_src;
		protected BPCodePane m_dest;
		protected BPTextField m_encoding;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;

		public void initUI(Container par, Object... params)
		{
			m_src = new BPCodePane();
			m_dest = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			m_encoding = new BPTextField();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			JPanel px = new JPanel();
			BPLabel lblsrc = new BPLabel(" Raw");
			BPLabel lbldest = new BPLabel(" Encoded");
			BPLabel lblen = new BPLabel("Encoding:");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actdecode = BPAction.build("Decode").callback(this::onDecode).getAction();
			Action actencode = BPAction.build("Encode").callback(this::onEncode).getAction();
			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actdecode, BPAction.separator(), actencode, BPAction.separator() });
			toolbar.add(lblen);
			toolbar.add(m_encoding);
			toolbar.add(px);

			m_encoding.setHorizontalAlignment(JTextField.CENTER);
			m_encoding.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			px.setPreferredSize(new Dimension(5000, 0));
			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_encoding.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));

			m_src.setMonoFont();
			m_dest.setMonoFont();
			m_encoding.setMonoFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();
			lblen.setLabelFont();

			m_encoding.setText("utf-8");

			sp.add(psrc);
			sp.add(pdest);
			psrc.setLayout(new BorderLayout());
			pdest.setLayout(new BorderLayout());
			m_src.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_dest.setBorder(new EmptyBorder(0, 0, 0, 0));
			lblsrc.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			lbldest.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			psrc.add(lblsrc, BorderLayout.NORTH);
			pdest.add(lbldest, BorderLayout.NORTH);
			psrc.add(m_scrollsrc, BorderLayout.CENTER);
			pdest.add(m_scrolldest, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);

			m_src.resizeDoc();
		}

		public void initDatas(Object... params)
		{
		}

		protected String getTextEncoding()
		{
			String en = m_encoding.getText();
			if (en == null)
				return "utf-8";
			en = en.trim();
			return en.length() == 0 ? "utf-8" : en;
		}

		protected void onEncode(ActionEvent e)
		{
			UIStd.wrapSegE(() -> m_dest.setText(URLEncoder.encode(m_src.getText(), getTextEncoding()).replaceAll("\\+", "%20")));
		}

		protected void onDecode(ActionEvent e)
		{
			UIStd.wrapSegE(() -> m_src.setText(URLDecoder.decode(m_dest.getText(), getTextEncoding())));
		}
	}
}
