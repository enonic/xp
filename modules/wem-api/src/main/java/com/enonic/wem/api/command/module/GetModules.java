package com.enonic.wem.api.command.module;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.ModuleVersions;

public final class GetModules
    extends Command<List<Module>>
{
    private boolean getAllModules = false;

    private List<ModuleVersion> modules = Lists.newArrayList();

    public GetModules module( final ModuleVersion module )
    {
        this.modules.add( module );
        return this;
    }

    public GetModules modules( final ModuleVersions modules )
    {
        this.modules.addAll( modules.getList() );
        return this;
    }

    public GetModules all()
    {
        getAllModules = true;
        return this;
    }

    boolean isGetAll()
    {
        return getAllModules;
    }

    List<ModuleVersion> getModules()
    {
        return modules;
    }

    @Override
    public void validate()
    {

    }
}
