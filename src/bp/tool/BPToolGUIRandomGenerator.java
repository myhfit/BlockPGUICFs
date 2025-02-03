package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPToolGUIRandomGenerator extends BPToolGUIBase<BPToolGUIRandomGenerator.BPToolGUIContextRandomGenerator>
{
	public String getName()
	{
		return "Random Number Generator";
	}

	protected BPToolGUIContextRandomGenerator createToolContext()
	{
		return new BPToolGUIContextRandomGenerator();
	}

	protected static class BPToolGUIContextRandomGenerator implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPTextPane m_txt;

		protected BPTextField m_tfcount;
		protected BPTextField m_tfmin;
		protected BPTextField m_tfmax;
		protected BPCheckBox m_chkfloat;
		protected BPCheckBox m_chkoneline;

		public void initUI(Container par, Object... params)
		{
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actclear = BPAction.build("Clear").callback(this::onClear).getAction();
			Action actgen = BPAction.build("Generate").callback(this::onGen).getAction();
			BPCodePanel tp = new BPCodePanel();
			m_tfcount = new BPTextField();
			m_tfmin = new BPTextField();
			m_tfmax = new BPTextField();
			m_chkfloat = new BPCheckBox("Float");
			m_chkoneline = new BPCheckBox("One Line");
			BPLabel lblnum = new BPLabel(" Count: ");
			BPLabel lblmin = new BPLabel(" Min: ");
			BPLabel lblmax = new BPLabel(" Max: ");
			JPanel px = new JPanel();
			m_txt = tp.getTextPanel();

			m_tfcount.setMonoFont();
			m_tfmin.setMonoFont();
			m_tfmax.setMonoFont();
			m_chkfloat.setMonoFont();
			m_chkoneline.setMonoFont();
			lblnum.setLabelFont();
			lblmin.setLabelFont();
			lblmax.setLabelFont();

			m_tfcount.setHorizontalAlignment(JTextField.CENTER);
			m_tfmin.setHorizontalAlignment(JTextField.CENTER);
			m_tfmax.setHorizontalAlignment(JTextField.CENTER);
			m_tfcount.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfmin.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfmax.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfcount.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfmin.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfmax.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			px.setPreferredSize(new Dimension(Short.MAX_VALUE, 0));

			m_tfcount.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_tfmin.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_tfmax.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));

			m_txt.setEditable(false);

			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actclear, BPAction.separator(), actgen });
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(1, 1, 1, 1)));

			toolbar.add(lblnum);
			toolbar.add(m_tfcount);
			toolbar.add(lblmin);
			toolbar.add(m_tfmin);
			toolbar.add(lblmax);
			toolbar.add(m_tfmax);
			toolbar.add(m_chkfloat);
			toolbar.add(m_chkoneline);
			toolbar.add(px);

			par.add(toolbar, BorderLayout.NORTH);
			par.add(tp, BorderLayout.CENTER);
		}

		public void initDatas(Object... params)
		{
			m_tfcount.setText("1");
			m_tfmin.setText("1");
			m_tfmax.setText("100");
		}

		protected void onClear(ActionEvent e)
		{
			m_txt.setText("");
		}

		protected void onGen(ActionEvent e)
		{
			boolean isfloat = m_chkfloat.isSelected();
			boolean oneline = m_chkoneline.isSelected();
			StringBuilder sb = new StringBuilder();
			int count = ObjUtil.toInt(m_tfcount.getText().trim(), 1);
			String sp = "\n";
			if (oneline)
				sp = ",";
			if (!isfloat)
			{
				int min = ObjUtil.toInt(m_tfmin.getText().trim(), 1);
				int max = ObjUtil.toInt(m_tfmax.getText().trim(), 100);
				double l = max - min;
				for (int i = 0; i < count; i++)
				{
					double v = Math.random();
					sb.append(min + (int) Math.round(v * l) + sp);
				}
			}
			else
			{
				double min = ObjUtil.toDouble(m_tfmin.getText().trim(), 1);
				double max = ObjUtil.toDouble(m_tfmax.getText().trim(), 100);
				double l = max - min;
				NumberFormat nf = new DecimalFormat("0.000000");
				for (int i = 0; i < count; i++)
				{
					double v = Math.random();
					sb.append(nf.format(min + (v * l)) + sp);
				}
			}
			if (sb.length() != 0)
			{
				String oldtxt = m_txt.getText();
				if (oldtxt.length() == 0)
					sb.delete(sb.length() - 1, sb.length());
				else
				{
					if (oneline)
						sb.delete(sb.length() - 1, sb.length());
					sb.append("\n");
					sb.append(oldtxt);
				}
				m_txt.setText(sb.toString());
				m_txt.setCaretPosition(0);
			}
		}
	}
}
