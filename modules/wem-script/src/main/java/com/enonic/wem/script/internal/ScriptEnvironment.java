package com.enonic.wem.script.internal;

import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.Maps;

import com.enonic.wem.script.ScriptLibrary;
import com.enonic.wem.script.command.Command;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandInvoker;

@Singleton
public final class ScriptEnvironment
    implements CommandInvoker
{
    private final Map<String, ScriptLibrary> libraries;

    private final Map<String, CommandHandler> commandHandlers;

    public ScriptEnvironment()
    {
        this.libraries = Maps.newConcurrentMap();
        this.commandHandlers = Maps.newConcurrentMap();
    }

    public ScriptLibrary getLibrary( final String name )
    {
        return this.libraries.get( name );
    }

    public void addLibrary( final ScriptLibrary library )
    {
        if ( library == null )
        {
            return;
        }

        this.libraries.put( library.getName(), library );
    }

    public void removeLibrary( final ScriptLibrary library )
    {
        if ( library == null )
        {
            return;
        }

        this.libraries.remove( library.getName() );
    }

    public void addHandler( final CommandHandler commandHandler )
    {
        if ( commandHandler == null )
        {
            return;
        }

        this.commandHandlers.put( commandHandler.getType().getName(), commandHandler );
    }

    public void removeHandler( final CommandHandler commandHandler )
    {
        if ( commandHandler == null )
        {
            return;
        }

        this.commandHandlers.remove( commandHandler.getType().getName() );
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
        findHandler( command.getClass().getName() ).invoke( command );
    }
}
