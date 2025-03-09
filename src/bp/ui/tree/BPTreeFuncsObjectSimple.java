package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.ui.scomp.BPTree.BPTreeNode;

public class BPTreeFuncsObjectSimple implements BPTreeFuncs
{
	protected Object m_root;
	protected String m_childkey;

	public BPTreeFuncsObjectSimple(Object data, String childkey)
	{
		m_root = data;
		m_childkey = childkey;
	}

	public List<?> getRoots()
	{
		List<Object> rc = new ArrayList<Object>();
		rc.add(m_root);
		return rc;
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta)
	{
		Object v = node.getUserObject();
		return getChildren(v);
	}

	public List<?> getChildren(Object v)
	{
		List<Object> rc = null;
		if (v != null)
		{
			if (v instanceof List)
			{
				rc = new ArrayList<Object>();
				List<?> vs = (List<?>) v;
				for (Object chd : vs)
				{
					rc.add(chd);
				}
			}
			else if (v instanceof Map)
			{
				rc = new ArrayList<Object>();
				Map<?, ?> vm = (Map<?, ?>) v;
				Object chdobj = vm.get(m_childkey);
				if (chdobj instanceof List)
				{
					rc.addAll((List<?>) chdobj);
				}
				// for (Entry<?, ?> entry : vm.entrySet())
				// {
				// rc.add(new Object[] { entry.getKey(), entry.getValue() });
				// }
				// rc.sort((a, b) ->
				// {
				// return ((String) ((Object[])
				// a)[0]).toLowerCase().compareTo(((String) ((Object[])
				// b)[0]).toLowerCase());
				// });
			}
			// else if (v.getClass().isArray())
			// {
			// rc = new ArrayList<Object>();
			// Object chdvs = ((Object[]) v)[1];
			// if (chdvs != null && (chdvs instanceof List || chdvs instanceof
			// Map || chdvs.getClass().isArray()))
			// rc.addAll(getChildren(chdvs));
			// else
			// rc.add(chdvs);
			// }
		}
		return rc;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		Object v = node.getUserObject();
		if (v == null)
			return false;
		return !(v instanceof List || v instanceof Map || v.getClass().isArray());
	}
}
