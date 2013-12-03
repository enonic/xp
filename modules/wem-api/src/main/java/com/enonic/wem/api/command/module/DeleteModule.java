package com.enonic.wem.api.command.module;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

public final class DeleteModule
    extends Command<Module>
{
    private ModuleKey module;

    public DeleteModule module( final ModuleKey module )
    {
        this.module = module;
        return this;
    }

    public ModuleKey getModule()
    {
        return module;
    }

    @Override
    public void validate()
    {
    }
}
