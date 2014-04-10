package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

final class GetLayoutDescriptorCommand
{
    private LayoutDescriptorKey key;

    private ModuleService moduleService;

    public LayoutDescriptor execute()
    {
        try
        {
            final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( this.key );
            final Resource resource = this.moduleService.getResource( moduleResourceKey );

            final String descriptorXml = resource.readAsString();
            final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
            XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );
            builder.name( this.key.getName() ).key( this.key );

            return builder.build();
        }
        catch ( ResourceNotFoundException e )
        {
            throw new LayoutDescriptorNotFoundException( this.key, e );
        }
    }

    public GetLayoutDescriptorCommand key( final LayoutDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public GetLayoutDescriptorCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }
}
