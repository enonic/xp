package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.json.content.page.PageTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypeName;

@javax.ws.rs.Path("content/page/template")
@Produces(MediaType.APPLICATION_JSON)
public final class PageTemplateResource
    extends AbstractResource
{
    @Inject
    protected PageTemplateService pageTemplateService;

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
            final Content content = client.execute( Commands.content().get().byId( contentId ) );
            final Content nearestSite = client.execute( Commands.site().getNearestSite().content( contentId ) );

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
}
