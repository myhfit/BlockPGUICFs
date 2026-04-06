package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIStd;
import bp.util.TextUtil;

public class BPToolGUIStringEscape extends BPToolGUIBase<BPToolGUIStringEscape.BPToolGUIContextStringEscape>
{
	public String getName()
	{
		return BPActionHelpers.getValue(BPActionConstCommon.TNAME_STRESCAPE, null, null);
	}

	protected BPToolGUIContextStringEscape createToolContext()
	{
		return new BPToolGUIContextStringEscape();
	}

	protected static interface StringEscapeFunc
	{
		String name();

		String escape(String raw);

		String unescape(String escaped);
	}

	protected static class StringEscapeFunc_CStyle implements StringEscapeFunc
	{
		public String name()
		{
			return "C Style(\'\\\')";
		}

		public String escape(String raw)
		{
			return TextUtil.escape(raw);
		}

		public String unescape(String escaped)
		{
			return TextUtil.unescape(escaped);
		}
	}

	protected static class StringEscapeFunc_CStyleASCII implements StringEscapeFunc
	{
		public String name()
		{
			return "C Style(\'\\\') With ASCII(\\u)";
		}

		public String escape(String raw)
		{
			return TextUtil.escapeToASCII(raw, null);
		}

		public String unescape(String escaped)
		{
			return TextUtil.unescape(escaped);
		}
	}

	protected static class StringEscapeFunc_UP_ASCII implements StringEscapeFunc
	{
		public String name()
		{
			return "Classic Style(\\U+)";
		}

		public String escape(String raw)
		{
			return TextUtil.escapeToASCII(raw, (c) ->
			{
				int cp = Character.codePointAt(new char[] { c }, 0);
				return "\\U+" + TextUtil.fillString(Integer.toString(cp, 16), '0', 4);
			});
		}

		public String unescape(String escaped)
		{
			String r = escaped.replace("\\U+", "\\u");
			return TextUtil.unescape(r);
		}
	}

	protected static class BPToolGUIContextStringEscape implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_src;
		protected BPCodePane m_dest;
		protected BPComboBox<StringEscapeFunc> m_cmbfuncs;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;

		public void initUI(Container par, Object... params)
		{
			m_src = new BPCodePane();
			m_dest = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			m_cmbfuncs = new BPComboBox<StringEscapeFunc>();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			JPanel px = new JPanel();
			BPLabel lblsrc = new BPLabel(" " + BPActionHelpers.getValue(BPActionConstCommon.TXT_RAW, null, null));
			BPLabel lbldest = new BPLabel(" " + BPActionHelpers.getValue(BPActionConstCommon.TXT_ESCAPED, null, null));
			BPLabel lblen = new BPLabel(BPLocaleHelpers.getValue(BPLocaleConstCC.FUNCTION) + ":");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actdecode = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNUNESCAPE, this::onDecode);
			Action actencode = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNESCAPE, this::onEncode);
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			toolbar.setActions(new Action[] { actdecode, BPAction.separator(), actencode, BPAction.separator(), BPAction.separator(), BPAction.separator() });
			toolbar.add(lblen);
			toolbar.add(m_cmbfuncs);
			toolbar.add(px);

			px.setPreferredSize(new Dimension(5000, 0));
			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_cmbfuncs.replaceWBorder();

			m_src.setMonoFont();
			m_dest.setMonoFont();
			m_cmbfuncs.setMonoFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();
			lblen.setLabelFont();

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
			List<StringEscapeFunc> funcs = new ArrayList<StringEscapeFunc>();
			funcs.add(new StringEscapeFunc_CStyle());
			funcs.add(new StringEscapeFunc_CStyleASCII());
			funcs.add(new StringEscapeFunc_UP_ASCII());
			BPComboBox.BPComboBoxModel<StringEscapeFunc> model = new BPComboBox.BPComboBoxModel<StringEscapeFunc>();
			model.setDatas(funcs);
			m_cmbfuncs.setModel(model);
			m_cmbfuncs.setRenderer(new BPComboBox.BPComboBoxRenderer(obj -> obj == null ? "" : ((StringEscapeFunc) obj).name()));
			m_cmbfuncs.setSelectedIndex(0);
		}

		protected StringEscapeFunc getEscapeFunction()
		{
			return (StringEscapeFunc) m_cmbfuncs.getSelectedItem();
		}

		protected void onEncode(ActionEvent e)
		{
			UIStd.wrapSegE(() -> m_dest.setText(getEscapeFunction().escape(m_src.getText())));
		}

		protected void onDecode(ActionEvent e)
		{
			UIStd.wrapSegE(() -> m_src.setText(getEscapeFunction().unescape(m_dest.getText())));
		}
	}
}
