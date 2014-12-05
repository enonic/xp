package com.enonic.wem.script.v2.logger;

import java.util.function.Function;

import org.slf4j.Logger;

import jdk.nashorn.api.scripting.AbstractJSObject;

public final class LoggerScriptObject
    extends AbstractJSObject
{
    private final Logger logger;

    public LoggerScriptObject( final Logger logger )
    {
        this.logger = logger;
        initialize();
    }

    private void initialize()
    {
        setMember( "debug", logFunction( this::logDebug ) );
        setMember( "info", logFunction( this::logInfo ) );
        setMember( "warning", logFunction( this::logWarning ) );
        setMember( "error", logFunction( this::logError ) );
    }

    private Object logFunction( final Function<Object[], Object> func )
    {
        return new AbstractJSObject()
        {
            @Override
            public Object call( final Object thiz, final Object... args )
            {
                return func.apply( args );
            }

            @Override
            public boolean isFunction()
            {
                return true;
            }

            @Override
            public boolean isStrictFunction()
            {
                return true;
            }
        };
    }

    private Object logDebug( final Object[] args )
    {
        return null;
    }

    private Object logInfo( final Object[] args )
    {
        return null;
    }

    private Object logWarning( final Object[] args )
    {
        return null;
    }

    private Object logError( final Object[] args )
    {
        return null;
    }
}
