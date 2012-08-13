package com.enonic.wem.core.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.exception.SystemException;

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
    public void invoke( final CommandContext context, final Command command )
    {
        try
        {
            final CommandHandler handler = findHandler( command.getClass() );
            handler.handle( context, command );
        }
        catch ( final BaseException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new SystemException( e, "System error in [{0}]", command.getClass().getName() );
        }
    }

    private synchronized CommandHandler findHandler( final Class type )
    {
        final CommandHandler handler = this.handlerMap.get( type );
        if ( handler != null )
        {
            return handler;
        }

        throw new SystemException( "Handle for command [{0}] not found", type.getName() );
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
