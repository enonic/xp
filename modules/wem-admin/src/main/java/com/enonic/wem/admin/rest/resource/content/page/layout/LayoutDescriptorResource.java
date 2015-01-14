package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.json.content.page.region.LayoutDescriptorJson;
import com.enonic.wem.admin.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.LayoutDescriptor;
import com.enonic.wem.api.content.page.region.LayoutDescriptorService;
import com.enonic.wem.api.content.page.region.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKey;

@Path(ResourceConstants.REST_ROOT + "content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("admin-login")
public final class LayoutDescriptorResource
    implements AdminResource
{
    private LayoutDescriptorService layoutDescriptorService;

    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String layoutDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( layoutDescriptorKey );
        final LayoutDescriptor descriptor = layoutDescriptorService.getByKey( key );
        return new LayoutDescriptorJson( descriptor );
    }

    @GET
    @Path("list/by_module")
    public LayoutDescriptorsJson getByModules( @QueryParam("moduleKey") final String moduleKey )
    {
        final LayoutDescriptors descriptors = layoutDescriptorService.getByModule( ModuleKey.from( moduleKey ) );
        return new LayoutDescriptorsJson( descriptors );
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
