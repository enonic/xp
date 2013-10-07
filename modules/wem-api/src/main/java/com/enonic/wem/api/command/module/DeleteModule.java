package com.enonic.wem.api.command.module;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleVersion;

public final class DeleteModule
    extends Command<Boolean>
{
    private ModuleVersion module;

    public DeleteModule module( final ModuleVersion module )
    {
        this.module = module;
        return this;
    }

    ModuleVersion getModule()
    {
        return module;
    }

    @Override
    public void validate()
    {
    }
}
