package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.id.SerialIDGenerator;
import bp.id.UUIDGenerator;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.editor.BPCodePanel;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPToolGUIIDGenerator extends BPToolGUIBase<BPToolGUIIDGenerator.BPToolGUIContextGenID>
{
	public String getName()
	{
		return "ID Generator";
	}

	public String getSubTitle()
	{
		return getName();
	}

	protected BPToolGUIContextGenID createToolContext()
	{
		return new BPToolGUIContextGenID();
	}

	protected static class BPToolGUIContextGenID implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPTextPane m_txt;

		protected BPComboBox<GenIDFunc> m_cmbfuncs;
		protected BPTextField m_tfcount;
		protected BPCheckBox m_chkoneline;

		public void initUI(Container par, Object... params)
		{
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actclear = BPAction.build("Clear").callback(this::onClear).getAction();
			Action actgen = BPAction.build("Generate").callback(this::onGen).getAction();
			BPCodePanel tp = new BPCodePanel();
			m_tfcount = new BPTextField();
			m_chkoneline = new BPCheckBox("One Line");
			m_cmbfuncs = new BPComboBox<GenIDFunc>();
			BPLabel lblnum = new BPLabel(" Count: ");
			BPLabel lblfunc = new BPLabel(" Function: ");
			JPanel px = new JPanel();
			m_txt = tp.getTextPanel();

			m_tfcount.setMonoFont();
			m_chkoneline.setMonoFont();
			lblfunc.setLabelFont();
			lblnum.setLabelFont();
			m_cmbfuncs.setMonoFont();

			m_tfcount.setHorizontalAlignment(JTextField.CENTER);
			m_tfcount.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			m_tfcount.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(80, 0)));
			px.setPreferredSize(new Dimension(Short.MAX_VALUE, 0));

			m_tfcount.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_cmbfuncs.replaceWBorder();

			m_txt.setEditable(false);

			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actclear, BPAction.separator(), actgen });
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(1, 1, 1, 1)));

			toolbar.add(lblfunc);
			toolbar.add(m_cmbfuncs);
			toolbar.add(lblnum);
			toolbar.add(m_tfcount);
			toolbar.add(m_chkoneline);
			toolbar.add(px);

			par.add(toolbar, BorderLayout.NORTH);
			par.add(tp, BorderLayout.CENTER);
		}

		public void initDatas(Object... params)
		{
			List<GenIDFunc> funcs = new ArrayList<GenIDFunc>();
			funcs.add(new GenIDFunc_UUID());
			funcs.add(new GenIDFunc_Integer());
			BPComboBox.BPComboBoxModel<GenIDFunc> model = new BPComboBox.BPComboBoxModel<GenIDFunc>();
			model.setDatas(funcs);
			m_cmbfuncs.setModel(model);
			m_cmbfuncs.setSelectedIndex(0);

			m_tfcount.setText("1");
		}

		protected void onClear(ActionEvent e)
		{
			m_txt.setText("");
		}

		protected void onGen(ActionEvent e)
		{
			boolean oneline = m_chkoneline.isSelected();
			StringBuilder sb = new StringBuilder();
			int count = ObjUtil.toInt(m_tfcount.getText().trim(), 1);
			String sp = "\n";
			GenIDFunc func = (GenIDFunc) m_cmbfuncs.getSelectedItem();
			if (oneline)
				sp = ",";

			for (int i = 0; i < count; i++)
			{
				sb.append(func.gen() + sp);
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

	protected static interface GenIDFunc
	{
		String name();

		String gen();
	}

	protected static class GenIDFunc_UUID implements GenIDFunc
	{
		public String name()
		{
			return "UUID";
		}

		public String gen()
		{
			return new UUIDGenerator().genID();
		}

		public String toString()
		{
			return name();
		}
	}

	protected static class GenIDFunc_Integer implements GenIDFunc
	{
		protected SerialIDGenerator m_idgen;

		public GenIDFunc_Integer()
		{
			m_idgen = new SerialIDGenerator();
		}

		public String name()
		{
			return "Integer";
		}

		public String gen()
		{
			return m_idgen.genID();
		}

		public String toString()
		{
			return name();
		}
	}
}
