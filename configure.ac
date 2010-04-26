#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.61)
AC_INIT(JUDS, 0.93, klaus.trainer@web.de)
AC_CONFIG_SRCDIR([com/google/code/juds/UnixDomainSocket.c])
AC_CONFIG_HEADER([config.h])

# Checks for programs.
AC_PROG_CC

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

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
