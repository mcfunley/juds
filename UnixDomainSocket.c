/***********************************************/
/* UnixDomainSocket.c                          */
/* Inspired by J-BUDS version 1.0              */
/* See COPYRIGHT file for license details      */
/***********************************************/
#include "UnixDomainSocket.h"

#include <jni.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>

#define ASSERTNOERR(cond, msg) do { \
	if (cond) { perror(msg); return -1; }} while(0)

/* In the class UnixDomainSocket SOCK_DGRAM and SOCK_STREAM correspond to the
 * constant values 0 and 1; SOCK_TYPE replaces them with the respective macro */
#define SOCK_TYPE(type) ((type) == 0 ? SOCK_DGRAM : SOCK_STREAM)

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeCreate(JNIEnv * jEnv,
							  jclass jClass,
							  jstring jSocketFile,
							  jint jSocketType)
{
	int s;			/* socket file handle */
	struct sockaddr_un sa;
	socklen_t salen;
	const char *socketFile =
	    (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);

	/* create the socket */
	s = socket(PF_UNIX, SOCK_TYPE(jSocketType), 0);
	ASSERTNOERR(s == -1, "nativeCreate: socket");
	sa.sun_family = AF_UNIX;
	strcpy(sa.sun_path, socketFile);
	salen = strlen(sa.sun_path) + sizeof(sa.sun_family);
	umask(0111);		/* read and write access for everybody */
	/* bind to the socket; here the socket file is created */
	ASSERTNOERR(bind(s, (struct sockaddr *)&sa, salen) == -1,
		    "nativeCreate: bind");
	if (SOCK_TYPE(jSocketType) == SOCK_STREAM) {
		ASSERTNOERR(listen(s, 0) == -1, "nativeCreate: listen");
		s = accept(s, (struct sockaddr *)&sa, &salen);
		ASSERTNOERR(s == -1, "nativeCreate: accept");
	}

	(*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);

	/* return the socket file handle */
	return s;
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeOpen(JNIEnv * jEnv,
							jclass jClass,
							jstring jSocketFile,
							jint jSocketType)
{
	int s;			/* socket file handle */
	struct sockaddr_un sa;
	socklen_t salen;
	const char *socketFile =
	    (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);

	s = socket(PF_UNIX, SOCK_TYPE(jSocketType), 0);
	ASSERTNOERR(s == -1, "nativeOpen: socket");
	sa.sun_family = AF_UNIX;
	strcpy(sa.sun_path, socketFile);
	salen = strlen(sa.sun_path) + sizeof(sa.sun_family);
	ASSERTNOERR(connect(s, (struct sockaddr *)&sa, salen) == -1,
		    "nativeOpen: connect");

	(*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);

	/* return the socket file handle */
	return s;
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeRead(JNIEnv * jEnv,
							jclass jClass,
							jint jSocketFileHandle,
							jbyteArray jbarr,
							jint off, jint len)
{
	ssize_t count;
	jbyte *cbarr = (*jEnv)->GetByteArrayElements(jEnv, jbarr, NULL);
	ASSERTNOERR(cbarr == NULL, "nativeRead: GetByteArrayElements");

	/* read up to len bytes from the socket into the buffer */
	count = read(jSocketFileHandle, &cbarr[off], len);
	ASSERTNOERR(count == -1, "nativeRead: read");

	(*jEnv)->ReleaseByteArrayElements(jEnv, jbarr, cbarr, 0);

	/* return the number of bytes read */
	return count;
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeWrite(JNIEnv * jEnv,
							 jclass jClass,
							 jint jSocketFileHandle,
							 jbyteArray jbarr,
							 jint off, jint len)
{
	ssize_t count;
	jbyte *cbarr = (*jEnv)->GetByteArrayElements(jEnv, jbarr, NULL);
	ASSERTNOERR(cbarr == NULL, "nativeWrite: GetByteArrayElements");

	/* try to write len bytes from the buffer to the socket */
	count = write(jSocketFileHandle, &cbarr[off], len);
	ASSERTNOERR(count == -1, "nativeWrite: write");

	(*jEnv)->ReleaseByteArrayElements(jEnv, jbarr, cbarr, JNI_ABORT);

	/* return the number of bytes written */
	return count;
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeClose(JNIEnv * jEnv,
							 jclass jClass,
							 jint jSocketFileHandle)
{
	return close(jSocketFileHandle);
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeCloseInput(JNIEnv *
							      jEnv,
							      jclass
							      jClass,
							      jint
							      jSocketFileHandle)
{
	/* close the socket input stream */
	return shutdown(jSocketFileHandle, SHUT_RD);
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeCloseOutput(JNIEnv *
							       jEnv,
							       jclass
							       jClass,
							       jint
							       jSocketFileHandle)
{
	/* close the socket output stream */
	return shutdown(jSocketFileHandle, SHUT_WR);
}

JNIEXPORT jint JNICALL Java_UnixDomainSocket_nativeUnlink(JNIEnv * jEnv,
							  jclass jClass,
							  jstring jSocketFile)
{
	int ret;
	const char *socketFile =
	    (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);

	/* unlink socket file */
	ret = unlink(socketFile);

	(*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);

	return ret;
}
