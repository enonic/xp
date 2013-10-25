package com.enonic.wem.api.command.module;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public final class GetModuleResource
    extends Command<Resource>
{
    private ModuleResourceKey resourceKey;

    public GetModuleResource resourceKey( final ModuleResourceKey value )
    {
        this.resourceKey = value;
        return this;
    }

    public ModuleResourceKey getResourceKey()
    {
        return resourceKey;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.resourceKey, "resourceKey cannot be null" );
    }
}
