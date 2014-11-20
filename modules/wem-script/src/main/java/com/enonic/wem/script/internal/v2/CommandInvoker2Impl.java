package com.enonic.wem.script.internal.v2;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.script.command.CommandHandler2;
import com.enonic.wem.script.command.CommandInvoker2;
import com.enonic.wem.script.command.CommandRequest;

public final class CommandInvoker2Impl
    implements CommandInvoker2
{
    private final Map<String, CommandHandler2> handlers;

    public CommandInvoker2Impl()
    {
        this.handlers = Maps.newConcurrentMap();
    }

    public void register( final CommandHandler2 handler )
    {
        this.handlers.put( handler.getName(), handler );
    }

    public void unregister( final CommandHandler2 handler )
    {
        this.handlers.remove( handler.getName() );
    }

    @Override
    public Object invoke( final CommandRequest req )
    {
        final String name = req.getName();
        final CommandHandler2 handler = this.handlers.get( name );
        if ( handler != null )
        {
            return invoke( handler, req );
        }

        throw new IllegalArgumentException( String.format( "Command [%s] not found", name ) );
    }

    private Object invoke( final CommandHandler2 handler, final CommandRequest req )
    {
        return handler.execute( req );
    }
}
