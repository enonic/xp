package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

public final class GetModule
    extends Command<Module>
{
    private ModuleKey moduleKey;

    public GetModule module( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    @Override
    public void validate()
    {

    }
}
