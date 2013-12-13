package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

@Path("content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PageDescriptorResource
    extends AbstractResource
{
    @GET
    public PageDescriptorJson getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
        throws IOException
    {
        final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
        final PageDescriptor descriptor = getDescriptor( key, client );
        final PageDescriptorJson json = new PageDescriptorJson( descriptor );
        return json;
    }

    static PageDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
        throws IOException
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();
        XmlSerializers.pageDescriptor().parse( descriptorResource.readAsString() ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );
        return builder.build();
    }
}
