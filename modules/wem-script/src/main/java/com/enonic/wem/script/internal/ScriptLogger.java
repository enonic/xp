package com.enonic.wem.script.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;

import com.enonic.wem.api.resource.ResourceKey;

public final class ScriptLogger
{
    private final Logger log;

    public ScriptLogger( final ResourceKey source )
    {
        this.log = LoggerFactory.getLogger( source.getModule().toString() );
    }

    public void debug( final String message, final Object... args )
    {
        this.log.debug( format( message, args ) );
    }

    public void info( final String message, final Object... args )
    {
        this.log.info( format( message, args ) );
    }

    public void warning( final String message, final Object... args )
    {
        this.log.warn( format( message, args ) );
    }

    public void error( final String message, final Object... args )
    {
        this.log.error( format( message, args ) );
    }

    public String format( final String message, final Object... args )
    {
        final Object[] converted = convertArgs( args );
        return String.format( message, converted );
    }

    private Object[] convertArgs( final Object[] args )
    {
        final Object[] target = new Object[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            target[i] = convertArg( args[i] );
        }

        return target;
    }

    private Object convertArg( final Object arg )
    {
        if ( arg == null )
        {
            return null;
        }

        if ( arg.getClass().getPackage().getName().startsWith( "jdk.nashorn." ) )
        {
            return convertJSObject( arg );
        }

        return arg;
    }

    private Object convertJSObject( final Object arg )
    {
        if ( arg instanceof JSObject )
        {
            return serialize( (JSObject) arg );
        }

        return convertJSObject( ScriptUtils.convert( arg, JSObject.class ) );
    }

    private String serialize( final JSObject arg )
    {
        return new JsObjectSerializer().toJson( arg ).toString();
    }
}
