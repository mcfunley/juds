package com.google.code.juds;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.net.URL;

public class JUDS {

    /**
     * A constant for the datagram socket type (connectionless).
     */
    public static final int SOCK_DGRAM = 0;

    /**
     * A constant for the stream oriented stream socket type (connection-based)
     */
    public static final int SOCK_STREAM = 1;

    
    public static final int SERVER = 0;
    public static final int CLIENT = 1;


    static URL jarURL = JUDS.class
        .getProtectionDomain()
        .getCodeSource()
        .getLocation();

    private static ClassLoader judsCl = new URLClassLoader(
        new URL[] { jarURL },
        ClassLoader.getSystemClassLoader());

    public static Object safeSocket(int type, String socketFile, int socketType) 
        throws ClassNotFoundException, NoSuchMethodException, 
               InstantiationException, IllegalAccessException, 
               InvocationTargetException {

        String name = (type == SERVER) ? "Server" : "Client";
        Class c = judsCl.loadClass("com.google.code.juds.UnixDomainSocket"+name);
        
        Constructor ctor = c.getConstructor(new Class[] { 
                String.class, int.class });
        return ctor.newInstance(new Object[] { socketFile, socketType });
    }

}
