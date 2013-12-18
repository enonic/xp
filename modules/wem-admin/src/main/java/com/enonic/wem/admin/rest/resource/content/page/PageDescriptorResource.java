package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.GetPageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;

import static com.enonic.wem.api.command.Commands.page;

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
        final PageDescriptorKey pageDescriptorKey = PageDescriptorKey.from( key.getModuleKey(), key.getPath() );
        final GetPageDescriptor getPageDescriptor = page().descriptor().page().getByKey( pageDescriptorKey );
        return client.execute( getPageDescriptor );
    }
}
