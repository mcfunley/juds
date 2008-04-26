// UnixDomainSocket.java
// Inspired by J-BUDS version 1.0
// See COPYRIGHT file for license details
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * This class provides a means of using unix domain socket client/server
 * connections in Java
 * 
 * @author Klaus Trainer
 */
public abstract class UnixDomainSocket {

	static {
		// Load the Unix Domain Socket C library
		System.loadLibrary("unixdomainsocket");
	}

	/**
	 * A constant for the datagram socket type (connectionless).
	 */
	public static final int SOCK_DGRAM = 0;

	/**
	 * A constant for the stream oriented stream socket type (connection-based)
	 */
	public static final int SOCK_STREAM = 1;

	protected UnixDomainSocketInputStream in;

	protected UnixDomainSocketOutputStream out;

	protected int nativeSocketFileHandle;

	// Name of the socket file
	protected String socketFile;

	protected int socketType;

	private int timeout;

	// Native methods implemented in the Unix Domain Socket C library
	protected native static int nativeCreate(String socketFile, int socketType);

	protected native static int nativeOpen(String socketFile, int socketType);

	protected native static int nativeRead(int nativeSocketFileHandle,
			byte[] b, int off, int len);

	protected native static int nativeWrite(int nativeSocketFileHandle,
			byte[] b, int off, int len);

	protected native static int nativeClose(int nativeSocketFileHandle);

	protected native static int nativeCloseInput(int nativeSocketFileHandle);

	protected native static int nativeCloseOutput(int nativeSocketFileHandle);

	protected native static int nativeUnlink(String socketFile);

	/**
	 * Returns an input stream for this socket.
	 * 
	 * @return An input stream for reading bytes from this socket
	 */
	public InputStream getInputStream() {
		return (InputStream) in;
	}

	/**
	 * Returns an output stream for this socket.
	 * 
	 * @return An output stream for writing bytes to this socket
	 */
	public OutputStream getOutputStream() {
		return (OutputStream) out;
	}

	/**
	 * Sets the read timeout for the socket. If a read call blocks for the
	 * specified amount of time it will be cancelled, and a
	 * java.io.InterruptedIOException will be thrown. A <code>timeout</code>
	 * of zero is interpreted as an infinite timeout.
	 * 
	 * @param timeout
	 *            The specified timeout, in milliseconds.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * Closes the socket
	 */
	public void close() {
		nativeClose(nativeSocketFileHandle);
	}

	/**
	 * Unlink socket file
	 */
	public void unlink() {
		nativeUnlink(socketFile);
	}

	protected class UnixDomainSocketInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			byte[] b = new byte[1];
			int count;
			// If a timeout is set, then use a read thread
			if (timeout > 0) {
				// Create a thread to read the byte
				UnixDomainSocketReadThread thread = new UnixDomainSocketReadThread(
						b, 0, 1);
				thread.setDaemon(true);
				thread.start();
				try {
					// Wait up until the specified timeout for the thread to
					// complete
					thread.join(timeout);
				} catch (InterruptedException e) {
				}
				// If the thread is still alive the read() call has
				// blocked longer than the specified timeout
				if (thread.isAlive()) {
					throw new InterruptedIOException(
							"Unix Domain Socket read() call timed out");
				} else {
					count = thread.getData();
				}
			} else {
				count = nativeRead(nativeSocketFileHandle, b, 0, 1);
				if (count == -1)
					throw new IOException();
			}
			return (int) b[0];
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}

			int count;
			// If a timeout is set, then use a read thread
			if (timeout > 0) {
				// Create a thread to read the byte
				UnixDomainSocketReadThread thread = new UnixDomainSocketReadThread(
						b, off, len);
				thread.setDaemon(true);
				thread.start();
				try {
					// Wait up until the specified timeout for the thread to
					// complete
					thread.join(timeout);
				} catch (InterruptedException e) {
				}
				// If the thread is still alive the read() call has
				// blocked longer than the specified timeout
				if (thread.isAlive()) {
					throw new InterruptedIOException(
							"Unix Domain Socket read() call timed out");
				} else {
					count = thread.getData();
				}
			} else {
				count = nativeRead(nativeSocketFileHandle, b, off, len);
				if (count == -1)
					throw new IOException();
			}
			return count;
		}

		// Closes the socket input stream
		public void close() throws IOException {
			nativeCloseInput(nativeSocketFileHandle);
		}
	}

	protected class UnixDomainSocketOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			byte[] data = new byte[1];
			data[0] = (byte) b;
			if (nativeWrite(nativeSocketFileHandle, data, 0, 1) != 1)
				throw new IOException("Unable to write to unix domain socket");
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if ((off < 0) || (off > b.length) || (len < 0)
					|| ((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			if (nativeWrite(nativeSocketFileHandle, b, off, len) != len)
				throw new IOException("Unable to write to unix domain socket");
		}

		// Closes the socket output stream
		public void close() throws IOException {
			nativeCloseOutput(nativeSocketFileHandle);
		}
	}

	protected class UnixDomainSocketReadThread extends Thread {
		private int count, off, len;

		private byte[] b;

		public UnixDomainSocketReadThread(byte[] b, int off, int len) {
			this.b = b;
			this.off = off;
			this.len = len;
		}

		public void run() {
			count = nativeRead(nativeSocketFileHandle, b, off, len);
		}

		public int getData() {
			return count;
		}
	}
}