package com.enonic.wem.core.content.page.layout;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.core.content.page.AbstractGetDescriptorHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetLayoutDescriptorHandler
    extends AbstractGetDescriptorHandler<GetLayoutDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final LayoutDescriptorKey key = this.command.getKey();

        final String descriptorXml = readDescriptorXml( key );
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );

        final LayoutDescriptor layoutDescriptor = builder.build();

        command.setResult( layoutDescriptor );
    }
}
