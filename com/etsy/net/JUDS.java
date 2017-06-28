package com.etsy.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;


public class JUDS {

    /**
     * A constant for the datagram socket type (connectionless).
     */
    public static final int SOCK_DGRAM; 

    /**
     * A constant for the stream oriented stream socket type (connection-based)
     */
    public static final int SOCK_STREAM; 
    public static final int SOCK_SEQPACKET; 

    public static final int SERVER = 0;
    public static final int CLIENT = 1;


    static URL jarURL;
    
    static {
        if(System.getenv("JUDSDIR") != null) {
            try {
                jarURL = new File(System.getenv("JUDSDIR")).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Unable to create URL from path " + System.getenv("JUDSDIR"));
            }
        } else {
            URL url = JUDS.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();

            URL prepared = null;
            if ("jar".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol())) {
                String filePath = url.getPath();
                int end = filePath.indexOf("!/");
                if (end >= 0) {
                    filePath = filePath.substring(0, end);
                    if (filePath.contains("file://") && !filePath.contains("file:////")) {
                        filePath = filePath.replaceFirst("file://", "file:////");
                    }
                    try {
                        prepared = new URL(filePath);
                    } catch (MalformedURLException ex) {
                    }
                }
            }
            if (prepared == null) {
                prepared = url;
            }
            jarURL = prepared;
        }
        
        UnixDomainSocket.staticInit();
        SOCK_DGRAM = UnixDomainSocket.nativeGetSocketType("SOCK_DGRAM");
        SOCK_STREAM = UnixDomainSocket.nativeGetSocketType("SOCK_STREAM");
        SOCK_SEQPACKET = UnixDomainSocket.nativeGetSocketType("SOCK_SEQPACKET");
    }

    private static ClassLoader judsCl = new URLClassLoader(
        new URL[] { jarURL },
        ClassLoader.getSystemClassLoader());

    public static Object safeSocket(int type, String socketFile, int socketType) 
        throws ClassNotFoundException, NoSuchMethodException, 
               InstantiationException, IllegalAccessException, 
               InvocationTargetException {

        String name = (type == SERVER) ? "Server" : "Client";
        Class c = Class.forName("com.etsy.net.UnixDomainSocket"+name, 
                                true, judsCl);
        
        Constructor ctor = c.getConstructor(new Class[] { 
                String.class, int.class });
        return ctor.newInstance(new Object[] { socketFile, socketType });
    }

}
