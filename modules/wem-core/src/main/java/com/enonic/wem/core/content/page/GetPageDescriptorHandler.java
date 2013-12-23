package com.enonic.wem.core.content.page;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.api.command.content.page.GetPageDescriptor;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.xml.XmlSerializers;

public class GetPageDescriptorHandler
    extends CommandHandler<GetPageDescriptor>
{
    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final PageDescriptorKey key = this.command.getKey();

            final ModuleResourceKey moduleResourceKey = new ModuleResourceKey( key.getModuleKey(), key.getPath() );
            final Resource resource = context.getClient().execute( new GetModuleResource().resourceKey( moduleResourceKey ) );

            final String descriptorXml = resource.readAsString();
            final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
            XmlSerializers.pageDescriptor().parse( descriptorXml ).to( builder );

            final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
            builder.name( descriptorName );

            final PageDescriptor pageDescriptor = builder.build();

            command.setResult( pageDescriptor );
        }
        catch ( ResourceNotFoundException e )
        {
            throw new PageDescriptorNotFoundException( command.getKey(), e );
        }
    }
}
