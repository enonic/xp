package com.enonic.wem.admin.status;

import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class NashornStatusInfoBuilder
    extends StatusInfoBuilder
{
    public NashornStatusInfoBuilder()
    {
        super( "nashorn" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final Class contextClz = loadClass( "jdk.nashorn.internal.runtime.Context" );
        json.put( "contextClass", contextClz != null ? contextClz.toString() : null );

        if ( contextClz != null )
        {
            final Method method1 = findMethod( contextClz, "setGlobal" );
            json.put( "contextClassMethod1", method1 != null ? method1.toString() : null );

            final Method method2 = findMethod( contextClz, "getGlobal" );
            json.put( "contextClassMethod2", method2 != null ? method2.toString() : null );
        }

        final Class globalClz = loadClass( "jdk.nashorn.internal.objects.Global" );
        json.put( "globalClass", globalClz != null ? globalClz.toString() : null );

        if ( contextClz != null )
        {
            final Method method1 = findMethod( globalClz, "newEmptyInstance" );
            json.put( "globalClassMethod1", method1 != null ? method1.toString() : null );

            final Method method2 = findMethod( globalClz, "allocate" );
            json.put( "globalClassMethod2", method2 != null ? method2.toString() : null );
        }
    }

    private Class loadClass( final String clzName )
    {
        try
        {
            return Class.forName( clzName );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private Method findMethod( final Class clz, final String name )
    {
        for ( final Method method : clz.getMethods() )
        {
            if ( method.getName().equals( name ) )
            {
                return method;
            }
        }

        return null;
    }
}
