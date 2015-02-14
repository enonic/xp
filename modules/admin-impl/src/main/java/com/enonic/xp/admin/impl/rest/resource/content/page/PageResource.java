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
import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.form.InlineMixinsToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;

@Path(ResourceConstants.REST_ROOT + "content/page")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class PageResource
    implements AdminResource
{
    private PageService pageService;

    private ContentTypeService contentTypeService;

    private InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer;

    private ContentPrincipalsResolver principalsResolver;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageJson params )
    {
        final CreatePageParams command = params.getCreatePage();
        final Content updatedContent = this.pageService.create( command );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdatePageJson params )
    {
        final UpdatePageParams command = params.getUpdatePage();
        final Content updatedContent = this.pageService.update( command );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    @GET
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content updatedContent = this.pageService.delete( contentId );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), inlineMixinsToFormItemsTransformer, principalsResolver );
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService );
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.inlineMixinsToFormItemsTransformer = new InlineMixinsToFormItemsTransformer( mixinService );
    }

    @Reference
    public void setPageService( final PageService pageService )
    {
        this.pageService = pageService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }
}
