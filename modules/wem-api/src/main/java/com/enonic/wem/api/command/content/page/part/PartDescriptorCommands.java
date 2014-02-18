package com.enonic.wem.api.command.content.page.part;


import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.module.ModuleKeys;

public final class PartDescriptorCommands
{
    public GetPartDescriptor getByKey( final PartDescriptorKey key )
    {
        return new GetPartDescriptor( key );
    }

    public CreatePartDescriptor create()
    {
        return new CreatePartDescriptor();
    }

    public GetPartDescriptorsByModules getByModules( final ModuleKeys moduleKeys )
    {
        return new GetPartDescriptorsByModules( moduleKeys );
    }
}
