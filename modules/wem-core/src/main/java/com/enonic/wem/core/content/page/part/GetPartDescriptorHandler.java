package com.enonic.wem.core.content.page.part;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.part.GetPartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.core.content.page.AbstractGetDescriptorHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetPartDescriptorHandler
    extends AbstractGetDescriptorHandler<GetPartDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        final PartDescriptorKey key = this.command.getKey();

        final String descriptorXml = readDescriptorXml( key );
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        XmlSerializers.partDescriptor().parse( descriptorXml ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );

        final PartDescriptor partDescriptor = builder.build();

        command.setResult( partDescriptor );
    }
}
