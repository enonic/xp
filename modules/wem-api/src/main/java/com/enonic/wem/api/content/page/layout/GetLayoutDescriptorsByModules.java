package com.enonic.wem.api.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKeys;

public class GetLayoutDescriptorsByModules
    extends Command<LayoutDescriptors>
{
    private final ModuleKeys moduleKeys;

    public GetLayoutDescriptorsByModules( final ModuleKeys moduleKeys )
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
