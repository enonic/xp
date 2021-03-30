package com.enonic.xp.script.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.resource.ResourceKey;

public final class ScriptLogger
{
    private static final String NASHORN_EXCEPTION_MEMBER = "nashornException";

    private final LogArgConverter converter;

    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source, final ObjectConverter converter )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getApplicationKey().toString() );
        this.converter = new LogArgConverter( converter );
    }

    public void debug( final String message, final Object arg )
    {
        if ( arg instanceof Throwable )
        {
            this.log.debug( message, (Throwable) arg );
        }
        else if ( arg instanceof JSObject && ( (JSObject) arg ).hasMember( NASHORN_EXCEPTION_MEMBER ) )
        {
            this.log.debug( message, (Exception) ( (JSObject) arg ).getMember( NASHORN_EXCEPTION_MEMBER ) );
        }
        else
        {
            this.log.debug( format( message, arg ) );
        }
    }

    public void debug( final String message, final Object... args )
    {
        this.log.debug( format( message, args ) );
    }

    public void info( final String message, final Object arg )
    {
        if ( arg instanceof Throwable )
        {
            this.log.info( message, (Throwable) arg );
        }
        else if ( arg instanceof JSObject && ( (JSObject) arg ).hasMember( NASHORN_EXCEPTION_MEMBER ) )
        {
            this.log.info( message, (Exception) ( (JSObject) arg ).getMember( NASHORN_EXCEPTION_MEMBER ) );
        }
        else
        {
            this.log.info( format( message, arg ) );
        }
    }

    public void info( final String message, final Object... args )
    {
        this.log.info( format( message, args ) );
    }

    public void warning( final String message, final Object arg )
    {
        if ( arg instanceof Throwable )
        {
            this.log.warn( message, (Throwable) arg );
        }
        else if ( arg instanceof JSObject && ( (JSObject) arg ).hasMember( NASHORN_EXCEPTION_MEMBER ) )
        {
            this.log.warn( message, (Exception) ( (JSObject) arg ).getMember( NASHORN_EXCEPTION_MEMBER ) );
        }
        else
        {
            this.log.warn( format( message, arg ) );
        }
    }

    public void warning( final String message, final Object... args )
    {
        this.log.warn( format( message, args ) );
    }

    public void error( final String message, final Object arg )
    {
        if ( arg instanceof Throwable )
        {
            this.log.error( message, (Throwable) arg );
        }
        else if ( arg instanceof JSObject && ( (JSObject) arg ).hasMember( NASHORN_EXCEPTION_MEMBER ) )
        {
            this.log.error( message, (Exception) ( (JSObject) arg ).getMember( NASHORN_EXCEPTION_MEMBER ) );
        }
        else
        {
            this.log.error( format( message, arg ) );
        }
    }

    public void error( final String message, final Object... args )
    {
        this.log.error( format( message, args ) );
    }

    public String format( final String message, final Object arg )
    {
        return format( message, new Object[]{arg} );
    }

    public String format( final String message, final Object... args )
    {
        final String prefix = "(" + this.source.getPath() + ") ";
        if ( args.length == 0 )
        {
            return prefix + message;
        }
        else
        {
            final Object[] converted = this.converter.convertArgs( args );
            return prefix + String.format( message, converted );
        }
    }
}
