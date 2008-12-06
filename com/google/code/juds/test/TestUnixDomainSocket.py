#!/usr/bin/env python

import sys, os, socket, time

BUFSIZE = 128

def main():
    if len(sys.argv) != 2:
        print "usage: %s socketfilename" % sys.argv[0]
        sys.exit(1)
    socket_file = sys.argv[1]

    print ("Test #1: Test UnixDomainSocketClient\n"
        "Testcase 1.1: Test UnixDomainSocketClient with a stream socket...")
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    s.bind(socket_file)
    s.listen(0)
    client, client_address = s.accept()
    text = "Hello I'm the server..."
    client.send(text)
    print "Text sent: \"Hello I'm the server...\""
    text = client.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()
    os.unlink(socket_file)

    print "Testcase 1.2: Test UnixDomainSocketClient with a datagram socket..."
    s = socket.socket(socket.AF_UNIX, socket.SOCK_DGRAM)
    s.bind(socket_file)
    text = s.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()
    os.unlink(socket_file)

    print ("\nTest #2: Test UnixDomainSocketServer\n"
        "Testcase 2.1: Test UnixDomainSocketServer with a stream socket...")
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    time.sleep(2)   # wait for the server
    s.connect(socket_file)
    text = "Hello I'm the client..."
    s.send(text)
    print "Text sent: \"Hello I'm the client...\""
    text = s.recv(BUFSIZE)
    print "Text received: \"%s\"" % text
    s.close()

    print "Testcase 2.2: Test UnixDomainSocketServer with a datagram socket..."
    s = socket.socket(socket.AF_UNIX, socket.SOCK_DGRAM)
    time.sleep(2)   # wait for the server
    s.connect(socket_file)
    text = "Hello I'm the client..."
    s.send(text)
    print "Text sent: \"%s\"" % text
    s.close()


if __name__ == "__main__":
    main()
