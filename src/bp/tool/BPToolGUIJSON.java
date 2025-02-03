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
import bp.ui.actions.BPAction;
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
		protected BPTree m_src;
		protected BPCodePane m_dest;
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrolldest;

		public void initUI(Container par, Object... params)
		{
			m_src = new BPTree();
			m_dest = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrolldest = new JScrollPane();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			BPLabel lblsrc = new BPLabel(" Raw");
			BPLabel lbldest = new BPLabel(" Encoded");
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actdecode = BPAction.build("Decode").callback(this::onDecode).getAction();
			Action actencode = BPAction.build("Encode").callback(this::onEncode).getAction();
			actencode.setEnabled(false);

			toolbar.setHasButtonBorder(true);
			toolbar.setActions(new Action[] { actdecode, BPAction.separator(), actencode });

			m_scrollsrc.setViewportView(m_src);
			m_scrolldest.setViewportView(m_dest);
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrolldest.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_src.setRootVisible(false);
			m_src.setCellRenderer(new BPTreeCellRendererObject());

			m_src.setMonoFont();
			m_dest.setMonoFont();
			lblsrc.setLabelFont();
			lbldest.setLabelFont();

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

		@SuppressWarnings("unchecked")
		public void initDatas(Object... params)
		{
			String dest = null;
			if (params != null && params.length > 0)
			{
				Map<String, Object> ps = (Map<String, Object>) params[0];
				dest = (String) ps.get("dest");
			}
			
			if (dest != null)
			{
				m_dest.setText(dest);
				setTreeData(JSONUtil.decode(dest));
			}
			else
				setTreeData(null);
		}

		protected void onEncode(ActionEvent e)
		{
		}

		protected void onDecode(ActionEvent e)
		{
			UIStd.wrapSeg(() -> setTreeData(JSONUtil.decode(m_dest.getText())));
		}

		protected void setTreeData(Object data)
		{
			BPTreeModel model = new BPTreeModel(new BPTreeFuncsObject(data));
			m_src.setModel(model);
		}
	}
}
