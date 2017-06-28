import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.etsy.net.*;

public class TestUnixDomainSocket {

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 1) {
			System.out
					.println("usage: java TestUnixDomainSocket socketfilename");
			System.exit(1);
		}
		String socketFile = args[0];

		byte[] b = new byte[128];
		// Testcase 1.1: Test UnixDomainSocketClient with a stream socket
		UnixDomainSocketClient socket = new UnixDomainSocketClient(socketFile,
				JUDS.SOCK_STREAM);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		in.read(b);
		System.out.println("Text received: \"" + new String(b) + "\"");
		String text = "[2] Hello! I'm the client!";
		out.write(text.getBytes());
		System.out.println("Text sent: " + "\"" + text + "\"");
		socket.close();

        Thread.sleep(2000);

		// Testcase 1.2: Test UnixDomainSocketClient with a datagram socket
		socket = new UnixDomainSocketClient(socketFile,
				JUDS.SOCK_DGRAM);
		System.out.println("Provoke and catch an "
				+ "UnsupportedOperationException:");
		try {
			in = socket.getInputStream();
		} catch (UnsupportedOperationException e) {
			System.out.println("UnsupportedOperationException has been "
					+ "thrown as expected.");
		}
		out = socket.getOutputStream();
		text = "[3] Hello! I'm the client!";
		out.write(text.getBytes());
		System.out.println("Text sent: \"" + text + "\"");
		socket.close();

        Thread.sleep(2000);

		// Testcase 1.3: Test UnixDomainSocketClient with a SEQPACKET socket
		socket = new UnixDomainSocketClient(socketFile,JUDS.SOCK_SEQPACKET);
		System.out.println("Provoke and catch an "
				+ "UnsupportedOperationException:");
		try {
			in = socket.getInputStream();
		} catch (UnsupportedOperationException e) {
			System.out.println("UnsupportedOperationException has been "
					+ "thrown as expected.");
		}
		out = socket.getOutputStream();
		text = "[3] Hello! I'm the client!";
		out.write(text.getBytes());
		System.out.println("Text sent: \"" + text + "\"");
		socket.close();

		// Testcase 2.1: Test UnixDomainSocketServer with a stream socket
		System.out.println("\nTest #2: Test UnixDomainSocketServer\nTestcase "
				+ "2.1: Test UnixDomainSocketServer with a stream socket...");
		UnixDomainSocketServer ssocket = new UnixDomainSocketServer(socketFile,
				JUDS.SOCK_STREAM);
		in = ssocket.getInputStream();
		out = ssocket.getOutputStream();
		in.read(b);
		System.out.println("Text received: \"" + new String(b) + "\"");
		text = "[5] Hello! I'm the server!";
		out.write(text.getBytes());
		System.out.println("Text sent: " + "\"" + text + "\"");
		ssocket.close();
		ssocket.unlink();

		// Testcase 2.2: Test UnixDomainSocketServer with a datagram socket
		System.out.println("Testcase 2.2: Test UnixDomainSocketServer with "
				+ "a datagram socket...");
		ssocket = new UnixDomainSocketServer(socketFile,
				JUDS.SOCK_DGRAM);
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
