package com.enonic.xp.script.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.resource.ResourceKey;

public final class ScriptLogger
{
    private final LogArgConverter converter;

    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source, final JavascriptHelper helper )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getApplicationKey().toString() );
        this.converter = new LogArgConverter( helper );
    }

    public void debug( final String message, final Object arg )
    {
        this.log.debug( format( message, arg ) );
    }

    public void debug( final String message, final Object... args )
    {
        this.log.debug( format( message, args ) );
    }

    public void info( final String message, final Object arg )
    {
        this.log.info( format( message, arg ) );
    }

    public void info( final String message, final Object... args )
    {
        this.log.info( format( message, args ) );
    }

    public void warning( final String message, final Object arg )
    {
        this.log.warn( format( message, arg ) );
    }

    public void warning( final String message, final Object... args )
    {
        this.log.warn( format( message, args ) );
    }

    public void error( final String message, final Object arg )
    {
        this.log.error( format( message, arg ) );
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
