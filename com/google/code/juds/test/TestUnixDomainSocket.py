#!/usr/bin/env python

import sys, os, socket, time

BUFSIZE = 128

def main():
    if len(sys.argv) != 2:
        print "usage: %s socketfilename" % sys.argv[0]
        sys.exit(1)
    socket_file = sys.argv[1]

    # Testcase 1.1: Test UnixDomainSocketClient with a stream socket
    print ("Test #1: Test UnixDomainSocketClient\n"
        "Testcase 1.1: Test UnixDomainSocketClient with a stream socket...")
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    s.bind(socket_file)
    s.listen(0)
    client, client_address = s.accept()
    text = "[1] Hello I'm the server!"
    client.send(text)
    print "Text sent: \"%s\"" % text
    text = client.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()
    os.unlink(socket_file)
    
    # Testcase 1.2: Test UnixDomainSocketClient with a datagram socket
    print "Testcase 1.2: Test UnixDomainSocketClient with a datagram socket..."
    s = socket.socket(socket.AF_UNIX, socket.SOCK_DGRAM)
    s.bind(socket_file)
    text = s.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()
    os.unlink(socket_file)
    
    time.sleep(1)   # wait for the server

    # Testcase 2.1: Test UnixDomainSocketServer with a stream socket
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    s.connect(socket_file)
    text = "[4] Hello I'm the client!"
    s.send(text)
    print "Text sent: \"%s\"" % text
    text = s.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()
    
    time.sleep(1)   # wait for the server

    # Testcase 2.2: Test UnixDomainSocketServer with a datagram socket
    s = socket.socket(socket.AF_UNIX, socket.SOCK_DGRAM)
    s.connect(socket_file)
    text = "[6] Hello I'm the client!"
    s.send(text)
    print "Text sent: \"%s\"" % text
    s.close()


if __name__ == "__main__":
    main()
