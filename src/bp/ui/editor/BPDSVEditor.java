package bp.ui.editor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import bp.data.BPXYData;
import bp.format.BPFormat;
import bp.format.BPFormatCSV;
import bp.format.BPFormatDSV;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.format.BPFormatTSV;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.ui.actions.BPActionHolder;
import bp.ui.actions.BPDSVActions;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.Std;
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

	protected void postInitTable()
	{
		m_funcs.setColCheck(true);
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

	public void showSplit(ActionEvent e)
	{
		Integer n = ObjUtil.toInt(UIStd.input("100", "Input Split Number", null), null);
		if (n != null)
		{
			BPResource res = CommonUIOperations.showSaveResource(null, getExts(), null);
			if (res != null)
			{
				BPResourceDir dir = (BPResourceDir) res.getParentResource();
				String suffix = res.getName();
				String ext = res.getExt();
				suffix = suffix.substring(0, suffix.length() - ext.length());
				saveSplitDSV(n, dir, suffix, ext);
			}
		}
	}

	public void saveSplitDSV(int splitnum, BPResourceDir dir, String suffix, String ext)
	{
		List<BPXData> datas = m_model.getDatas();
		int n = datas.size();
		BPDSVContainer con = (BPDSVContainer) m_con;
		for (int i = 0; i < n; i += splitnum)
		{
			List<BPXData> subdatas = new ArrayList<BPXData>();
			for (int j = i; j < (i + splitnum) && j < n; j++)
				subdatas.add(datas.get(j));
			BPXYDData xydata = createSaveData(m_funcs.getColumnNames(), m_funcs.getColumnClasses(), m_funcs.getColumnLabels(), subdatas);
			BPResource sres = dir.getChild(suffix + "_" + i + ext, false);
			String encoding = con == null ? "utf-8" : con.getEncoding();
			String delimiter = ",";
			{
				BPFormat format = BPFormatManager.getFormatByExt(ext);
				if (format == null || (!format.checkFeature(BPFormatFeature.DSV)))
					format = new BPFormatCSV();
				delimiter = ((BPFormatDSV) format).getDelimiter();
			}

			try (BPDSVContainer newcon = new BPDSVContainer(encoding, delimiter))
			{
				newcon.bind(sres);
				newcon.open();
				newcon.writeXYData(xydata);
			}
			finally
			{
			}
		}
		UIStd.info("Split finished");
	}

	public void showAppendDSV(ActionEvent e)
	{
		BPResource[] ress = CommonUIOperations.showSelectResources(null, dlg -> dlg.setFilterWithExts(new String[] { ".csv", ".dsv" }));
		if (ress != null && ress.length > 0)
			doAppendDSV(ress);
	}

	public void doAppendDSV(BPResource[] ress)
	{
		int colsize;
		Map<String, Integer> colmap = new HashMap<>();
		{
			String[] cls = m_funcs.getColumnLabels();
			if (cls != null)
			{
				for (int i = 0; i < cls.length; i++)
					colmap.put(cls[i], i);
			}
			colsize = cls.length;
		}
		int total = ress.length;
		int c = 0;

		BPTableModel<BPXData> model = m_model;
		for (BPResource res : ress)
		{
			String en = m_con == null ? "utf-8" : ((BPDSVContainer) m_con).getEncoding();

			String delimiter;
			{
				BPFormat format = BPFormatManager.getFormatByExt(res.getExt());
				if (format != null && format.checkFeature(BPFormatFeature.DSV))
					delimiter = ((BPFormatDSV) format).getDelimiter();
				else
					delimiter = ",";
			}
			try (BPDSVContainer con = new BPDSVContainer(en, delimiter))
			{
				con.bind(res);
				con.open();
				BPXYData xydata = con.readXYData();
				String[] cls = xydata.getColumnLabels();
				if (cls == null)
					cls = xydata.getColumnNames();
				int[] colidcies = new int[cls.length];
				for (int i = 0; i < cls.length; i++)
				{
					String cl = cls[i];
					Integer idx = colmap.get(cl);
					colidcies[i] = idx == null ? -1 : idx;
				}
				List<BPXData> lines = xydata.getDatas();
				List<BPXData> newlines = new ArrayList<BPXData>();
				for (BPXData line : lines)
				{
					Object[] newlinearr = new Object[colsize];
					for (int i = 0; i < line.length(); i++)
					{
						int colidx = colidcies[i];
						if (colidx > -1)
							newlinearr[colidx] = line.getColValue(i);
					}
					BPXData newline = new BPXData.BPXDataArray(newlinearr);
					newlines.add(newline);
				}
				model.addAll(newlines);
				model.fireTableDataChanged();
				c++;
			}
			catch (Exception e)
			{
				Std.err(e);
			}
		}
		UIStd.info("success:" + c + "/" + total);
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
