package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.xml.XmlSerializers;

final class GetPageDescriptorCommand
{
    private PageDescriptorKey key;

    protected ResourceService resourceService;

    public PageDescriptor execute()
    {
        try
        {
            return getDescriptor( this.key );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PageDescriptorNotFoundException( this.key, e );
        }
    }

    private PageDescriptor getDescriptor( final PageDescriptorKey key )
    {
        final ModuleResourceKey resourceKey = key.toResourceKey();
        final Resource resource = this.resourceService.getResource( resourceKey );

        final String descriptorXml = resource.readAsString();
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
        XmlSerializers.pageDescriptor().parse( descriptorXml ).to( builder );
        builder.key( key );

        return builder.build();
    }

    public GetPageDescriptorCommand key( final PageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public GetPageDescriptorCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }
}
