package com.enonic.wem.core.module;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public final class ModuleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateModuleHandler.class );
        commands.add( GetModuleHandler.class );
        commands.add( GetModuleResourceHandler.class );
        commands.add( DeleteModuleHandler.class );
        commands.add( UpdateModuleHandler.class );
    }
}
