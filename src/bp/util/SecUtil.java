package bp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecUtil
{
	public final static byte[] md5(byte[] bs)
	{
		return wrapE(() -> md(bs, "MD5"));
	}

	public final static byte[] md(byte[] bs, String al) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance(al);
		byte[] bsnew = md.digest(bs);
		return bsnew;
	}

	private final static <T> T wrapE(ESupplier<T> seg)
	{
		try
		{
			return seg.get();
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return null;
	}

	private static interface ESupplier<T>
	{
		public T get() throws Exception;
	}
}
