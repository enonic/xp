package com.enonic.wem.admin.rest.resource.content.site;

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
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;

@Path("content/site")
@Produces(MediaType.APPLICATION_JSON)
public final class SiteResource
{
    private SiteService siteService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private AttachmentService attachmentService;

    private MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer;

    // TODO: Remove, content update is used instead
    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdateSiteJson updateSiteJson )
    {
        final UpdateSiteParams updateSiteCommand = updateSiteJson.getUpdateSite();
        final Content updatedContent = this.siteService.update( updateSiteCommand, ContentConstants.CONTEXT_STAGE );

        return new ContentJson( updatedContent, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer );
    }

    // TODO: move to ContentResource service method also
    @POST
    @Path("nearest")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson getNearest( final GetNearestSiteJson params )
    {
        final ContentId contentId = params.getGetNearestSiteByContentId();
        final Content nearestSite = this.siteService.getNearestSite( contentId, ContentConstants.CONTEXT_STAGE );
        if ( nearestSite != null )
        {
            return new ContentJson( nearestSite, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer );
        }
        else
        {
            return null;
        }
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService, this.attachmentService );
    }

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinReferencesToFormItemsTransformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }

    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }
}
