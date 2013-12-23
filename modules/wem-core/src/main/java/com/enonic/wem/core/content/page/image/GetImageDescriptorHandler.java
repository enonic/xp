package com.enonic.wem.core.content.page.image;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.image.GetImageDescriptor;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetImageDescriptorHandler
    extends CommandHandler<GetImageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final ImageDescriptorKey key = this.command.getKey();

            final ModuleResourceKey moduleResourceKey = new ModuleResourceKey( key.getModuleKey(), key.getPath() );
            final Resource resource = context.getClient().execute( new GetModuleResource().resourceKey( moduleResourceKey ) );

            final String descriptorXml = resource.readAsString();
            final ImageDescriptor.Builder builder = ImageDescriptor.newImageDescriptor();
            XmlSerializers.imageDescriptor().parse( descriptorXml ).to( builder );

            final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
            builder.name( descriptorName );

            final ImageDescriptor imageDescriptor = builder.build();

            command.setResult( imageDescriptor );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new ImageDescriptorNotFoundException( command.getKey(), e );
        }
    }
}
