package com.enonic.wem.core.content.page.layout;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetLayoutDescriptorHandler
    extends CommandHandler<GetLayoutDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final LayoutDescriptorKey key = this.command.getKey();

            final ModuleResourceKey moduleResourceKey = new ModuleResourceKey( key.getModuleKey(), key.getPath() );
            final Resource resource = context.getClient().execute( new GetModuleResource().resourceKey( moduleResourceKey ) );

            final String descriptorXml = resource.readAsString();
            final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
            XmlSerializers.layoutDescriptor().parse( descriptorXml ).to( builder );

            final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
            builder.name( descriptorName );

            final LayoutDescriptor layoutDescriptor = builder.build();

            command.setResult( layoutDescriptor );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new LayoutDescriptorNotFoundException( command.getKey(), e );
        }
    }
}
