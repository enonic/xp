package com.enonic.xp.admin.impl.rest.resource.content.page;

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
import com.enonic.xp.admin.impl.json.content.page.PageDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.page.part.GetByModulesParams;
import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.core.content.page.PageDescriptorService;
import com.enonic.xp.core.content.page.PageDescriptors;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleKeys;
import com.enonic.xp.core.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class PageDescriptorResource
    implements AdminResource
{
    private PageDescriptorService pageDescriptorService;

    @GET
    public PageDescriptorJson getByKey( @QueryParam("key") final String pageDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( pageDescriptorKey );
        final PageDescriptor descriptor = pageDescriptorService.getByKey( key );
        final PageDescriptorJson json = new PageDescriptorJson( descriptor );
        return json;
    }

    @GET
    @Path("list/by_module")
    public PageDescriptorListJson getByModule( @QueryParam("moduleKey") final String moduleKey )
    {
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByModule( ModuleKey.from( moduleKey ) );
        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageDescriptorListJson getByModules( final GetByModulesParams params )
    {
        final ModuleKeys moduleKeys = ModuleKeys.from( params.getModuleKeys() );
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByModules( moduleKeys );
        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
