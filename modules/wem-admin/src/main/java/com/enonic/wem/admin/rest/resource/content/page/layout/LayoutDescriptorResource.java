package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorsJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.command.content.page.layout.GetLayoutDescriptorsByModules;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutDescriptorResource
    extends AbstractResource
{
    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String layoutDescriptorKey )
    {
        final LayoutDescriptorKey key = LayoutDescriptorKey.from( layoutDescriptorKey );
        final LayoutDescriptor descriptor = getDescriptor( key, client );
        final LayoutDescriptorJson json = new LayoutDescriptorJson( descriptor );
        return json;
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public LayoutDescriptorsJson getByModules( final com.enonic.wem.admin.rest.resource.content.page.layout.GetByModulesParams params )
    {
        final GetLayoutDescriptorsByModules getDescriptors = page().descriptor().layout().getByModules( params.getModuleKeys() );
        final LayoutDescriptors layoutDescriptors = client.execute( getDescriptors );
        return new LayoutDescriptorsJson( layoutDescriptors );
    }

    private LayoutDescriptor getDescriptor( final LayoutDescriptorKey key, final Client client )
    {
        final GetLayoutDescriptor getLayoutDescriptor = page().descriptor().layout().getByKey( key );
        return client.execute( getLayoutDescriptor );
    }
}
