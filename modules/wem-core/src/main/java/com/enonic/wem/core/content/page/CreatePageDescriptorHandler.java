package com.enonic.wem.core.content.page;

import com.enonic.wem.api.command.content.page.CreatePageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.content.page.PageDescriptorXml;

import static com.enonic.wem.api.content.page.PageDescriptor.newPageDescriptor;

public class CreatePageDescriptorHandler
    extends CommandHandler<CreatePageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final PageDescriptor pageDescriptor = newPageDescriptor().
            config( command.getConfig() ).
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String pageDescriptorXml = serialize( pageDescriptor );
        DescriptorHelper.storeDescriptorResource( pageDescriptor, pageDescriptorXml, this.context.getClient() );

        command.setResult( pageDescriptor );
    }

    private String serialize( final PageDescriptor pageDescriptor )
    {
        final PageDescriptorXml pageDescriptorXml = new PageDescriptorXml();
        pageDescriptorXml.from( pageDescriptor );
        return XmlSerializers.pageDescriptor().serialize( pageDescriptorXml );
    }
}
