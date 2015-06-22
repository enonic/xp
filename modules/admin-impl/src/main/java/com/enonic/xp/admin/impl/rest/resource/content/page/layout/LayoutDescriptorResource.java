package com.enonic.xp.admin.impl.rest.resource.content.page.layout;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
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
    public LayoutDescriptorsJson getByModules( final GetByModulesParams params )
    {
        final LayoutDescriptors descriptors = layoutDescriptorService.getByModules( params.getModuleKeys() );
        return new LayoutDescriptorsJson( descriptors );
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }
}
