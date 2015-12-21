package com.enonic.xp.admin.impl.adminapp;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceProcessor;

public final class GetAdminApplicationDescriptorCommand
    extends AbstractGetAdminApplicationDescriptorCommand<GetAdminApplicationDescriptorCommand>
{
    private DescriptorKey descriptorKey;

    public GetAdminApplicationDescriptorCommand descriptorKey( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
        return this;
    }

    public AdminApplicationDescriptor execute()
    {
        final ResourceProcessor<DescriptorKey, AdminApplicationDescriptor> processor = createProcessor( descriptorKey );
        return this.resourceService.processResource( processor );
    }
}
