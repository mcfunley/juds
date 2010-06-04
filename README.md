JUDS
====


ABSTRACT
--------

Java Unix Domain Sockets (JUDS) provide classes to address the need in Java
for accessing Unix domain sockets. The source is provided for a shared library
containing the compiled native C code, which is called into by the Java
UnixDomainSocket classes via JNI (Java Native Interface) to open, close, unlink
(delete), read, and write to Unix domain sockets.


DESCRIPTION
-----------

JUDS is similar with and inspired by J-BUDS. However, JUDS has been written to
have more performance than J-BUDS while supplying all features Unix domain
sockets have.
JUDS consists of an abstract class UnixDomainSocket and two derived classes,
UnixDomainSocketClient and UnixDomainSocketServer. It can simply be used by
instantiating one of the two classes. Through the getInputStream and
getOutputStream methods, the resulting instance can be used to get an
InputStream and/or OutputStream object.

From version 0.93 on, UnixDomainSocketServer can be used with accept() to
obtain undifferentiated UnixDomainSocket, this allowing to handle multiple
clients from the same Server. New API is then used in that case (the previous
one remains supported).

*Limitations*:

Datagram sockets are unidirectional, i.e. trying to get an OutputStream for an
UnixDomainSocketServer object results in an UnsupportedOperationException being
thrown. Accordingly trying to get an InputStream for an UnixDomainSocketClient
also results in such an exception being thrown.
With the API before version 0.92, stream sockets can only handle connections
between two end points. Trying to connect to an UnixDomainSocketServer which
has already accepted a connection with another client will result in an error.
With New API (version 0.93 or higher) UnixDomainServerSocket.accept() returns
an independent socket for each new incoming connection.

VERSION
=======

Version 0.94 - 2010-04-30


VERSION HISTORY
===============

Version 0.94 - 2010-04-30
    - Now builds on OSX.
    - Build system switched to autoconf.
    - Native libraries are now embedded in the jar. 
    - Support for universal jars.
    - Mainline repository moved to: http://github.com/mcfunley/juds.
    - Refactored native library.
    - Fixed errant bzero of random memory location.
    - Minor bug fixes.

Version 0.93 - 2010-01-08
    - Extend API with accept(): allow server socket to handle multiple clients.
    - Simplified API still supported but incompatible with accept().
    - Test class for new API (can be use as a sample).
    - Fix for stream closure detection.

Version 0.92 - 2009-11-05
    - Removed the umask() call in UnixDomainSocket.c. As some nice people have
      told me, changing the umask is evil ;-)

Version 0.9 - 2008-02-07
    - Fixed a bug in the nativeCreate() function in UnixDomainSocket.c which
      was the reason for UnixDomainSocketServer to not work correctly.

Version 0.8 - 2008-12-06
    - Fixed UnixDomainSocket.UnixDomainSocketInputStream.read() method in order
      to return correct results (Thanks to Roman Kosenko!).
    - Rearranged test, made it more comprehensible. Now the test can be
      launched by running the make target 'test'. There isn't any additional
      operation from the user required anymore (Thanks to Roman Kosenko!).
    - Introduced package structure (com.google.code.juds), improved Makefile.
    - Added unlink(socketFile) calls in UnixDomainSocket.c for the case of
      failing listen() or accept() calls to remove the unused socket file
      (Thanks Alan Harder!).

Version 0.7 - 2008-07-16
    - Made changes to the makefile. This fixes some issues for building under
      non-Linux platforms.
    - Added a note how to fix an issue under cygwin to the documentation.
    - Restructured the documentation.

Version 0.6 - 2008-06-03
    - Made some minor refactoring and orthographical corrections.
      No difference in functionality to version 0.5.

Version 0.5 - 2008-03-05
    - Initial version


INSTALLATION
============

The java UnixDomainSocket classes use native methods in order to access AF_UNIX
sockets for interprocess communication. JUDS can be installed by using the
included makefile.
IMPORTANT NOTE:  In order for the compilation process to work, it is likely
that you need to change one or more of the top four variables in the makefile.
By example please select you right jdk by edeting Makefile JAVA_HOME .

A installation process could look like this:

	make
	make install

make install does nothing more than copying the shared library
libunixdomainsocket.so to the /usr/lib directory. Alternatively you can also
set the variable LD_LIBRARY_PATH to include the directory where
libunixdomainsocket.so is located.


TESTING
=======

To test the installation, simply run the make target 'test':
	make test


TROUBLESHOOTING
===============

There might be issues with gcc under cygwin because the type __int64 used in
jni.h is not defined. Here are some proposals how that can be fixed:
http://maba.wordpress.com/2004/07/28/generating-dll-files-for-jni-under-windowscygwingcc/
(see also the comments)

Please send bug reports and comments to klaus.trainer@web.de.


LICENSE
=======

This library is free software; you can redistribute it and/or modify it under
the terms of version 2.1 of the GNU Lesser General Public License as published
by the Free Software Foundation.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Lesser General Public License for more details:

http://www.opensource.org/licenses/lgpl-license.html
http://www.gnu.org/copyleft/lesser.html

To obtain a written copy of the GNU Lesser General Public License,
please write to the Free Software Foundation, Inc., 59 Temple Place, 
Suite 330, Boston, MA  02111-1307 USA


CONTRIBUTORS
============
Klaus Trainer - original author.

Philippe Lhardy

Mathias Herberts

Richard Moats

Dan McKinley - current maintainer. 


ACKNOWLEDGMENTS
===============

JUDS has been inspired by Robert Morgan's J-BUDS (Java Based Unix Domain
Sockets) which is licensed under the GNU Lesser General Public License
version 2.1 as well.
