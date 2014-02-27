package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.part.GetPartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

public class GetPartDescriptorHandler
    extends CommandHandler<GetPartDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final PartDescriptorKey key = this.command.getKey();

            final ModuleResourceKey moduleResourceKey = DescriptorKeyToModuleResourceKey.translate( key );
            final Resource resource = context.getClient().execute( new GetModuleResource().resourceKey( moduleResourceKey ) );

            final String descriptorXml = resource.readAsString();
            final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
            XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );
            builder.name( key.getName() ).key( key );

            final PartDescriptor partDescriptor = builder.build();

            command.setResult( partDescriptor );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PartDescriptorNotFoundException( command.getKey(), e );
        }
    }
}
