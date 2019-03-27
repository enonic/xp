package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.io.IOException;
import java.util.function.Predicate;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.page.CreatePageTemplateParams;
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
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteService;

@javax.ws.rs.Path(ResourceConstants.REST_ROOT + "content/page/template")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class PageTemplateResource
    implements JaxRsComponent
{
    private PageTemplateService pageTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private ContentPrincipalsResolver principalsResolver;

    private ContentIconUrlResolver contentIconUrlResolver;

    @GET
    public ContentJson getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey );
        return new ContentJson( pageTemplate, contentIconUrlResolver, principalsResolver );
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
        return new ContentListJson( pageTemplates.toContents(), metaData, contentIconUrlResolver, principalsResolver );
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
        return new ContentListJson( filteredPageTemplates.toContents(), metaData, contentIconUrlResolver, principalsResolver );
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
        return new ContentJson( pageTemplate, contentIconUrlResolver, principalsResolver );
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

            final Site nearestSite = this.contentService.getNearestSite( contentId );

            if ( nearestSite != null )
            {
                if ( content.isPageTemplate() )
                {
                    return ( (PageTemplate) content ).getController() != null;
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
                if ( page != null && page.hasDescriptor() )
                {
                    return true;
                }

                if ( new ControllerMappingsResolver( siteService ).canRender( content, nearestSite ) )
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

    @POST
    @javax.ws.rs.Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageTemplateJson params )
    {
        CreatePageTemplateParams templateParams = params.getCreateTemplate();

        templateParams.name( ensureUniqueName( templateParams.getSite(), templateParams.getName() ) );

        PageTemplate template = this.pageTemplateService.create( templateParams );
        return new ContentJson( template, contentIconUrlResolver, principalsResolver );
    }

    private ContentName ensureUniqueName( ContentPath parent, ContentName name )
    {
        String baseName = name.toString();
        String currentName = baseName;
        int counter = 1;
        while ( this.contentService.contentExists( ContentPath.from( ContentPath.from( parent, "_templates" ), currentName ) ) )
        {
            currentName = NamePrettyfier.create( baseName + "-" + counter++ );
        }
        return ContentName.from( currentName );
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
        this.contentIconUrlResolver = new ContentIconUrlResolver( contentTypeService );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
