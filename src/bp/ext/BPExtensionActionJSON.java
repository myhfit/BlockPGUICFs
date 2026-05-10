package bp.ext;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import bp.data.BPTreeData.BPTreeDataObj;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPDataActionFactory;
import bp.ui.editor.BPJSONPanel;
import bp.ui.res.icon.BPIconResV;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.JSONUtil;

public class BPExtensionActionJSON
{
	public final static Action[] getCloneActions(BPJSONPanel panel)
	{
		WeakReference<BPJSONPanel> ref = new WeakReference<BPJSONPanel>(panel);
		Action act = BPAction.build("Clone").callback(e -> showClone(e, ref.get())).vIcon(BPIconResV.CLONE()).tooltip("Clone").getAction();
		return new Action[] { act };
	}

	protected final static void showClone(ActionEvent e, BPJSONPanel panel)
	{
		List<Action> acts=new ArrayList<Action>();
		String text=panel.getTextPanel().getText();
		Object obj=JSONUtil.decode(text);
		if(obj instanceof Map)
		{
			BPTreeDataObj treedata=new BPTreeDataObj();
			treedata.setRoot(obj);
			ServiceLoader<BPDataActionFactory> facs = ClassUtil.getExtensionServices(BPDataActionFactory.class);
			for (BPDataActionFactory fac : facs)
			{
				Action[] acts2 = fac.getAction(treedata, BPDataActionFactory.ACTIONNAME_CLONEDATA, null);
				if (acts2 != null)
				{
					for (Action act : acts2)
					{
						acts.add(act);
					}
				}
			}
		}
		if (acts != null && acts.size() > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(acts.toArray(new Action[0]));
			for (JComponent comp : comps)
			{
				pop.add(comp);
			}
			JComponent source = (JComponent) e.getSource();
			JComponent par = (JComponent) source.getParent();
			pop.show(par, source.getX(), source.getY() + source.getHeight());
		}
	}
}