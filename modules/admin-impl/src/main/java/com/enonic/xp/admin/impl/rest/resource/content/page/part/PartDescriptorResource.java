package com.enonic.xp.admin.impl.rest.resource.content.page.part;

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
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.PartDescriptor;
import com.enonic.xp.content.page.region.PartDescriptorService;
import com.enonic.xp.content.page.region.PartDescriptors;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class PartDescriptorResource
    implements AdminResource
{
    private PartDescriptorService partDescriptorService;

    @GET
    public PartDescriptorJson getByKey( @QueryParam("key") final String partDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( partDescriptorKey );
        final PartDescriptor descriptor = partDescriptorService.getByKey( key );
        return new PartDescriptorJson( descriptor );
    }

    @GET
    @Path("list/by_module")
    public PartDescriptorsJson getByModule( @QueryParam("moduleKey") final String moduleKey )
    {
        final PartDescriptors descriptors = partDescriptorService.getByModule( ModuleKey.from( moduleKey ) );
        return new PartDescriptorsJson( descriptors );
    }


    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public PartDescriptorsJson getByModules( final GetByModulesParams params )
    {
        final PartDescriptors descriptors = partDescriptorService.getByModules( params.getModuleKeys() );
        return new PartDescriptorsJson( descriptors );
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }
}
