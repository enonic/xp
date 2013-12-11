package com.enonic.wem.core.module;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;


final class ModuleKeyResolverImpl
    implements ModuleKeyResolver
{
    private final ImmutableMap<ModuleName, ModuleKey> modules;

    ModuleKeyResolverImpl( final Map<ModuleName, ModuleKey> modules )
    {
        this.modules = ImmutableMap.copyOf( modules );
    }

    @Override
    public ModuleKey resolve( final ModuleName moduleName )
    {
        return this.modules.get( moduleName );
    }
}
