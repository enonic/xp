package com.enonic.wem.script.internal;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.script.command.Command;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandInvoker;
import com.enonic.wem.script.command.CommandName;

public final class ScriptEnvironment
    implements CommandInvoker
{
    private final Map<String, CommandHandler> commandHandlers;

    public ScriptEnvironment()
    {
        this.commandHandlers = Maps.newConcurrentMap();
    }

    private String getName( final Command command )
    {
        return getName( command.getClass() );
    }

    private String getName( final CommandHandler handler )
    {
        return getName( handler.getType() );
    }

    private String getName( final Class<?> type )
    {
        final CommandName name = type.getAnnotation( CommandName.class );
        if ( name != null )
        {
            return name.value();
        }

        return type.getName();
    }

    public void addHandler( final CommandHandler handler )
    {
        System.out.println("Add handler " + handler);
        if ( handler == null )
        {
            return;
        }

        final String name = getName( handler );
        this.commandHandlers.put( name, handler );
    }

    public void removeHandler( final CommandHandler handler )
    {
        System.out.println("Remove handler " + handler);
        if ( handler == null )
        {
            return;
        }

        final String name = getName( handler );
        this.commandHandlers.remove( name );
    }

    private CommandHandler findHandler( final String name )
    {
        final CommandHandler handler = this.commandHandlers.get( name );
        if ( handler != null )
        {
            return handler;
        }

        throw new IllegalArgumentException( "Command [" + name + "] not found" );
    }

    @Override
    public Command newCommand( final String name )
    {
        return findHandler( name ).newCommand();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invokeCommand( final Command command )
    {
        findHandler( getName( command ) ).invoke( command );
    }
}
