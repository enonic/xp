package com.enonic.wem.core.module;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.module.ModuleService;

public final class ModuleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ModuleService.class ).to( ModuleServiceImpl.class ).in( Singleton.class );
        bind( ModuleKeyResolverService.class ).to( ModuleKeyResolverServiceImpl.class );
        bind( ModuleLoader.class ).asEagerSingleton();
    }
}
