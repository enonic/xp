package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;

public final class GetModules
    extends Command<Modules>
{
    private boolean getAllModules = false;

    private ModuleKeys modules;

    public GetModules modules( final ModuleKeys modules )
    {
        this.modules = modules;
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

    public ModuleKeys getModules()
    {
        return modules;
    }

    @Override
    public void validate()
    {

    }
}
