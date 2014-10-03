package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;

@javax.ws.rs.Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public final class PageTemplateResource
{
    protected final static Context STAGE_CONTEXT = Context.create().
        workspace( ContentConstants.WORKSPACE_STAGE ).
        repository( ContentConstants.CONTENT_REPO ).
        build();

    protected PageTemplateService pageTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private AttachmentService attachmentService;

    private MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer;

    @GET
    public ContentJson getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey, STAGE_CONTEXT );
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer );
    }

    @GET
    @javax.ws.rs.Path("list")
    public ContentListJson list( @QueryParam("siteId") String siteContentIdAsString )
    {

        final ContentId siteId = ContentId.from( siteContentIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId, STAGE_CONTEXT );

        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( pageTemplates.getSize() ).
            hits( pageTemplates.getSize() ).
            build();
        return new ContentListJson( pageTemplates.toContents(), metaData, newContentIconUrlResolver(),
                                    mixinReferencesToFormItemsTransformer );
    }

    @GET
    @javax.ws.rs.Path("listByCanRender")
    public ContentListJson listByCanRender( @QueryParam("siteId") String siteContentIdAsString,
                                            @QueryParam("contentTypeName") String contentTypeName )
    {
        final ContentId siteId = ContentId.from( siteContentIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId, STAGE_CONTEXT );
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateParams().canRender( ContentTypeName.from( contentTypeName ) ).build();
        final PageTemplates filteredPageTemplates = pageTemplates.filter( spec );
        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( filteredPageTemplates.getSize() ).
            hits( filteredPageTemplates.getSize() ).
            build();
        return new ContentListJson( filteredPageTemplates.toContents(), metaData, newContentIconUrlResolver(),
                                    mixinReferencesToFormItemsTransformer );
    }

    @GET
    @javax.ws.rs.Path("default")
    public ContentJson getDefault( @QueryParam("siteId") String siteContentIdAsString,
                                   @QueryParam("contentTypeName") String contentTypeNameAsString )
    {
        final ContentId siteId = ContentId.from( siteContentIdAsString );
        final ContentTypeName contentTypeName = ContentTypeName.from( contentTypeNameAsString );
        final PageTemplate pageTemplate = pageTemplateService.getDefault( GetDefaultPageTemplateParams.create().
            site( siteId ).
            contentType( contentTypeName ).
            build(), STAGE_CONTEXT );
        if ( pageTemplate == null )
        {
            return null;
        }
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer );
    }

    // TODO: Move to some kind of Portal meta resource?
    @GET
    @javax.ws.rs.Path("isRenderable")
    public boolean isRenderable( @QueryParam("contentId") String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        try
        {
            final Content content = contentService.getById( contentId, STAGE_CONTEXT );
            final Content nearestSite = this.siteService.getNearestSite( contentId, STAGE_CONTEXT );

            if ( nearestSite != null )
            {
                if ( content.isPageTemplate() )
                {
                    return true;
                }

                final ContentId siteId = nearestSite.getId();
                final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId, STAGE_CONTEXT );

                for ( final PageTemplate pageTemplate : pageTemplates )
                {
                    if ( pageTemplate.canRender( content.getType() ) )
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        catch ( ContentNotFoundException e )
        {
            return false;
        }
    }

    private ContentIconUrlResolver newContentIconUrlResolver()
    {
        return new ContentIconUrlResolver( this.contentTypeService, this.attachmentService );
    }

    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
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

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinReferencesToFormItemsTransformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }
}
