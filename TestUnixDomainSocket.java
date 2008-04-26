import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestUnixDomainSocket {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out
					.println("usage: java TestUnixDomainSocket socketfilename");
			System.exit(1);
		}
		String socketFile = args[0];

		byte[] b = new byte[128];
		UnixDomainSocketClient socket = new UnixDomainSocketClient(socketFile,
				UnixDomainSocket.SOCK_STREAM);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		System.out.println("Test #1: Test UnixDomainSocketClient\nTestcase "
				+ "1.1: Test UnixDomainSocketClient with a stream socket...");
		in.read(b);
		System.out.println("Text received: \"" + new String(b) + "\"");
		String text = "Hello! I'm the client!";
		out.write(text.getBytes());
		System.out.println("Text sent: " + "\"" + text + "\"");
		socket.close();

		System.out.println("Testcase 1.2: Test UnixDomainSocketClient with "
				+ "a datagram socket...");
		socket = new UnixDomainSocketClient(socketFile,
				UnixDomainSocket.SOCK_DGRAM);
		System.out.println("Provoke and catch an "
				+ "UnsupportedOperationException:");
		try {
			in = socket.getInputStream();
		} catch (UnsupportedOperationException e) {
			System.out.println("UnsupportedOperationException has been "
					+ "thrown as expected.");
		}
		out = socket.getOutputStream();
		text = "Hello! I'm the client!";
		out.write(text.getBytes());
		System.out.println("Text sent: \"" + text + "\"");
		socket.close();

		UnixDomainSocketServer ssocket = new UnixDomainSocketServer(socketFile,
				UnixDomainSocket.SOCK_STREAM);
		in = ssocket.getInputStream();
		out = ssocket.getOutputStream();
		System.out.println("\nTest #2: Test UnixDomainSocketServer\nTestcase "
				+ "2.1: Test UnixDomainSocketServer with a stream socket...");
		in.read(b);
		System.out.println("Text received: \"" + new String(b) + "\"");
		text = "Hello! I'm the server!";
		out.write(text.getBytes());
		System.out.println("Text sent: " + "\"" + text + "\"");
		ssocket.close();
		ssocket.unlink();

		System.out.println("Testcase 2.2: Test UnixDomainSocketServer with "
				+ "a datagram socket...");
		ssocket = new UnixDomainSocketServer(socketFile,
				UnixDomainSocket.SOCK_DGRAM);
		System.out.println("Provoke and catch an "
				+ "UnsupportedOperationException:");
		in = ssocket.getInputStream();
		try {
			out = ssocket.getOutputStream();
		} catch (UnsupportedOperationException e) {
			System.out.println("UnsupportedOperationException has been "
					+ "thrown as expected.");
		}
		in.read(b);
		System.out.println("Text received: \"" + new String(b) + "\"");
		ssocket.close();
		ssocket.unlink();
	}
}
