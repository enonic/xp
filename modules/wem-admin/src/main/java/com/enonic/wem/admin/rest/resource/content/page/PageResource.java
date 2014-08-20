package com.enonic.wem.admin.rest.resource.content.page;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentTypeService;

@Path("content/page")
@Produces(MediaType.APPLICATION_JSON)
public final class PageResource
{
    private static final Context STAGE_CONTEXT = new Context( ContentConstants.WORKSPACE_STAGE );

    @Inject
    protected PageService pageService;

    @Inject
    protected ContentTypeService contentTypeService;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Inject
    protected AttachmentService attachmentService;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageJson params )
    {
        final CreatePageParams command = params.getCreatePage();
        final Content updatedContent = this.pageService.create( command, STAGE_CONTEXT );

        return new ContentJson( updatedContent, newContentIconUrlResolver() );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdatePageJson params )
    {
        final UpdatePageParams command = params.getUpdatePage();
        final Content updatedContent = this.pageService.update( command, STAGE_CONTEXT );

        return new ContentJson( updatedContent, newContentIconUrlResolver() );
    }

    @GET
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( @QueryParam("contentId") final String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content updatedContent = this.pageService.delete( contentId, STAGE_CONTEXT );

        return new ContentJson( updatedContent, newContentIconUrlResolver() );
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.siteTemplateService, this.contentTypeService, this.attachmentService );
    }
}
