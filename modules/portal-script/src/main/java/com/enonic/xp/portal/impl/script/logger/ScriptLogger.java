package com.enonic.xp.portal.impl.script.logger;

import javax.script.Bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.resource.ResourceKey;

public final class ScriptLogger
{
    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getModule().toString() );
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
        final String prefix = "(" + this.source.getPath() + ") ";
        if ( args.length == 0 )
        {
            return prefix + message;
        }
        else
        {
            return prefix + String.format( message, args );
        }
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "log", this );
    }
}
