package com.enonic.xp.admin.impl.tool;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceProcessor;

public final class GetAdminToolDescriptorCommand
    extends AbstractGetAdminToolDescriptorCommand<GetAdminToolDescriptorCommand>
{
    private DescriptorKey descriptorKey;

    public GetAdminToolDescriptorCommand descriptorKey( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
        return this;
    }

    public AdminToolDescriptor execute()
    {
        final ResourceProcessor<DescriptorKey, AdminToolDescriptor> processor = createProcessor( descriptorKey );
        return this.resourceService.processResource( processor );
    }
}
