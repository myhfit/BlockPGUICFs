package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.config.UIConfigs;
import bp.data.BPTextContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatXML;
import bp.res.BPResource;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPTextPane;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncsObjectSimple;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPXMLPanel extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2087484676199892669L;

	protected BPTreeComponentBase m_tree;
	protected Consumer<BPEditorPane> m_changedhandler;
	protected AtomicBoolean m_changed;
	protected boolean m_canpreview;
	protected JScrollPane m_scroll2;
	protected JPanel m_sp;

	public BPXMLPanel()
	{
	}

	protected void init()
	{
		m_changed = new AtomicBoolean(false);
		m_sp = new JPanel();
		m_scroll2 = new JScrollPane();
		m_tree = new BPTreeComponentBase();
		m_scroll = new JScrollPane();
		m_txt = createTextPane();
		m_changedhandler = this::onTextChanged;

		m_tree.setActionMap(getActionMap());
		m_tree.setRootVisible(false);
		m_tree.setMonoFont();
		m_tree.setCellRenderer(new BPXMLMapObjectCellRenderer());
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_txt.setOnPosChanged(this::onPosChanged);
		m_scroll2.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		setLayout(new BorderLayout());
		m_scroll.setViewportView(m_txt);
		m_scroll2.setViewportView(m_tree);
		m_sp.setLayout(new GridLayout(1, 2, 0, 0));
		m_sp.add(m_scroll);
		m_sp.add(m_scroll2);
		add(m_sp, BorderLayout.CENTER);

		m_txt.setChangedHandler(m_changedhandler);

		initActions();
		initListeners();

		m_canpreview = true;

		preview(m_txt);
	}

	protected void setTextContainerValue(String text)
	{
		super.setTextContainerValue(text);
		preview(m_txt);
	}

	protected void preview(BPEditorPane txt)
	{
		m_changed.set(true);
		UIUtil.laterUI(() ->
		{
			if (m_changed.compareAndSet(true, false))
			{
				doRefresh();
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
					doRefresh();
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	protected void doRefresh()
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		Document doc = null;
		String en = m_con.getEncoding();
		if (en == null)
			en = "utf-8";
		BPTextPane txt = m_txt;
		if (txt == null)
			return;
		try (ByteArrayInputStream bis = new ByteArrayInputStream(TextUtil.fromString(txt.getText(), en)))
		{
			DocumentBuilder db = dbfac.newDocumentBuilder();
			doc = db.parse(bis);
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			Std.err(e);
		}
		doc.getDocumentElement().normalize();
		Map<String, Object> obj = (Map<String, Object>) transNode(doc.getDocumentElement());

		m_tree.setModel(new BPTreeModel(new BPTreeFuncsObjectSimple(obj, ".elements")));
	}

	protected Object transNode(Node node)
	{
		if (node == null)
			return null;
		Map<String, Object> rc = null;
		String nname = node.getNodeName();
		String nv = node.getNodeValue();
		if (nname.equals("#text"))
		{
			if (nv.trim().length() == 0)
				return null;
			else
			{
				return nv.trim();
			}
		}
		rc = new LinkedHashMap<String, Object>();
		rc.put(".nodename", node.getNodeName());
		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null)
		{
			for (int i = 0; i < nnm.getLength(); i++)
			{
				Node n = nnm.item(i);
				rc.put(n.getNodeName(), n.getNodeValue());
			}
		}
		if (nv != null && nv.length() > 0)
			rc.put(".nodevalue", node.getNodeValue());
		NodeList nl = node.getChildNodes();
		if (nl != null && nl.getLength() > 0)
		{
			List<Object> chds = new ArrayList<Object>();
			for (int i = 0; i < nl.getLength(); i++)
			{
				Object subnode = transNode(nl.item(i));
				if (subnode != null)
					chds.add(subnode);
			}
			rc.put(".elements", chds);
		}
		return rc;
	}

	public void toggleRightPanel()
	{
		boolean canpreview = !m_canpreview;
		m_canpreview = canpreview;
		if (canpreview)
		{
			m_sp.add(m_scroll2);
			onTextChanged(m_txt);
		}
		else
		{
			m_sp.remove(m_scroll2);
		}
		m_sp.validate();
	}

	public static class BPEditorFactoryXML implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatXML.FORMAT_XML };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPXMLPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			BPXMLPanel pnl = (BPXMLPanel) editor;
			BPTextContainerBase con = new BPTextContainerBase();
			if (options != null)
			{
				LogicUtil.VLF(((String) options.get("encoding")), TextUtil::checkNotEmpty, con::setEncoding);
			}
			con.bind(res);
			pnl.bind(con);
		}

		public String getName()
		{
			return "XML Editor";
		}

		public boolean handleFormat(String formatkey)
		{
			return true;
		}

		public BPSetting getSetting(String formatkey)
		{
			BPSettingBase rc = new BPSettingBase();
			rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			return rc;
		}
	}

	protected static class BPXMLMapObjectCellRenderer extends DefaultTreeCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4941677931369336844L;

		@SuppressWarnings("unchecked")
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			Object v = value;
			if (v instanceof BPTreeNode)
				v = ((BPTreeNode) v).getUserObject();
			if (v != null)
			{
				if (v.getClass().isArray())
				{
					Object[] vs = (Object[]) v;
					v = vs[0] + ":" + vs[1];
				}
				else if (v instanceof Map)
				{
					StringBuilder sb = new StringBuilder();
					Map<String, ?> mo = (Map<String, ?>) v;
					sb.append("{");
					for (String k : mo.keySet())
					{
						if (".elements".equals(k))
							continue;
						if (".nodename".equals(k))
						{
							sb.append(mo.get(k));
							continue;
						}
						if (".nodevalue".equals(k))
						{
							sb.append(":" + mo.get(k));
							continue;
						}
						sb.append(" " + k + "=" + mo.get(k));
					}
					sb.append("}");
					v = sb.toString();
				}
				else if (v instanceof String)
					v = "\"" + v + "\"";
			}
			return super.getTreeCellRendererComponent(tree, v, selected, expanded, leaf, row, hasFocus);
		}
	}

}