package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.locale.BPLocaleConstCC;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeFuncsObject;
import bp.ui.util.UIStd;
import bp.util.JSONUtil;

public class BPToolGUIJSON extends BPToolGUIBase<BPToolGUIJSON.BPToolGUIContextJSON>
{
	public String getName()
	{
		return "JSON";
	}

	protected BPToolGUIContextJSON createToolContext()
	{
		return new BPToolGUIContextJSON();
	}

	protected static class BPToolGUIContextJSON implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPTree m_dest;
		protected BPCodePane m_src;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;

		public void initUI(Container par, Object... params)
		{
			m_dest = new BPTree();
			m_src = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			BPLabel lblsrc = new BPLabel(" " + BPLocaleConstCC.SOURCE.text());
			BPLabel lbldest = new BPLabel(" " + BPLocaleConstCC.DESTINATION.text());
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actdecode = BPActionHelpers.getAction(BPActionConstCommon.TXT_DECODE, this::onDecode);
			Action actencode = BPActionHelpers.getAction(BPActionConstCommon.TXT_ENCODE, this::onEncode);
			actencode.setEnabled(false);
			toolbar.setHasButtonBorder(true);
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			toolbar.setActions(new Action[] { actdecode, BPAction.separator(), actencode });

			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_dest.setRootVisible(false);
			m_dest.setCellRenderer(new BPTreeCellRendererObject());

			m_dest.setMonoFont();
			m_src.setMonoFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();

			sp.add(psrc);
			sp.add(pdest);
			psrc.setLayout(new BorderLayout());
			pdest.setLayout(new BorderLayout());
			m_src.setBorder(new EmptyBorder(0, 0, 0, 0));
			lblsrc.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			lbldest.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			psrc.add(lblsrc, BorderLayout.NORTH);
			pdest.add(lbldest, BorderLayout.NORTH);
			psrc.add(m_scrollsrc, BorderLayout.CENTER);
			pdest.add(m_scrolldest, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);
		}

		@SuppressWarnings("unchecked")
		public void initDatas(Object... params)
		{
			String src = null;
			if (params != null && params.length > 0)
			{
				Object p0 = params[0];
				if (p0 instanceof Map)
				{
					Map<String, Object> ps = (Map<String, Object>) p0;
					src = (String) ps.get("src");
				}
			}

			if (src != null)
			{
				m_src.setText(src);
				setTreeData(JSONUtil.decode(src));
			}
			else
				setTreeData(null);
		}

		protected void onEncode(ActionEvent e)
		{
		}

		protected void onDecode(ActionEvent e)
		{
			UIStd.wrapSeg(() -> setTreeData(JSONUtil.decode(m_src.getText())));
		}

		protected void setTreeData(Object data)
		{
			BPTreeModel model = new BPTreeModel(new BPTreeFuncsObject(data));
			m_dest.setModel(model);
		}
	}
}
