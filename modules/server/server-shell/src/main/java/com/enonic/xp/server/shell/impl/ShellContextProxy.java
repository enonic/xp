package com.enonic.xp.server.shell.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;

final class ShellContextProxy
    implements InvocationHandler
{
    private final static String GET_PROPERTY_NAME = "getProperty";

    private final BundleContext delegate;

    private final Map<String, String> properties;

    public ShellContextProxy( final BundleContext delegate )
    {
        this.delegate = delegate;
        this.properties = new HashMap<>();
    }

    @Override
    public Object invoke( final Object proxy, final Method method, final Object[] args )
        throws Throwable
    {
        if ( method.getName().equals( GET_PROPERTY_NAME ) )
        {
            return getProperty( (String) args[0] );
        }

        return method.invoke( this.delegate, args );
    }

    private String getProperty( final String key )
    {
        final String value = this.properties.get( key );
        if ( value != null )
        {
            return value;
        }

        return this.delegate.getProperty( key );
    }

    public void property( final String key, final String value )
    {
        this.properties.put( key, value );
    }

    public BundleContext build()
    {
        return (BundleContext) Proxy.newProxyInstance( getClass().getClassLoader(), new Class[]{BundleContext.class}, this );
    }
}
