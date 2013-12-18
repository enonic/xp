package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutDescriptorResource
    extends AbstractResource
{
    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
    {
        final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
        final LayoutDescriptor descriptor = getDescriptor( key, client );
        final LayoutDescriptorJson json = new LayoutDescriptorJson( descriptor );
        return json;
    }

    private LayoutDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
    {
        final LayoutDescriptorKey layoutDescriptorKey = LayoutDescriptorKey.from( key.getModuleKey(), key.getPath() );
        final GetLayoutDescriptor getLayoutDescriptor = page().descriptor().layout().getByKey( layoutDescriptorKey );
        return client.execute( getLayoutDescriptor );
    }
}
