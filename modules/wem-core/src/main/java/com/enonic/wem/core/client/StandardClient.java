package com.enonic.wem.core.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandContextFactory;
import com.enonic.wem.core.command.CommandInvoker;

@Component
public final class StandardClient
    implements Client
{
    private CommandInvoker invoker;

    private CommandContextFactory commandContextFactory;

    @Override
    public <R, C extends Command<R>> R execute( final C command )
    {
        command.validate();
        doInvoke( command );
        return command.getResult();
    }

    private void doInvoke( final Command command )
    {
        final CommandContext context = createContext();

        try
        {
            this.invoker.invoke( context, command );
        }
        finally
        {
            if ( context != null )
            {
                context.dispose();
            }
        }
    }

    @Autowired
    public void setInvoker( final CommandInvoker invoker )
    {
        this.invoker = invoker;
    }

    @Autowired
    public void setCommandContextFactory( final CommandContextFactory commandContextFactory )
    {
        this.commandContextFactory = commandContextFactory;
    }

    private CommandContext createContext()
    {
        if ( this.commandContextFactory != null )
        {
            final CommandContext context = this.commandContextFactory.create();
            context.setClient( this );
            return context;
        }

        return null;
    }
}
