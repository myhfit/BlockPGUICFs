package bp.tool;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogSelectData;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPToolSQButton;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPToolGUITime extends BPToolGUIBase<BPToolGUITime.BPToolGUIContextTime>
{
	public String getName()
	{
		return "Time";
	}

	protected BPToolGUIContextTime createToolContext()
	{
		return new BPToolGUIContextTime();
	}

	protected static class BPToolGUIContextTime implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPTextField m_txtts;
		protected BPTextField m_txtdf;
		protected BPTextField m_txttraw;
		protected BPTextField m_txttlocal;
		protected BPComboBox<TimeZone> m_cmbzones;
		protected BPTextField m_txtttarget;

		public void initUI(Container par, Object... params)
		{
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel pdest = new JPanel();
			JPanel psrcbox = new JPanel();
			JPanel pdestbox = new JPanel();
			BPLabel lblsrc = new BPLabel(" Source");
			BPLabel lbldest = new BPLabel(" Time");

			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

			lblsrc.setLabelFont();
			lbldest.setLabelFont();
			lblsrc.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			lbldest.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));

			sp.add(psrc);
			sp.add(pdest);
			psrc.setLayout(new BorderLayout());
			pdest.setLayout(new BorderLayout());
			psrcbox.setLayout(new BoxLayout(psrcbox, BoxLayout.Y_AXIS));
			pdestbox.setLayout(new BoxLayout(pdestbox, BoxLayout.Y_AXIS));

			m_txtts = new BPTextField();
			psrcbox.add(createLinePanel("Time(ms):", m_txtts, BPAction.build(" > ").callback(this::onToTime).getAction()));
			psrcbox.add(createLinePanel(new BPToolSQButton("Get Current", BPAction.build("current").callback(this::onGetCurrent).getAction())));
			psrcbox.add(Box.createGlue());

			m_txtdf = new BPTextField();
			m_txtdf.setText("yyyy-MM-dd HH:mm:ss.SSS");
			pdestbox.add(createLinePanel("Format:", m_txtdf, null));
			m_txttraw = new BPTextField();
			pdestbox.add(createLinePanel("Raw Time:", m_txttraw, BPAction.build(" < ").callback(this::onFromRawTime).getAction()));
			m_txttlocal = new BPTextField();
			pdestbox.add(createLinePanel("Local Time:", m_txttlocal, BPAction.build(" < ").callback(this::onFromLocalTime).getAction()));
			BPLabel lbl = new BPLabel("Time Zone");
			lbl.setLabelFont();
			pdestbox.add(createLinePanel(lbl));
			m_cmbzones = new BPComboBox<TimeZone>();
			m_cmbzones.setModel(new BPComboBoxModel<TimeZone>());
			m_cmbzones.setRenderer(new TimeZoneRenderer());
			pdestbox.add(createLinePanel("Time Zone:", m_cmbzones, BPAction.build(" ... ").callback(this::onSelectTimeZone).getAction()));
			m_txtttarget = new BPTextField();
			pdestbox.add(createLinePanel("Target Time:", m_txtttarget, BPAction.build(" < ").callback(this::onFromTargetTime).getAction()));

			pdestbox.add(Box.createGlue());

			psrc.add(lblsrc, BorderLayout.NORTH);
			psrc.add(psrcbox, BorderLayout.CENTER);
			pdest.add(lbldest, BorderLayout.NORTH);
			pdest.add(pdestbox, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
		}

		protected JPanel createLinePanel(Component comp)
		{
			JPanel linepan = new JPanel();
			linepan.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 0), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
			linepan.setLayout(new BorderLayout());
			linepan.add(comp, BorderLayout.CENTER);
			linepan.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
			return linepan;
		}

		protected JPanel createLinePanel(String lblstr, JComponent tf, Action act)
		{
			JPanel linepan = new JPanel();
			BPLabel lbl = new BPLabel(lblstr);
			tf.setBorder(new MatteBorder(0, 1, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
			linepan.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 0), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
			lbl.setMonoFont();
			lbl.setPreferredSize(new Dimension(UIUtil.scale(85), UIConfigs.TEXTFIELD_HEIGHT()));
			if (tf instanceof BPTextField)
			{
				((BPTextField) tf).setMonoFont();
			}
			else if (tf instanceof BPComboBox)
			{
				((BPComboBox<?>) tf).setMonoFont();
				m_cmbzones.setPreferredSize(new Dimension(20, UIConfigs.TEXTFIELD_HEIGHT() + 2));
			}

			linepan.setLayout(new BorderLayout());
			linepan.add(lbl, BorderLayout.WEST);
			linepan.add(tf, BorderLayout.CENTER);
			if (act != null)
			{
				BPToolSQButton btn = new BPToolSQButton((String) act.getValue(Action.NAME), act);
				linepan.add(btn, BorderLayout.EAST);
			}
			linepan.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
			return linepan;
		}

		public void initDatas(Object... params)
		{
			String[] tzs = TimeZone.getAvailableIDs();
			List<TimeZone> tzlist = new ArrayList<TimeZone>();
			for (String tz : tzs)
				tzlist.add(TimeZone.getTimeZone(tz));
			tzlist.sort((a, b) ->
			{
				int od = a.getRawOffset() - b.getRawOffset();
				if (od != 0)
					return od;
				return a.getDisplayName().compareTo(b.getDisplayName());
			});
			m_cmbzones.getBPModel().setDatas(tzlist);
			m_cmbzones.setSelectedItem(TimeZone.getDefault());
			setCurrentTime();
		}

		protected void onGetCurrent(ActionEvent e)
		{
			setCurrentTime();
		}

		protected void setCurrentTime()
		{
			m_txtts.setText(Long.toString(System.currentTimeMillis()));
		}

		protected void onToTime(ActionEvent e)
		{
			Long l = ObjUtil.toLong(m_txtts.getText(), null);
			if (l != null)
			{
				Date d = new Date(l);
				DateFormat df = new SimpleDateFormat(m_txtdf.getText().trim());
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				m_txttraw.setText(df.format(d));
				df.setTimeZone(TimeZone.getDefault());
				m_txttlocal.setText(df.format(d));
				df.setTimeZone((TimeZone) m_cmbzones.getSelectedItem());
				m_txtttarget.setText(df.format(d));
			}
		}

		protected void onFromRawTime(ActionEvent e)
		{
			DateFormat df = new SimpleDateFormat(m_txtdf.getText().trim());
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			try
			{
				Date d = df.parse(m_txttraw.getText().trim());
				m_txtts.setText(Long.toString(d.getTime()));
			}
			catch (ParseException e1)
			{
				UIStd.err(e1);
			}
		}

		protected void onSelectTimeZone(ActionEvent e)
		{
			List<TimeZone> tzs = m_cmbzones.getBPModel().getDatas();
			BPDialogSelectData<TimeZone> dlg = new BPDialogSelectData<TimeZone>();
			dlg.setTransFunc(BPToolGUIContextTime::timeZone2Str);
			dlg.setSource(tzs);
			dlg.setTitle("Select TimeZone");
			dlg.setFilterVisible(true);
			dlg.setVisible(true);
			TimeZone tz = dlg.getSelectData();
			if (tz != null)
			{
				m_cmbzones.setSelectedItem(tz);
			}
		}

		protected void onFromLocalTime(ActionEvent e)
		{
			DateFormat df = new SimpleDateFormat(m_txtdf.getText().trim());
			df.setTimeZone(TimeZone.getDefault());
			try
			{
				Date d = df.parse(m_txttlocal.getText().trim());
				m_txtts.setText(Long.toString(d.getTime()));
			}
			catch (ParseException e1)
			{
				UIStd.err(e1);
			}
		}

		protected void onFromTargetTime(ActionEvent e)
		{
			DateFormat df = new SimpleDateFormat(m_txtdf.getText().trim());
			df.setTimeZone((TimeZone) m_cmbzones.getSelectedItem());
			try
			{
				Date d = df.parse(m_txtttarget.getText().trim());
				m_txtts.setText(Long.toString(d.getTime()));
			}
			catch (ParseException e1)
			{
				UIStd.err(e1);
			}
		}

		protected static String timeZone2Str(TimeZone tz)
		{
			if (tz == null)
				return "";
			return "(" + (tz.getRawOffset() / 3600000d) + ") " + tz.getDisplayName() + ":" + tz.getID();
		}

		private static class TimeZoneRenderer extends DefaultListCellRenderer
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1903958635890783931L;

			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				return super.getListCellRendererComponent(list, timeZone2Str((TimeZone) value), index, isSelected, cellHasFocus);
			}
		}
	}
}