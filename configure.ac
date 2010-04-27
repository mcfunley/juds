#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.61)
AC_INIT(JUDS, 0.93, klaus.trainer@web.de)
AC_CONFIG_SRCDIR([com/google/code/juds/UnixDomainSocket.c])
AC_CONFIG_HEADER([config.h])

# PROGRAMS
AC_PROG_CC
if test x$JAVA_HOME = x; then 
   AX_CHECK_JAVA_HOME
fi
JAVAC=$JAVA_HOME/bin/javac

if test "`$JAVAC -version 2>&1 | grep '1.6'`" = ""; then
    AC_MSG_ERROR([The java compiler $JAVAC does not seem to be version 1.6 (try setting JAVA_HOME)?])
fi
AC_SUBST(JAVA_HOME)
AC_SUBST(JAVAC)



# Detect the platform
if test `uname` = "Darwin"; then
    PLATFORM=darwin
    LIBSWITCH=-dynamiclib
    M64="-arch x86_64"
    M32="-arch i386"
    LIBEXT=dylib
else
    PLATFORM=linux
    LIBSWITCH=-shared
    M64=-m64
    M32=-m32
    LIBEXT=so
fi
AC_SUBST(PLATFORM)
AC_SUBST(LIBSWITCH)
AC_SUBST(M32)
AC_SUBST(M64)
AC_SUBST(LIBEXT)


# Checks for libraries.

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([stdlib.h strings.h sys/socket.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST
AC_TYPE_SSIZE_T

# Checks for library functions.
AC_CHECK_FUNCS([bzero socket])

AC_SUBST(PACKAGE_VERSION)
AC_SUBST(CFLAGS)

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
