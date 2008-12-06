SHELL = /bin/sh
VERSION = 0.8
PACKAGE = com.google.code.juds
PACKAGE_DIR = com/google/code/juds
TEST_SOCKET_FILE = JUDS_TEST_SOCKET_FILE
CC = gcc
PLAT = linux
JAVA_HOME = /usr/lib/jvm/java-6-sun
INCLUDEPATH = -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/$(PLAT)
PREFIX = /usr
CFLAGS = -O2 -Wall
JAVA_FLAGS = -g:none -deprecation -target 1.6


all: jar libunixdomainsocket.so

jar: juds-$(VERSION).jar

juds-$(VERSION).jar: $(PACKAGE_DIR)/UnixDomainSocket.class $(PACKAGE_DIR)/UnixDomainSocketClient.class $(PACKAGE_DIR)/UnixDomainSocketServer.class
	$(JAVA_HOME)/bin/jar cf $@ $(PACKAGE_DIR)/*.class

libunixdomainsocket.so: $(PACKAGE_DIR)/UnixDomainSocket.c $(PACKAGE_DIR)/UnixDomainSocket.h
	$(CC) $(CFLAGS) -shared -fPIC $(INCLUDEPATH) -o $@ $< 

$(PACKAGE_DIR)/UnixDomainSocket.h: $(PACKAGE).UnixDomainSocket
	$(JAVA_HOME)/bin/javah -o $@ $<

$(PACKAGE).UnixDomainSocket: $(PACKAGE_DIR)/UnixDomainSocket.class

$(PACKAGE_DIR)/UnixDomainSocket.class: $(PACKAGE_DIR)/UnixDomainSocket.java
	$(JAVA_HOME)/bin/javac $(JAVA_FLAGS) $?

$(PACKAGE_DIR)/UnixDomainSocketClient.class: $(PACKAGE_DIR)/UnixDomainSocketClient.java
	$(JAVA_HOME)/bin/javac $(JAVA_FLAGS) $?

$(PACKAGE_DIR)/UnixDomainSocketServer.class: $(PACKAGE_DIR)/UnixDomainSocketServer.java
	$(JAVA_HOME)/bin/javac $(JAVA_FLAGS) $?

install: libunixdomainsocket.so
	cp libunixdomainsocket.so $(PREFIX)/lib

uninstall:
	rm -f $(PREFIX)/lib/libunixdomainsocket.so

test: $(PACKAGE_DIR)/test/TestUnixDomainSocket.class
	python $(PACKAGE_DIR)/test/TestUnixDomainSocket.py $(TEST_SOCKET_FILE) &
	@sleep 2
	java $(PACKAGE).test.TestUnixDomainSocket $(TEST_SOCKET_FILE)
	rm -f $(TEST_SOCKET_FILE)

$(PACKAGE_DIR)/test/TestUnixDomainSocket.class: $(PACKAGE_DIR)/test/TestUnixDomainSocket.java jar
	$(JAVA_HOME)/bin/javac -cp juds-$(VERSION).jar $(JAVA_FLAGS) $<

clean:
	rm -f $(PACKAGE_DIR)/*.class $(PACKAGE_DIR)/test/*.class $(PACKAGE_DIR)/*.h *.so *.jar $(TEST_SOCKET_FILE)
