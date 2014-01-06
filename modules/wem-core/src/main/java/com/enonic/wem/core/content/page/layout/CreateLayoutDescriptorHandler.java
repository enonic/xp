package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.command.content.page.layout.CreateLayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorXml;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.page.DescriptorStorageHelper;
import com.enonic.wem.xml.XmlSerializers;

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
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            key( command.getKey() ).
            build();

        final String layoutDescriptorXml = serialize( layoutDescriptor );

        new DescriptorStorageHelper( this.context.getClient() ).
            store( layoutDescriptor, layoutDescriptorXml );

        command.setResult( layoutDescriptor );
    }

    private String serialize( final LayoutDescriptor layoutDescriptor )
    {
        final LayoutDescriptorXml layoutDescriptorXml = new LayoutDescriptorXml();
        layoutDescriptorXml.from( layoutDescriptor );
        return XmlSerializers.layoutDescriptor().serialize( layoutDescriptorXml );
    }
}
