import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.etsy.net.*;


/**
 * Standalone java Test code for new API with listen/accept
 * Can be used as example.
 * 
 * Test is just to exchange string hello handshake between a server and a client through a unix domain socket with file given in argument.
 * 
 * @author plhardy
 *
 */
public class TestUnixDomainSocketServer 
{

	private String socketFileName;
	private int socketType;
	private int errors;
	
	private class SocketConnection {
		private String description;
		private InputStream is;
		private OutputStream os;
		public SocketConnection(String pDescription, UnixDomainSocket pUnixDomainSocket)
		throws IOException
		{
			description = pDescription;
			is = pUnixDomainSocket.getInputStream();
			os = pUnixDomainSocket.getOutputStream();
		}
		
		public void expect(String pSentence)
		throws IOException
		{
			byte[] expected = new byte[pSentence.getBytes("UTF8").length];
			int read = is.read(expected);
			if (read == expected.length)
			{
				if (new String(expected, "UTF8").equals(pSentence))
				{
					logInfo("" + description + " received :" + pSentence);
					return;
				}
			}
			throw new IOException("Unexpected");
		}
		
		public void send(String pSentence)
		throws IOException
		{
			os.write(pSentence.getBytes("UTF8"));
			logInfo("" + description + " sent :" + pSentence);
		}
	}
	
	public TestUnixDomainSocketServer(String pSocketFileName, int pSocketType)
	{
		socketFileName = pSocketFileName;
		socketType = pSocketType;
		errors=0;
	}
	
	public UnixDomainSocketServer initServer()
	throws IOException
	{
		 return new UnixDomainSocketServer(socketFileName, socketType,3 );
	}
	
	public void runServer(UnixDomainSocketServer serverSocket)
	throws IOException
	{
		
		UnixDomainSocket socket = serverSocket.accept();	
		SocketConnection connection = new SocketConnection("Server <- Client on " + socketFileName, socket);
		connection.expect("Client Hello");
		connection.send("Server Hello");
	}
	
	public void logInfo( String pInfo)
	{
		System.out.println(pInfo);
	}
	
	public void logError( Object[] pInfos, Throwable pThrowable)
	{
		errors ++;
		if (pThrowable != null)
		{
			pThrowable.printStackTrace(System.err);
		}
		System.out.println(pInfos);
	}
	
	public int getErrors() {
		return errors;
	}

	public void runClient()
	throws IOException
	{
		UnixDomainSocketClient clientSocket = new UnixDomainSocketClient(socketFileName, socketType);
		SocketConnection connection = new SocketConnection("Client -> Server on " + socketFileName, clientSocket);		
		connection.send("Client Hello");
		connection.expect("Server Hello");
	}
	
	public void fullTest()
	throws InterruptedException, IOException
	{
		
		// do it now to avoid any race condition between socket creation and client first start
		final UnixDomainSocketServer server = initServer();
		
		try {
			Thread serverThread = new Thread()
			{
				public void run()
				{
					try {
						runServer(server);
					}
					catch (IOException ioException)
					{
						logError(new Object[]{"Server socket failure"},ioException);
					}
				}
			};
			
			serverThread.start();
			Thread clientThread = new Thread()
			{
				public void run()
				{
					try {
						runClient();
					}
					catch (IOException ioException)
					{
						logError(new Object[]{"Client socket failure"}, ioException);
					}
				}
			};
			
			clientThread.start();
			clientThread.join();
	
			serverThread.join();
		}
		finally
		{
			if (server !=null)
			{
				server.unlink();
			}
		}
		
		
	}
		
	public static void main(String[] args)
	{
		if (args.length != 1) {
			System.out
					.println("usage: java TestUnixDomainSocketServer socketfilename");
			System.exit(1);
		}
		TestUnixDomainSocketServer thisTest = new TestUnixDomainSocketServer(args[0], JUDS.SOCK_STREAM);
		
		try {
			thisTest.fullTest();
		}
		catch (Exception exception)
		{
			thisTest.logError(null, exception);
		}
			
		if (thisTest !=null)
		{
			if (thisTest.getErrors() == 0)
			{
				System.out.println("Test OK");
			}
		}	
	}
}
