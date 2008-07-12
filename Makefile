CC = gcc
PLAT = linux
JAVA_HOME = /usr/lib/jvm/java-6-sun/include
INCLUDEPATH = -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/$(PLAT)
JAVA_FLAGS =

all: UnixDomainSocket.class UnixDomainSocketClient.class \
	UnixDomainSocketServer.class libunixdomainsocket.so

UnixDomainSocket$1.class: UnixDomainSocket.java
	javac $(JAVA_FLAGS) UnixDomainSocket.java

UnixDomainSocketClient.class: UnixDomainSocketClient.java
	javac $(JAVA_FLAGS) UnixDomainSocketClient.java

UnixDomainSocketServer.class: UnixDomainSocketServer.java
	javac $(JAVA_FLAGS) UnixDomainSocketServer.java

UnixDomainSocket.h: UnixDomainSocket$1.class
	javah UnixDomainSocket

libunixdomainsocket.so: UnixDomainSocket.h UnixDomainSocket.c
	$(CC) -shared -fPIC $(INCLUDEPATH) -o libunixdomainsocket.so UnixDomainSocket.c

install:
	cp libunixdomainsocket.so /usr/lib

uninstall:
	rm -f /usr/lib/libunixdomainsocket.so

test: TestUnixDomainSocket.java all
	javac $(JAVA_FLAGS) TestUnixDomainSocket.java

clean:
	rm -f *.class *.so *.o
