package com.enonic.wem.core.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import com.enonic.wem.api.command.Command;

@Component
public final class CommandInvokerImpl
    implements CommandInvoker
{
    private final Map<Class, CommandHandler> handlerMap;

    public CommandInvokerImpl()
    {
        this.handlerMap = Maps.newHashMap();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke( final Command command )
    {
        command.validate();
        final CommandHandler handler = findHandler( command.getClass() );
        handler.handle( command );
    }

    private synchronized CommandHandler findHandler( final Class type )
    {
        final CommandHandler handler = this.handlerMap.get( type );
        if ( handler != null )
        {
            return handler;
        }

        throw new IllegalArgumentException( "Handle for command [" + type.getName() + "] not found" );
    }

    @Autowired
    public void setHandlers( final CommandHandler... handlers )
    {
        for ( final CommandHandler handler : handlers )
        {
            this.handlerMap.put( handler.getType(), handler );
        }
    }
}
