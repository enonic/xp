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

import com.enonic.xp.admin.AdminResource;
import com.enonic.xp.admin.impl.json.content.page.PageDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.rest.resource.content.page.part.GetByModulesParams;
import com.enonic.xp.admin.rest.resource.ResourceConstants;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.security.RoleKeys;

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
    public PageDescriptorListJson getByModule( @QueryParam("applicationKey") final String applicationKey )
    {
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByModule( ApplicationKey.from( applicationKey ) );
        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageDescriptorListJson getByModules( final GetByModulesParams params )
    {
        final ApplicationKeys applicationKeys = ApplicationKeys.from( params.getApplicationKeys() );
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByModules( applicationKeys );
        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }
}
