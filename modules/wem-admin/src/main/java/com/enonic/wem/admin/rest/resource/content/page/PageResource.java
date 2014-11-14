package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.servlet.jaxrs.JaxRsComponent;

@Path(ResourceConstants.REST_ROOT + "content/page")
@Produces(MediaType.APPLICATION_JSON)
public final class PageResource
    implements JaxRsComponent
{
    private PageService pageService;

    private ContentTypeService contentTypeService;

    private AttachmentService attachmentService;

    private MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer;

    private ContentPrincipalsResolver principalsResolver;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageJson params )
    {
        final CreatePageParams command = params.getCreatePage();
        final Content updatedContent = this.pageService.create( command );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdatePageJson params )
    {
        final UpdatePageParams command = params.getUpdatePage();
        final Content updatedContent = this.pageService.update( command );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @GET
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content updatedContent = this.pageService.delete( contentId );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService, this.attachmentService );
    }

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinReferencesToFormItemsTransformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }

    public void setPageService( final PageService pageService )
    {
        this.pageService = pageService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }

}
