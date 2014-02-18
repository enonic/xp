package com.enonic.wem.api.command.content.page.layout;


import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.module.ModuleKeys;

public final class LayoutDescriptorCommands
{
    public GetLayoutDescriptor getByKey( final LayoutDescriptorKey key )
    {
        return new GetLayoutDescriptor( key );
    }

    public CreateLayoutDescriptor create()
    {
        return new CreateLayoutDescriptor();
    }

    public GetLayoutDescriptorsByModules getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModules( moduleKeys );
    }
}
