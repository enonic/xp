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
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;

@Path("content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutDescriptorResource
{
    private LayoutDescriptorService layoutDescriptorService;

    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String layoutDescriptorKey )
    {
        final LayoutDescriptorKey key = LayoutDescriptorKey.from( layoutDescriptorKey );
        final LayoutDescriptor descriptor = layoutDescriptorService.getByKey( key );
        return new LayoutDescriptorJson( descriptor );
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public LayoutDescriptorsJson getByModules( final com.enonic.wem.admin.rest.resource.content.page.layout.GetByModulesParams params )
    {
        final LayoutDescriptors descriptors = layoutDescriptorService.getByModules( params.getModuleKeys() );
        return new LayoutDescriptorsJson( descriptors );
    }

    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }
}
