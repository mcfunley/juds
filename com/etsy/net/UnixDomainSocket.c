/***********************************************/
/* UnixDomainSocket.c                          */
/* Inspired by J-BUDS version 1.0              */
/* See COPYRIGHT file for license details      */
/***********************************************/
#include "UnixDomainSocket.h"

#include <jni.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <errno.h>

#define ASSERTNOERR(cond, msg) do { \
    if (cond) { fprintf(stderr, "[%d] ", errno); perror(msg); return -1; }} while(0)

/* In the class UnixDomainSocket SOCK_DGRAM and SOCK_STREAM correspond to the
 * constant values 0 and 1; SOCK_TYPE replaces them with the respective macro */
#define SOCK_TYPE(type) ((type) == 0 ? SOCK_DGRAM : SOCK_STREAM)


#ifndef SUN_LEN
#define SUN_LEN(su) \
        (sizeof(*(su)) - sizeof((su)->sun_path) + strlen((su)->sun_path))
#endif



socklen_t sockaddr_init(const char* socketFile, struct sockaddr_un* sa) {
    socklen_t salen;

    bzero(sa, sizeof(struct sockaddr_un));
    sa->sun_family = AF_UNIX;
    strcpy(sa->sun_path, socketFile);

    salen = SUN_LEN(sa);
    return salen;
}


JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeCreate(JNIEnv * jEnv,
                            jclass jClass,
                            jstring jSocketFile,
                            jint jSocketType)
{
    int s;            /* socket file handle */
    struct sockaddr_un sa;
    const char *socketFile =
        (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);

    socklen_t salen = sockaddr_init(socketFile, &sa);

    /* create the socket */
    s = socket(PF_UNIX, SOCK_TYPE(jSocketType), 0);
    ASSERTNOERR(s == -1, "nativeCreate: socket");

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

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeListen(JNIEnv * jEnv,
                            jclass jClass,
                            jstring jSocketFile,
                            jint jSocketType,
                            jint jBacklog)
{
    int s;            /* socket file handle */
    struct sockaddr_un sa;
    const char *socketFile =
        (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);
    socklen_t salen = sockaddr_init(socketFile, &sa);

    /* create the socket */
    s = socket(PF_UNIX, SOCK_TYPE(jSocketType), 0);
    ASSERTNOERR(s == -1, "nativeListen: socket");

    /* bind to the socket; here the socket file is created */
    ASSERTNOERR(bind(s, (struct sockaddr *)&sa, salen) == -1,
            "nativeListen: bind");
    if (SOCK_TYPE(jSocketType) == SOCK_STREAM) {
        ASSERTNOERR(listen(s, jBacklog) == -1, "nativeListen: listen");
    }

    (*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);

    /* return the listening socket file handle */
    return s;
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeAccept(JNIEnv * jEnv,
                            jclass jClass,
                            jint jSocketFileHandle,
                            jint jSocketType)
{
    int s = -1;            /* socket file handle */

    ASSERTNOERR(jSocketFileHandle == -1, "nativeAccept: socket");
    if (SOCK_TYPE(jSocketType) == SOCK_STREAM) {
        s = accept(jSocketFileHandle, NULL, 0);
        ASSERTNOERR(s == -1, "nativeAccept: accept");
    }

    /* return the socket file handle */
    return s;
}


JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeOpen(JNIEnv * jEnv,
                              jclass jClass,
                              jstring jSocketFile,
                              jint jSocketType)
{
    int s;            /* socket file handle */
    struct sockaddr_un sa;
    const char *socketFile =
        (*jEnv)->GetStringUTFChars(jEnv, jSocketFile, NULL);
    socklen_t salen = sockaddr_init(socketFile, &sa);

    s = socket(PF_UNIX, SOCK_TYPE(jSocketType), 0);
    ASSERTNOERR(s == -1, "nativeOpen: socket");
    ASSERTNOERR(connect(s, (struct sockaddr *)&sa, salen) == -1,
            "nativeOpen: connect");

    (*jEnv)->ReleaseStringUTFChars(jEnv, jSocketFile, socketFile);

    /* return the socket file handle */
    return s;
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeRead(JNIEnv * jEnv,
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

    // end of stream ( 0 in 'C' API should be -1 in java.io.InputStream API )
    if ( count == 0 )
      {
        count = -1;
      }
    /* return the number of bytes read */
    return count;
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeWrite(JNIEnv * jEnv,
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

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeClose(JNIEnv * jEnv,
                               jclass jClass,
                               jint jSocketFileHandle)
{
    return close(jSocketFileHandle);
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeCloseInput(JNIEnv * jEnv,
                                jclass jClass,
                                jint
                                jSocketFileHandle)
{
    /* close the socket input stream */
    return shutdown(jSocketFileHandle, SHUT_RD);
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeCloseOutput(JNIEnv * jEnv,
                                 jclass jClass,
                                 jint
                                 jSocketFileHandle)
{
    /* close the socket output stream */
    return shutdown(jSocketFileHandle, SHUT_WR);
}

JNIEXPORT jint JNICALL
Java_com_google_code_juds_UnixDomainSocket_nativeUnlink(JNIEnv * jEnv,
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
