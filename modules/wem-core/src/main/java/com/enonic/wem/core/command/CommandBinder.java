package com.enonic.wem.core.command;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.api.command.Command;

public final class CommandBinder
{
    private final Multibinder<CommandHandler> binder;

    private CommandBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, CommandHandler.class );
    }

    public <T extends Command> void add( final Class<? extends CommandHandler<T>> handler )
    {
        this.binder.addBinding().to( handler ).in( Scopes.SINGLETON );
    }

    public static CommandBinder from( final Binder binder )
    {
        return new CommandBinder( binder );
    }
}
