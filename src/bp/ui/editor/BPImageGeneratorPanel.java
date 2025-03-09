package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.data.BPDataWrapper;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPVarControlGroup;
import bp.ui.util.UIUtil;
import bp.util.JSONUtil;
import bp.util.Std;
import bp.util.TemplateUtil;

public class BPImageGeneratorPanel extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7075640059364490106L;

	protected JPanel m_sp;
	protected JScrollPane m_scroll2;
	protected Consumer<BPEditorPane> m_changedhandler;
	protected AtomicBoolean m_changed = new AtomicBoolean(false);

	protected BPCodePanel m_txtdata;
	protected BPVarControlGroup m_ctrl;

	protected boolean m_canpreview = true;

	protected void init()
	{
		m_ctrl = new BPVarControlGroup();
		m_txtdata = new BPCodePanel();
		m_sp = new JPanel();
		m_scroll2 = new JScrollPane();
		m_scroll = new JScrollPane();
		m_txt = createTextPane();
		m_changedhandler = this::onTextChanged;

		m_scroll.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_txt.setOnPosChanged(this::onPosChanged);
		m_scroll2.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_STRONGBORDER()));

		setLayout(new BorderLayout());
		m_scroll.setViewportView(m_txt);
		m_scroll2.setViewportView(m_ctrl.getComponent());
		m_scroll2.setPreferredSize(new Dimension(300, 200));
		m_sp.setLayout(new GridLayout(1, 2, 0, 0));
		m_sp.add(m_scroll);
		m_sp.add(m_txtdata);
		add(m_sp, BorderLayout.CENTER);
		add(m_scroll2, BorderLayout.EAST);

		m_txt.setChangedHandler(m_changedhandler);

		initActions();
		initListeners();

		m_canpreview = true;

		preview("{}");
	}

	public BPVarControlGroup getResultComp()
	{
		return m_ctrl;
	}

	protected void preview(String txt)
	{
		UIUtil.laterUI(() ->
		{
			if (m_changed.compareAndSet(true, false))
			{
				Map<String, Object> jsonobjtemplate = JSONUtil.decode(m_txt.getText());
				Map<String, Object> jsonobjdata = JSONUtil.decode(m_txtdata.getTextPanel().getText());
				Map<String, Object> bdata = TemplateUtil.bind(jsonobjtemplate, jsonobjdata);
				BPDataWrapper<Map<String, Object>> dw = new BPDataWrapper<Map<String, Object>>(bdata);
				m_ctrl.bind(dw);
			}
		});
	}

	protected void onTextChanged(BPEditorPane txt)
	{
		if (m_canpreview)
		{
			m_changed.set(true);
			UIUtil.laterUI(() ->
			{
				if (m_changed.compareAndSet(true, false))
				{
					try
					{
						Map<String, Object> jsonobjtemplate = JSONUtil.decode(txt.getText());
						Map<String, Object> jsonobjdata = JSONUtil.decode(m_txtdata.getTextPanel().getText());
						if (jsonobjtemplate != null)
						{
							Map<String, Object> bdata = TemplateUtil.bind(jsonobjtemplate, jsonobjdata);
							BPDataWrapper<Map<String, Object>> dw = new BPDataWrapper<Map<String, Object>>(bdata);
							m_ctrl.bind(dw);
							m_ctrl.updateData();
							m_ctrl.getComponent().repaint();
						}
					}
					catch (Exception e)
					{
						Std.debug(e.toString());
					}
				}
			});
		}
	}
}
