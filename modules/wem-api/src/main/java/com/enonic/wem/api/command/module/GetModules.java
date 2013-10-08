package com.enonic.wem.api.command.module;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public final class GetModules
    extends Command<List<Module>>
{
    private boolean getAllModules = false;

    private List<ModuleKey> modules = Lists.newArrayList();

    public GetModules modules( final ModuleKeys modules )
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

    public List<ModuleKey> getModules()
    {
        return modules;
    }

    @Override
    public void validate()
    {

    }
}
