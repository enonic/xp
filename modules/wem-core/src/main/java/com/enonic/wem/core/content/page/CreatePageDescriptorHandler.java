package com.enonic.wem.core.content.page;

import com.enonic.wem.api.command.content.page.CreatePageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorXml;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;

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
            regions( command.getRegions() ).
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String pageDescriptorXml = serialize( pageDescriptor );

        new DescriptorStorageHelper( this.context.getClient() ).
            store( pageDescriptor, pageDescriptorXml );

        command.setResult( pageDescriptor );
    }

    private String serialize( final PageDescriptor pageDescriptor )
    {
        final PageDescriptorXml pageDescriptorXml = new PageDescriptorXml();
        pageDescriptorXml.from( pageDescriptor );
        return XmlSerializers.pageDescriptor().serialize( pageDescriptorXml );
    }
}
