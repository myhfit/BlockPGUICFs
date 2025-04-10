package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import bp.ui.scomp.BPLabel;
import bp.ui.util.UIStd;

public class BPToolGUIRegEx extends BPToolGUIBase<BPToolGUIRegEx.BPToolGUIContextRegEx>
{
	public String getName()
	{
		return "Regular Expression";
	}

	protected BPToolGUIContextRegEx createToolContext()
	{
		return new BPToolGUIContextRegEx();
	}

	protected static class BPToolGUIContextRegEx implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_regex;
		protected BPCodePane m_data;
		protected JScrollPane m_scrollregex;
		protected JScrollPane m_scrolldata;

		public void initUI(Container par, Object... params)
		{
			m_regex = new BPCodePane();
			m_data = new BPCodePane();
			m_scrollregex = new JScrollPane();
			m_scrolldata = new JScrollPane();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel pregex = new JPanel();
			JPanel pdata = new JPanel();
			BPLabel lblregex = new BPLabel(" RegEx ");
			BPLabel lbldata = new BPLabel(" Data");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actfind = BPAction.build("Find").callback(this::onFind).getAction();
			Action actmatch = BPAction.build("Match").callback(this::onMatch).getAction();
			Action actreplace = BPAction.build("Replace").callback(this::onReplace).getAction();
			actreplace.setEnabled(false);
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actmatch, BPAction.separator(), actfind, BPAction.separator(), actreplace });

			m_scrollregex.setViewportView(m_regex);
			m_scrolldata.setViewportView(m_data);
			m_scrollregex.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldata.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			pregex.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

			m_regex.setMonoFont();
			m_data.setMonoFont();
			lblregex.setLabelFont();
			lbldata.setLabelFont();

			sp.add(pregex);
			sp.add(pdata);
			pregex.setLayout(new BorderLayout());
			pdata.setLayout(new BorderLayout());
			m_regex.setBorder(new EmptyBorder(0, 0, 0, 0));
			lblregex.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			lbldata.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			pregex.add(lblregex, BorderLayout.NORTH);
			pdata.add(lbldata, BorderLayout.NORTH);
			pregex.add(m_scrollregex, BorderLayout.CENTER);
			pdata.add(m_scrolldata, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);

			m_data.setupCodeBorder();
			m_data.resizeDoc();
		}

		public void initDatas(Object... params)
		{
		}

		protected void onReplace(ActionEvent e)
		{
		}

		protected void onMatch(ActionEvent e)
		{
			String regex = m_regex.getText().trim();
			String data = m_data.getText();
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(data);
			UIStd.info_small(m.matches() ? "Yes" : "No", null);
		}

		protected void onFind(ActionEvent e)
		{
			String regex = m_regex.getText().trim();
			String data = m_data.getText();
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(data);
			int c = 0;
			StringBuilder sb = new StringBuilder();
			while (m.find())
			{
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(m.start() + "-" + (m.end() - 1) + ":" + m.group());
				c++;
			}
			UIStd.info("Results:" + c + (c == 0 ? "" : ("\n" + sb.toString())));
		}
	}
}
