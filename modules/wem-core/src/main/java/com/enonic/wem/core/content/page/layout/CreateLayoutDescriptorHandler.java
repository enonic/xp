package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.command.content.page.layout.CreateLayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.page.DescriptorHelper;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.content.page.LayoutDescriptorXml;

import static com.enonic.wem.api.content.page.layout.LayoutDescriptor.newLayoutDescriptor;

public class CreateLayoutDescriptorHandler
    extends CommandHandler<CreateLayoutDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final LayoutDescriptor layoutDescriptor = newLayoutDescriptor().
            config( command.getConfig() ).
            controllerResource( command.getControllerResource() ).
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String layoutDescriptorXml = serialize( layoutDescriptor );
        DescriptorHelper.storeDescriptorResource( layoutDescriptor, layoutDescriptorXml, this.context.getClient() );

        command.setResult( layoutDescriptor );
    }

    private String serialize( final LayoutDescriptor layoutDescriptor )
    {
        final LayoutDescriptorXml layoutDescriptorXml = new LayoutDescriptorXml();
        layoutDescriptorXml.from( layoutDescriptor );
        return XmlSerializers.layoutDescriptor().serialize( layoutDescriptorXml );
    }
}
