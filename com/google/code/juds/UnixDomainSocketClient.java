package com.google.code.juds;

import java.io.IOException;
import java.io.InputStream;

public class UnixDomainSocketClient extends UnixDomainSocket {
	/**
	 * Creates a Unix domain socket and connects it to the server specified by
	 * the socket file.
	 * 
	 * @param socketFile
	 *            Name of the socket file
	 * 
	 * @exception IOException
	 *                If unable to construct the socket
	 */

	public UnixDomainSocketClient(String socketFile, int socketType)
			throws IOException {
		super.socketFile = socketFile;
		super.socketType = socketType;

		if ((nativeSocketFileHandle = nativeOpen(socketFile, socketType)) == -1)
			throw new IOException("Unable to open Unix domain socket");

		// Initialize the socket input and output streams
		if (socketType == UnixDomainSocket.SOCK_STREAM)
			in = new UnixDomainSocketInputStream();
		out = new UnixDomainSocketOutputStream();
	}

	/**
	 * Returns an input stream for this socket.
	 * 
	 * @exception UnsupportedOperationException
	 *                if <code>getInputStream</code> is invoked for an
	 *                <code>UnixDomainSocketClient</code> of type
	 *                <code>UnixDomainSocket.SOCK_DGRAM</code>.
	 * @return An input stream for writing bytes to this socket
	 */
	@Override
	public InputStream getInputStream() {
		if (socketType == UnixDomainSocket.SOCK_STREAM)
			return (InputStream) in;
		else
			throw new UnsupportedOperationException();
	}
}
