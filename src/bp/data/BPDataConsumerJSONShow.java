package bp.data;

import java.util.List;

import bp.data.BPDataConsumer.BPDataConsumerTextCollector;
import bp.format.BPFormatJSON;
import bp.format.BPFormatText;
import bp.tool.BPToolGUIJSON;
import bp.util.ObjUtil;

public class BPDataConsumerJSONShow extends BPDataConsumerTextCollector
{
	public void finish()
	{
		super.finish();
		BPToolGUIJSON t = new BPToolGUIJSON();
		t.showTool(ObjUtil.makeMap("dest", m_text));
	}

	public static class BPDataEndpointFactoryJSONShow implements BPDataEndpointFactory
	{
		public String getName()
		{
			return "Parse JSON";
		}

		@SuppressWarnings("unchecked")
		public <D> BPDataConsumer<D> create(String formatname)
		{
			return (BPDataConsumer<D>) new BPDataConsumerJSONShow();
		}

		public List<String> getSupportedFormats()
		{
			return ObjUtil.makeList(BPFormatText.FORMAT_TEXT, BPFormatJSON.FORMAT_JSON);
		}
	}

	public String getInfo()
	{
		return "Show JSON UI";
	}

	public boolean isEndpoint()
	{
		return true;
	}
}
