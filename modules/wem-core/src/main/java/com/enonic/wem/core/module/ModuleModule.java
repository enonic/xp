package com.enonic.wem.core.module;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.core.command.CommandBinder;

public final class ModuleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ModuleService.class ).to( ModuleServiceImpl.class ).in( Singleton.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateModuleHandler.class );
        commands.add( GetModuleHandler.class );
        commands.add( GetModulesHandler.class );
        commands.add( GetModuleResourceHandler.class );
        commands.add( CreateModuleResourceHandler.class );
        commands.add( UpdateModuleHandler.class );
    }
}
