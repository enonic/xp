package com.enonic.wem.core.content.page.image;

import com.enonic.wem.api.command.content.page.image.CreateImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.page.DescriptorHelper;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.content.page.ImageDescriptorXml;

import static com.enonic.wem.api.content.page.image.ImageDescriptor.newImageDescriptor;

public class CreateImageDescriptorHandler
    extends CommandHandler<CreateImageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final ImageDescriptor imageDescriptor = newImageDescriptor().
            config( command.getConfig() ).
            controllerResource( command.getControllerResource() ).
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String imageDescriptorXml = serialize( imageDescriptor );
        DescriptorHelper.storeDescriptorResource( imageDescriptor, imageDescriptorXml, this.context.getClient() );

        command.setResult( imageDescriptor );
    }

    private String serialize( final ImageDescriptor imageDescriptor )
    {
        final ImageDescriptorXml imageDescriptorXml = new ImageDescriptorXml();
        imageDescriptorXml.from( imageDescriptor );
        return XmlSerializers.imageDescriptor().serialize( imageDescriptorXml );
    }
}
