package bp.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import bp.data.BPDSVContainer;
import bp.data.BPXYData;
import bp.res.BPResource;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;

public class BPDataActionFactoryDSV implements BPDataActionFactory
{
	public Action[] getAction(Object data, String actionname, Runnable loaddatafunc)
	{
		Action[] rc = null;
		if (data != null && actionname != null)
		{
			if (data instanceof BPXYData && ACTIONNAME_CLONEDATA.equals(actionname))
			{
				Action actclonecsv = BPAction.build("CSV").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryDSV::cloneXYDataToCSV, loaddatafunc)).getAction();
				Action actclonetsv = BPAction.build("TSV").callback(new DataActionProcessor<BPXYData>((BPXYData) data, BPDataActionFactoryDSV::cloneXYDataToTSV, loaddatafunc)).getAction();
				rc = new Action[] { actclonecsv, actclonetsv };
			}
		}
		return rc;
	}

	private final static void cloneXYDataToCSV(BPXYData xydata, ActionEvent event)
	{
		cloneXYDataToDSV(xydata, event, ",", ".csv");
	}

	private final static void cloneXYDataToTSV(BPXYData xydata, ActionEvent event)
	{
		cloneXYDataToDSV(xydata, event, "\t", ".tsv");
	}

	private final static void cloneXYDataToDSV(BPXYData xydata, ActionEvent event, String delimiter, String ext)
	{
		BPResource file = CommonUIOperations.selectResource(null, true, new String[] { ext });
		if (file != null)
		{
			String encoding = UIStd.input("UTF-8", "Encoding:", "Input file encoding");
			if (encoding == null)
				return;
			BPDSVContainer con = new BPDSVContainer(encoding, delimiter);
			con.open();
			try
			{
				con.bind(file);
				con.writeXYData(xydata);
			}
			finally
			{
				con.close();
			}
		}
	}
}