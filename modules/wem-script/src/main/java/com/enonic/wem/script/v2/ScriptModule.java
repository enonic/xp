package com.enonic.wem.script.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScriptModule
{
    private final Logger logger;

    public ScriptModule()
    {
        this.logger = LoggerFactory.getLogger( ScriptModule.class );
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public Object command( final String name )
    {
        return null;
    }

    public Object resolve( final String name )
    {
        return null;
    }

    public Object require( final String name )
    {
        return null;
    }

    public Object format( final Object... args )
    {
        return "hello";
    }

    public Object jsonPath( final Object arg )
    {
        return "json";
    }
}
