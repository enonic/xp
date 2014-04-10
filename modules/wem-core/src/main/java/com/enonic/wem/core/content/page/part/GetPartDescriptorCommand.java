package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

final class GetPartDescriptorCommand
{
    private PartDescriptorKey key;

    private ModuleService moduleService;

    public PartDescriptor execute()
    {
        try
        {
            final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( this.key );
            final Resource resource = this.moduleService.getResource( moduleResourceKey );

            final String descriptorXml = resource.readAsString();
            final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
            XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );
            builder.name( this.key.getName() ).key( this.key );

            return builder.build();
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PartDescriptorNotFoundException( this.key, e );
        }
    }

    public GetPartDescriptorCommand key( final PartDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public GetPartDescriptorCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }
}
