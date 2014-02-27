package com.enonic.wem.api.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKeys;

public class GetPartDescriptorsByModules
    extends Command<PartDescriptors>
{
    private final ModuleKeys moduleKeys;

    public GetPartDescriptorsByModules( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
    }

    public ModuleKeys getModuleKeys()
    {
        return moduleKeys;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( moduleKeys, "moduleKeys is required" );
    }
}
