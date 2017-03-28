package com.enonic.xp.admin.impl.tool;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

public final class GetAdminToolIconCommand
{
    private DescriptorKey descriptorKey;

    protected ResourceService resourceService;

    public GetAdminToolIconCommand descriptorKey( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
        return this;
    }

    public GetAdminToolIconCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    public String execute()
    {
        final ResourceProcessor<DescriptorKey, String> processor = createProcessor( descriptorKey );
        return this.resourceService.processResource( processor );
    }

    protected ResourceProcessor<DescriptorKey, String> createProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, String>().
            key( key ).
            segment( "adminToolIcon" ).
            keyTranslator( AdminToolDescriptor::toIconResourceKey ).
            processor( Resource::readString ).
            build();
    }
}
