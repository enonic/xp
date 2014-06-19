package com.enonic.wem.core.module;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;

public final class ModuleServiceImpl
    implements ModuleService
{
    private ConcurrentMap<ModuleKey, Module> modules;

    public ModuleServiceImpl()
    {
        this.modules = Maps.newConcurrentMap();
    }

    @Override
    public Module getModule( final ModuleKey key )
        throws ModuleNotFoundException
    {
        final Module module = this.modules.get( key );
        if ( module == null )
        {
            throw new ModuleNotFoundException( key );
        }
        return module;
    }

    @Override
    public Modules getModules( final ModuleKeys keys )
    {
        final ImmutableList.Builder<Module> moduleList = ImmutableList.builder();
        for ( ModuleKey key : keys )
        {
            final Module module = this.modules.get( key );
            if ( module != null )
            {
                moduleList.add( module );
            }
        }
        return Modules.from( moduleList.build() );
    }

    @Override
    public Modules getAllModules()
    {
        return Modules.from( this.modules.values() );
    }

    protected void installModule( final Module module )
    {
        this.modules.put( module.getKey(), module );
    }
}
