package bp.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import bp.ui.editor.BPDSVEditor;

public class BPDSVActions extends BPXYDEditorActions
{
	public Action actsplit;
	public Action actappend;

	public BPDSVActions(BPDSVEditor editor)
	{
		super(editor);
		actsplit = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSPLIT, editor::showSplit);
		actappend = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNAPPEND, editor::showAppendDSV);
	}

	public Action[] getActions()
	{
		List<Action> rc = new ArrayList<Action>(Arrays.asList(super.getActions()));
		rc.add(BPAction.separator());
		rc.add(actsplit);
		rc.add(actappend);
		return rc.toArray(new Action[rc.size()]);
	}
}
