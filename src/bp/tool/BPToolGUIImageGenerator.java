package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Map;

import bp.data.BPDataWrapper;
import bp.ui.editor.BPImageGeneratorPanel;
import bp.ui.frame.BPFrame;
import bp.ui.scomp.BPVarControlGroup;
import bp.ui.util.UIUtil;
import bp.util.JSONUtil;

public class BPToolGUIImageGenerator extends BPToolGUIBase<BPToolGUIImageGenerator.BPToolGUIContextImageGenerator>
{
	public String getName()
	{
		return "Image Generator";
	}

	protected BPToolGUIContextImageGenerator createToolContext()
	{
		return new BPToolGUIContextImageGenerator();
	}

	protected void setFramePrefers(BPFrame f)
	{
		f.setPreferredSize(UIUtil.getPercentDimension(0.8f, 0.8f));
		f.pack();
		if (!f.isLocationByPlatform())
			f.setLocationRelativeTo(null);
	}

	protected static class BPToolGUIContextImageGenerator implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPImageGeneratorPanel m_imggenp;

		public void initUI(Container par, Object... params)
		{
			m_imggenp = new BPImageGeneratorPanel();

			par.add(m_imggenp, BorderLayout.CENTER);
		}

		public void initDatas(Object... params)
		{
			Map<String, Object> mv = JSONUtil.decode("{\"g\":{\"layout\":0,\"children\":[{\"type\":\"group\",\"width\":4,\"height\":4,\"color\":14737632,\"border\":1,\"border-width\":1,\"border-color\":0}]}}");
			BPDataWrapper<Map<String, Object>> dw = new BPDataWrapper<Map<String, Object>>(mv);
			BPVarControlGroup ctrl = m_imggenp.getResultComp();
			ctrl.bind(dw);
			ctrl.setDataKey("g");
			ctrl.updateData();
			ctrl.getComponent().repaint();
		}
	}
}
