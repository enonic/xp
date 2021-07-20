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

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.JsonObjectsFactory;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.CreatePageParams;
import com.enonic.xp.page.PageService;
import com.enonic.xp.page.UpdatePageParams;
import com.enonic.xp.security.RoleKeys;

import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.CMS_PATH;
import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.REST_ROOT;

@Path(REST_ROOT + "{content:(content|" + CMS_PATH + "/content)}/page")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class PageResource
    implements JaxRsComponent
{
    private PageService pageService;

    private JsonObjectsFactory jsonObjectsFactory;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageJson params )
    {
        final CreatePageParams command = params.getCreatePage();
        final Content updatedContent = this.pageService.create( command );

        return jsonObjectsFactory.createContentJson( updatedContent );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdatePageJson params )
    {
        final UpdatePageParams command = params.getUpdatePage();
        final Content updatedContent = this.pageService.update( command );

        return jsonObjectsFactory.createContentJson( updatedContent );
    }

    @GET
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content updatedContent = this.pageService.delete( contentId );

        return jsonObjectsFactory.createContentJson( updatedContent );
    }

    @Reference
    public void setPageService( final PageService pageService )
    {
        this.pageService = pageService;
    }

    @Reference
    public void setJsonObjectsFactory( final JsonObjectsFactory jsonObjectsFactory )
    {
        this.jsonObjectsFactory = jsonObjectsFactory;
    }
}
