package com.enonic.wem.core.content.page.image;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.image.GetImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.core.content.page.AbstractGetDescriptorHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetImageDescriptorHandler
    extends AbstractGetDescriptorHandler<GetImageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final ImageDescriptorKey key = this.command.getKey();

        final String descriptorXml = readDescriptorXml( key );
        final ImageDescriptor.Builder builder = ImageDescriptor.newImageDescriptor();
        XmlSerializers.imageDescriptor().parse( descriptorXml ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );

        final ImageDescriptor imageDescriptor = builder.build();

        command.setResult( imageDescriptor );
    }
}
