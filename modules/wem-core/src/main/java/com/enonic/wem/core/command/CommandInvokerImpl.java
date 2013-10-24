package com.enonic.wem.core.command;

import java.util.Map;

import javax.inject.Inject;

import com.google.inject.Provider;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.exception.SystemException;


public final class CommandInvokerImpl
    implements CommandInvoker
{
    private final Map<Class, Provider<CommandHandler>> handlers;

    @Inject
    public CommandInvokerImpl( final Map<Class, Provider<CommandHandler>> handlers )
    {
        this.handlers = handlers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke( final CommandContext context, final Command command )
    {
        try
        {
            final CommandHandler handler = findHandler( command.getClass() );
            handler.setContext( context );
            handler.setCommand( command );
            handler.handle();
        }
        catch ( final RuntimeException e )
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
        final Provider<CommandHandler> handler = this.handlers.get( type );
        if ( handler != null )
        {
            return handler.get();
        }

        throw new SystemException( "Handle for command [{0}] not found", type.getName() );
    }
}
