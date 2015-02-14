package com.enonic.xp.portal.impl.script.logger;

import javax.script.Bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.resource.ResourceKey;

public final class ScriptLogger
{
    private final static LogArgConverter ARG_CONVERTER = new LogArgConverter();

    private final static String FORMAT_STR = "({}) {}";

    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getModule().toString() );
    }

    public void debug( final String message, final Object... args )
    {
        this.log.debug( FORMAT_STR, this.source.getPath(), format( message, args ) );
    }

    public void info( final String message, final Object... args )
    {
        this.log.info( FORMAT_STR, this.source.getPath(), format( message, args ) );
    }

    public void warning( final String message, final Object... args )
    {
        this.log.warn( FORMAT_STR, this.source.getPath(), format( message, args ) );
    }

    public void error( final String message, final Object... args )
    {
        this.log.error( FORMAT_STR, this.source.getPath(), format( message, args ) );
    }

    public String format( final String message, final Object... args )
    {
        final Object[] converted = ARG_CONVERTER.convertArgs( args );
        return String.format( message, converted );
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "log", this );
    }
}
