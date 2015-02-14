package com.enonic.xp.script.impl.invoker;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.script.impl.bean.JsObjectConverter;

public final class CommandInvokerImpl
    implements CommandInvoker
{
    private final Map<String, CommandHandler> handlers;

    public CommandInvokerImpl()
    {
        this.handlers = Maps.newConcurrentMap();
    }

    public void register( final CommandHandler handler )
    {
        this.handlers.put( handler.getName(), handler );
    }

    public void unregister( final CommandHandler handler )
    {
        this.handlers.remove( handler.getName() );
    }

    @Override
    public Object invoke( final CommandRequest req )
    {
        final String name = req.getName();
        final CommandHandler handler = this.handlers.get( name );
        if ( handler != null )
        {
            return invoke( handler, req );
        }

        throw new IllegalArgumentException( String.format( "Command [%s] not found", name ) );
    }

    private Object invoke( final CommandHandler handler, final CommandRequest req )
    {
        final Object result = handler.execute( req );
        return JsObjectConverter.toJs( result );
    }
}
