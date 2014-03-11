package com.enonic.wem.core.content.page;

import java.io.IOException;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.util.Exceptions;
import com.enonic.wem.xml.XmlSerializers;

public class  GetPageDescriptorCommand
{
    private PageDescriptorKey key;

    private ModuleService moduleService;

    public PageDescriptor execute()
    {
        try
        {
            final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( key );
            final Resource resource = this.moduleService.getResource( moduleResourceKey );

            final String descriptorXml = resource.readAsString();
            final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
            XmlSerializers.pageDescriptor().parse( descriptorXml ).to( builder );
            builder.key( key );

            return builder.build();
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PageDescriptorNotFoundException( this.key, e );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving page descriptor [{0}]", this.key ).withCause( e );
        }
    }

    public GetPageDescriptorCommand key( final PageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public GetPageDescriptorCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }
}
