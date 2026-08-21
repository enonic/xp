package com.enonic.xp.script.impl.util;

import java.util.Date;

import javax.script.ScriptEngine;

import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public final class NashornHelper
{
    private static final String PROPMAP_QUEUE_THRESHOLD = "nashorn.propmap.queue.threshold";

    static
    {
        // Nashorn switches an object's property map to a delayed-hashing "element queue" once the object
        // has more than nashorn.propmap.queue.threshold (500) properties, and from then on only rehashes
        // every 512 insertions. That optimization is broken: after the queue has been merged back into the
        // hash bins, PropertyHashMap.cloneBins() returns the bins array of the shared ancestor property map
        // instead of a copy, and the builder of the next derived map mutates it in place. Objects that share
        // a property map - which is every object built with the same keys in the same order, like the ones
        // i18n.getPhrases() returns - then see each other's properties, lose their own, or make JSON.parse
        // fail with an IndexOutOfBoundsException. Raising the threshold keeps insertions on the correct
        // clone-per-insert path (and is not slower for objects of this size). See enonic/xp#6882.
        // Must be set before Nashorn's PropertyHashMap is initialized, hence before FACTORY below.
        if ( System.getProperty( PROPMAP_QUEUE_THRESHOLD ) == null )
        {
            System.setProperty( PROPMAP_QUEUE_THRESHOLD, String.valueOf( Integer.MAX_VALUE ) );
        }
    }

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
