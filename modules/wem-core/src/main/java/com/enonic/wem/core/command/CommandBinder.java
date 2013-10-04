package com.enonic.wem.core.command;

import java.lang.reflect.ParameterizedType;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;

import com.enonic.wem.api.command.Command;

public final class CommandBinder
{
    private final MapBinder<Class, CommandHandler> binder;

    private CommandBinder( final Binder binder )
    {
        this.binder = MapBinder.newMapBinder( binder, Class.class, CommandHandler.class );
    }

    public <T extends Command> void add( final Class<? extends CommandHandler<T>> handler )
    {
        final ParameterizedType type = (ParameterizedType) handler.getGenericSuperclass();
        final Class<?> commandType = (Class<?>) type.getActualTypeArguments()[0];
        this.binder.addBinding( commandType ).to( handler ).in( Scopes.SINGLETON );
    }

    public static CommandBinder from( final Binder binder )
    {
        return new CommandBinder( binder );
    }
}
