package com.enonic.wem.admin.rest.resource.content.site;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.schema.content.ContentTypeService;

@Path("content/site")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteResource
{
    @Inject
    protected SiteService siteService;

    @Inject
    protected ContentTypeService contentTypeService;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Inject
    protected AttachmentService attachmentService;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreateSiteJson createSiteJson )
    {
        final CreateSiteParams createSiteCommand = createSiteJson.getCreateSite();
        final Content updatedContent = this.siteService.create( createSiteCommand, ContentConstants.CONTEXT_STAGE );

        return new ContentJson( updatedContent, newContentIconUrlResolver() );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdateSiteJson updateSiteJson )
    {
        final UpdateSiteParams updateSiteCommand = updateSiteJson.getUpdateSite();
        final Content updatedContent = this.siteService.update( updateSiteCommand, ContentConstants.CONTEXT_STAGE );

        return new ContentJson( updatedContent, newContentIconUrlResolver() );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( final DeleteSiteJson deleteSiteJson )
    {
        final ContentId deleteSiteCommand = deleteSiteJson.getDeleteSite();
        final Content deletedContent = this.siteService.delete( deleteSiteCommand, ContentConstants.CONTEXT_STAGE );

        return new ContentJson( deletedContent, newContentIconUrlResolver() );
    }

    @POST
    @Path("nearest")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson getNearest( final GetNearestSiteJson params )
    {
        final ContentId contentId = params.getGetNearestSiteByContentId();
        final Content nearestSite = this.siteService.getNearestSite( contentId, ContentConstants.CONTEXT_STAGE );
        if ( nearestSite != null )
        {
            return new ContentJson( nearestSite, newContentIconUrlResolver() );
        }
        else
        {
            return null;
        }
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.siteTemplateService, this.contentTypeService, this.attachmentService );
    }
}
