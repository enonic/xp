package com.enonic.wem.core.module;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.module.source.SourceResolver;
import com.enonic.wem.core.module.source.SourceResolverImpl;

public final class ModuleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ModuleService.class ).to( ModuleServiceImpl.class ).in( Singleton.class );
        bind( SourceResolver.class ).to( SourceResolverImpl.class ).in( Singleton.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( GetModuleResourceHandler.class );
    }
}
