package com.enonic.wem.api.content.page.text;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKeys;

public class GetTextDescriptorsByModules
    extends Command<TextDescriptors>
{
    private final ModuleKeys moduleKeys;

    public GetTextDescriptorsByModules( final ModuleKeys moduleKeys )
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
