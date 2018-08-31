package com.enonic.xp.script.impl.util;

import java.util.Date;

import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public final class NashornHelper
{
    private final static NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();

    public static ScriptEngine getScriptEngine( final ClassLoader loader )
    {
        return FACTORY.getScriptEngine( new String[]{"--global-per-engine", "-strict"}, loader );
    }

    public static boolean isUndefined( final Object value )
    {
        return value == null || ScriptObjectMirror.isUndefined( value );
    }

    static boolean isNativeArray( final Object value )
    {
        return ( value instanceof ScriptObjectMirror ) && ( (ScriptObjectMirror) value ).isArray();
    }

    static boolean isNativeObject( final Object value )
    {
        return ( value instanceof ScriptObjectMirror ) && !isNativeArray( value );
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
