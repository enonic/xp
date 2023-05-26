package com.enonic.xp.script.impl.util;

import java.util.Date;

import javax.script.ScriptEngine;

import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public final class NashornHelper
{
    private static final NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();

    public static ScriptEngine getScriptEngine( final ClassLoader loader )
    {
        return FACTORY.getScriptEngine( new String[]{"--optimistic-types=false", "--global-per-engine", "-strict", "--language=es6"},
                                        loader );
    }

    public static boolean isUndefined( final Object value )
    {
        return value == null || ScriptObjectMirror.isUndefined( value );
    }

    static boolean isNativeArray( final Object value )
    {
        return ( value instanceof JSObject ) && ( (JSObject) value ).isArray();
    }

    static boolean isNativeObject( final Object value )
    {
        return ( value instanceof JSObject ) && !isNativeArray( value );
    }

    static void addToNativeObject( final Object object, final String key, final Object value )
    {
        ( (ScriptObjectMirror) object ).put( key, value );
    }

    static void addToNativeArray( final Object array, final Object value )
    {
        ( (ScriptObjectMirror) array ).callMember( "push", value );
    }

    public static boolean isDateType( final JSObject value )
    {
        return "Date".equalsIgnoreCase( value.getClassName() );
    }

    public static Date toDate( final JSObject value )
    {
        final Number time = (Number) ( (ScriptObjectMirror) value ).callMember( "getTime" );
        return new Date( time.longValue() );
    }
}
