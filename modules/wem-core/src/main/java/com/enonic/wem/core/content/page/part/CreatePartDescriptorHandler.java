package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.command.content.page.part.CreatePartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.page.DescriptorHelper;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.content.page.PartDescriptorXml;

import static com.enonic.wem.api.content.page.part.PartDescriptor.newPartDescriptor;

public class CreatePartDescriptorHandler
    extends CommandHandler<CreatePartDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final PartDescriptor partDescriptor = newPartDescriptor().
            config( command.getConfig() ).
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String partDescriptorXml = serialize( partDescriptor );
        DescriptorHelper.storeDescriptorResource( partDescriptor, partDescriptorXml, this.context.getClient() );

        command.setResult( partDescriptor );
    }

    private String serialize( final PartDescriptor partDescriptor )
    {
        final PartDescriptorXml partDescriptorXml = new PartDescriptorXml();
        partDescriptorXml.from( partDescriptor );
        return XmlSerializers.partDescriptor().serialize( partDescriptorXml );
    }
}
