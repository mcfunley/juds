package com.google.code.juds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnixDomainSocketServer extends UnixDomainSocket {
    /**
     * Creates a Unix domain socket and connects it to the server specified by
     * the socket file.
     * Simplified API, use this with bcklog parameter to use listen feature
     * 
     * @param socketFile
     *            Name of the socket file
     * @param socketType
     *            Either SOCK_STREAM or SOCK_DGRAM
     *
     * @exception IOException
     *                If unable to construct the socket
     */
    public UnixDomainSocketServer(String socketFile, int socketType)
            throws IOException {
        super.socketFile = socketFile;
        super.socketType = socketType;

        if ((nativeSocketFileHandle = nativeCreate(socketFile, socketType)) == -1)
            throw new IOException("Unable to open Unix domain socket");

        // Initialize the socket input and output streams
        in = new UnixDomainSocketInputStream();
        if (socketType == UnixDomainSocket.SOCK_STREAM)
            out = new UnixDomainSocketOutputStream();
    }

    /**
     * Creates a Unix domain socket and connects it to the server specified by
     * the socket file.
     * 
     * @param socketFile
     *            Name of the socket file
     * @param socketType
     *            Either SOCK_STREAM or SOCK_DGRAM
     * @param backlog
     *            maximum number of simultanenous connections pending to be accepted.
     *
     * @exception IOException
     *                If unable to construct the socket
     */
         public UnixDomainSocketServer(String pSocketFile, int pSocketType, int backlog)
            throws IOException {
        super.socketFile = pSocketFile;
        super.socketType = pSocketType;

        if ((nativeSocketFileHandle = nativeListen(socketFile, socketType, backlog)) == -1)
            throw new IOException("Unable to open and listen on Unix domain socket");
    }

        public UnixDomainSocket accept()
        throws IOException
        {
        int newSocketFileHandle = -1;
        if ((newSocketFileHandle = nativeAccept(nativeSocketFileHandle, socketType)) == -1)
            {
            throw new IOException("Unable to accept on Unix domain socket");
            }
        return new UnixDomainSocket(newSocketFileHandle, socketType) {};
    }

    /**
     * with new accept() API this should not be used
     * this method was kept for compatibility with simplified API
     */
    @Override
    public OutputStream getOutputStream()
    {
        if (out == null )
        {
            throw new UnsupportedOperationException();
        }
        return out;
    }

    /**
     * with new accept() API this should not be used
     * this method was kept for compatibility with simplified API
     */
    @Override
    public InputStream getInputStream()
    {
        if (in == null)
        {
            throw new UnsupportedOperationException();
        }
        return in;
    }
}
