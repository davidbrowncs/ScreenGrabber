
package fileHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

// Taken from:
// http://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
public final class IPChecker
{
	public static String getIp() throws Exception
	{
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}