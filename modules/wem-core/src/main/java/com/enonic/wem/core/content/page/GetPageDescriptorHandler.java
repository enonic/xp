package com.enonic.wem.core.content.page;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.GetPageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.xml.XmlSerializers;

public class GetPageDescriptorHandler
    extends AbstractGetDescriptorHandler<GetPageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final PageDescriptorKey key = this.command.getKey();

        final String descriptorXml = readDescriptorXml( key );
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
        XmlSerializers.pageDescriptor().parse( descriptorXml ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );

        final PageDescriptor pageDescriptor = builder.build();

        command.setResult( pageDescriptor );
    }
}
