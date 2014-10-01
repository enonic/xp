package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.json.content.page.PageTemplateListJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeName;

@javax.ws.rs.Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public final class PageTemplateResource
{
    private PageTemplateService pageTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    @GET
    public PageTemplateJson getByKey( @QueryParam("siteTemplateKey") final String siteTemplateKeyAsString,
                                      @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey, siteTemplateKey );
        return new PageTemplateJson( pageTemplate );
    }

    @GET
    @javax.ws.rs.Path("list")
    public PageTemplateListJson list( @QueryParam("key") String siteTemplateKeyAsString )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySiteTemplate( siteTemplateKey );

        return new PageTemplateListJson( pageTemplates );
    }

    @GET
    @javax.ws.rs.Path("listByCanRender")
    public PageTemplateListJson listByCanRender( @QueryParam("siteTemplateKey") String siteTemplateKeyAsString,
                                                 @QueryParam("contentTypeName") String contentTypeName )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySiteTemplate( siteTemplateKey );
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateParams().canRender( ContentTypeName.from( contentTypeName ) ).build();

        return new PageTemplateListJson( pageTemplates.filter( spec ) );

    }

    @GET
    @javax.ws.rs.Path("default")
    public PageTemplateJson getDefault( @QueryParam("siteTemplateKey") String siteTemplateKeyAsString,
                                        @QueryParam("contentTypeName") String contentTypeNameAsString )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final ContentTypeName contentTypeName = ContentTypeName.from( contentTypeNameAsString );
        final PageTemplate pageTemplate = pageTemplateService.getDefault( siteTemplateKey, contentTypeName );
        if ( pageTemplate == null )
        {
            return null;
        }
        return new PageTemplateJson( pageTemplate );
    }

    // TODO: Move to some kind of Portal meta resource?
    @GET
    @javax.ws.rs.Path("isRenderable")
    public boolean isRenderable( @QueryParam("contentId") String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        try
        {
            final Content content = contentService.getById( contentId, ContentConstants.CONTEXT_STAGE );
            final Content nearestSite = this.siteService.getNearestSite( contentId, ContentConstants.CONTEXT_STAGE );

            if ( nearestSite != null )
            {
                final ContentTypeName type = content.getType();
                final SiteTemplateKey siteTemplateKey = nearestSite.getSite().getTemplate();
                final PageTemplates pageTemplates = pageTemplateService.getBySiteTemplate( siteTemplateKey );

                for ( final PageTemplate pageTemplate : pageTemplates )
                {
                    if ( pageTemplate.canRender( type ) )
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        catch ( SiteTemplateNotFoundException | ContentNotFoundException e )
        {
            return false;
        }
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
}
