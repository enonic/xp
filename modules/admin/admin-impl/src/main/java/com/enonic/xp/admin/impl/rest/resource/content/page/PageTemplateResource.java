package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.io.IOException;
import java.util.function.Predicate;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.json.content.ContentListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateFilter;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.page.PageTemplates;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;

@javax.ws.rs.Path(ResourceConstants.REST_ROOT + "content/page/template")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class PageTemplateResource
    implements JaxRsComponent
{
    protected PageTemplateService pageTemplateService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private ContentPrincipalsResolver principalsResolver;

    @GET
    public ContentJson getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey );
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("list")
    public ContentListJson list( @QueryParam("siteId") String siteIdAsString )
    {

        final ContentId siteId = ContentId.from( siteIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );

        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( pageTemplates.getSize() ).
            hits( pageTemplates.getSize() ).
            build();
        return new ContentListJson( pageTemplates.toContents(), metaData, newContentIconUrlResolver(), principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("listByCanRender")
    public ContentListJson listByCanRender( @QueryParam("siteId") String siteIdAsString,
                                            @QueryParam("contentTypeName") String contentTypeName )
    {
        final ContentId siteId = ContentId.from( siteIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );
        final Predicate<PageTemplate> spec = PageTemplateFilter.canRender( ContentTypeName.from( contentTypeName ) );
        final PageTemplates filteredPageTemplates = pageTemplates.filter( spec );
        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( filteredPageTemplates.getSize() ).
            hits( filteredPageTemplates.getSize() ).
            build();
        return new ContentListJson( filteredPageTemplates.toContents(), metaData, newContentIconUrlResolver(), principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("default")
    public ContentJson getDefault( @QueryParam("siteId") String siteIdAsString,
                                   @QueryParam("contentTypeName") String contentTypeNameAsString )
    {
        final ContentId siteId = ContentId.from( siteIdAsString );
        final ContentTypeName contentTypeName = ContentTypeName.from( contentTypeNameAsString );
        final PageTemplate pageTemplate = pageTemplateService.getDefault( GetDefaultPageTemplateParams.create().
            site( siteId ).
            contentType( contentTypeName ).
            build() );
        if ( pageTemplate == null )
        {
            return null;
        }
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("isRenderable")
    public boolean isRenderable( @QueryParam("contentId") String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        try
        {
            final Content content = this.contentService.getById( contentId );
            if ( content.getType().isFragment() )
            {
                return true;
            }

            final Content nearestSite = this.contentService.getNearestSite( contentId );

            if ( nearestSite != null )
            {
                if ( content.isPageTemplate() )
                {
                    return true;
                }

                final ContentId siteId = nearestSite.getId();
                final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );

                for ( final PageTemplate pageTemplate : pageTemplates )
                {
                    if ( pageTemplate.canRender( content.getType() ) && pageTemplate.hasPage() )
                    {
                        return true;
                    }
                }

                final Page page = content.getPage();
                if ( page != null && page.hasController() )
                {
                    return true;
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
        return new ContentIconUrlResolver( this.contentTypeService );
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
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
