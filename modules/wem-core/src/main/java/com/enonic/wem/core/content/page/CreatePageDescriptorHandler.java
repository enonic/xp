package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.CreatePageDescriptorParams;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorXml;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.command.Commands.module;
import static com.enonic.wem.api.content.page.PageDescriptor.newPageDescriptor;
import static com.enonic.wem.api.resource.Resource.newResource;

public class CreatePageDescriptorHandler
    extends CommandHandler<CreatePageDescriptorParams>
{
    @Override
    public void handle()
        throws Exception
    {
        final PageDescriptor pageDescriptor = newPageDescriptor().
            config( command.getConfig() ).
            regions( command.getRegions() ).
            displayName( command.getDisplayName() ).
            key( command.getKey() ).
            build();

        final String pageDescriptorXml = serialize( pageDescriptor );

        store( pageDescriptor, pageDescriptorXml );

        command.setResult( pageDescriptor );
    }

    private String serialize( final PageDescriptor pageDescriptor )
    {
        final PageDescriptorXml pageDescriptorXml = new PageDescriptorXml();
        pageDescriptorXml.from( pageDescriptor );
        return XmlSerializers.pageDescriptor().serialize( pageDescriptorXml );
    }

    private void store( final PageDescriptor descriptor, final String descriptorXml )
    {
        final Resource descriptorResource = newResource().
            name( descriptor.getName().toString() ).
            stringValue( descriptorXml ).
            build();

        final ModuleResourceKey resourceKey = DescriptorKeyToModuleResourceKey.translate( descriptor.getKey() );

        this.context.getClient().execute( module().createResource().
            resourceKey( resourceKey ).
            resource( descriptorResource ) );
    }
}
