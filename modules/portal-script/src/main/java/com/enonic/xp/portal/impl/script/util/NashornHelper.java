package com.enonic.xp.portal.impl.script.util;

import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.Undefined;

public final class NashornHelper
{
    private final static NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();

    public static ScriptEngine getScriptEngine( final String... args )
    {
        return FACTORY.getScriptEngine( args );
    }

    public static ScriptEngine getScriptEngine( final ClassLoader loader, final String... args )
    {
        return FACTORY.getScriptEngine( args, loader );
    }

    public static Object newNativeObject()
    {
        return Global.newEmptyInstance();
    }

    public static Object newNativeArray()
    {
        return Global.allocate( new Object[0] );
    }

    public static Object getUndefined()
    {
        return Undefined.getUndefined();
    }

    public static boolean isUndefined( final Object value )
    {
        return ( value instanceof Undefined );
    }

    public static boolean isNativeArray( final Object value )
    {
        return ( value instanceof ScriptObject ) && ( (ScriptObject) value ).isArray();
    }

    public static boolean isNativeObject( final Object value )
    {
        return ( value instanceof ScriptObject ) && !isNativeArray( value );
    }

    public static void addToNativeObject( final Object object, final String key, final Object value )
    {
        ( (ScriptObject) object ).put( key, value, false );
    }

    public static void addToNativeArray( final Object array, final Object value )
    {
        NativeArray.push( array, value );
    }

    public static Object wrap( final Object source )
    {
        if ( source instanceof ScriptObject )
        {
            return ScriptUtils.wrap( (ScriptObject) source );
        }

        return source;
    }
}
