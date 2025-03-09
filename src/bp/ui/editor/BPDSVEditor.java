package bp.ui.editor;

import java.util.List;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.data.BPDSVContainer;
import bp.data.BPDSVData;
import bp.data.BPDataContainer;
import bp.data.BPXData;
import bp.data.BPXYDContainer;
import bp.data.BPXYDData;
import bp.format.BPFormat;
import bp.format.BPFormatCSV;
import bp.format.BPFormatDSV;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.format.BPFormatTSV;
import bp.res.BPResource;
import bp.ui.actions.BPActionHolder;
import bp.ui.actions.BPDSVActions;
import bp.util.LogicUtil;
import bp.util.TextUtil;

public class BPDSVEditor extends BPXYDEditor<BPXYDContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9212809561160774333L;

	protected BPActionHolder createActionHolder()
	{
		return new BPDSVActions(this);
	}

	protected BPXYDData createSaveData(String[] colnames, Class<?>[] colclasses, String[] collabels, List<BPXData> datas)
	{
		BPDSVData xydata = new BPDSVData();
		xydata.setColumnNames(colnames);
		xydata.setColumnClasses(colclasses);
		xydata.setColumnLabels(collabels);
		xydata.setDatas(datas);
		return xydata;
	}

	public String[] getExts()
	{
		return new String[] { ".csv", ".tsv" };
	}

	public BPDataContainer createDataContainer(BPResource res)
	{
		String encoding = null;
		String delimiter = null;
		if (m_con != null && m_con instanceof BPDSVContainer)
		{
			encoding = ((BPDSVContainer) m_con).getEncoding();
			delimiter = ((BPDSVContainer) m_con).getDelimiter();
		}
		else
		{
			encoding = "utf-8";
			delimiter = ",";
		}

		if (res != null && res.isIO())
		{
			BPDSVContainer con = new BPDSVContainer(encoding, delimiter);
			con.bind(res);
			return con;
		}
		else
		{
			return null;
		}
	}

	public final static class BPEditorFactoryDSV implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatCSV.FORMAT_CSV, BPFormatTSV.FORMAT_TSV };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPDSVEditor();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isIO())
			{
				String encoding = LogicUtil.PAR_NN(options, o -> LogicUtil.IFVR(o, o2 -> TextUtil.eds(((BPConfig) o2).get("encoding"))), o -> "utf-8");
				String delimiter = null;
				BPFormat formatt = BPFormatManager.getFormatByName(format.getName());
				if (formatt != null && formatt.checkFeature(BPFormatFeature.DSV))
					delimiter = ((BPFormatDSV) formatt).getDelimiter();
				if (delimiter == null)
					delimiter = ",";
				BPDSVContainer con = new BPDSVContainer(encoding, delimiter);
				con.bind(res);
				((BPDSVEditor) editor).bind(con);
			}
		}

		public String getName()
		{
			return "DSV Editor";
		}

		public BPSetting getSetting(String formatkey)
		{
			BPSettingBase rc = new BPSettingBase();
			rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			return rc;
		}
	}
}
