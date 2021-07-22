package com.enonic.xp.admin.impl.rest.resource.content.page;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.CMS_PATH;
import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.REST_ROOT;

@Path(REST_ROOT + "{content:(content|" + CMS_PATH + "/content)}/page/template")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class PageTemplateResource
    implements JaxRsComponent
{
/*
    private PageTemplateService pageTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    private JsonObjectsFactory jsonObjectsFactory;

    @GET
    public ContentJson getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey );
        return jsonObjectsFactory.createContentJson( pageTemplate );
    }

    @GET
    @Path("list")
    public ContentListJson<ContentJson> list( @QueryParam("siteId") String siteIdAsString )
    {

        final ContentId siteId = ContentId.from( siteIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );

        final ContentListMetaData metaData =
            ContentListMetaData.create().totalHits( pageTemplates.getSize() ).hits( pageTemplates.getSize() ).build();
        return new ContentListJson<>( pageTemplates.toContents(), metaData, jsonObjectsFactory::createContentJson );
    }

    @GET
    @Path("listByCanRender")
    public ContentListJson<ContentJson> listByCanRender( @QueryParam("siteId") String siteIdAsString,
                                                         @QueryParam("contentTypeName") String contentTypeName )
    {
        final ContentId siteId = ContentId.from( siteIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );
        final Predicate<PageTemplate> spec = PageTemplateFilter.canRender( ContentTypeName.from( contentTypeName ) );
        final PageTemplates filteredPageTemplates = pageTemplates.filter( spec );
        final ContentListMetaData metaData =
            ContentListMetaData.create().totalHits( filteredPageTemplates.getSize() ).hits( filteredPageTemplates.getSize() ).build();
        return new ContentListJson<>( filteredPageTemplates.toContents(), metaData, jsonObjectsFactory::createContentJson );
    }

    @GET
    @Path("default")
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
        return jsonObjectsFactory.createContentJson( pageTemplate );
    }

    @GET
    @Path("isRenderable")
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

                return new ControllerMappingsResolver( siteService ).canRender( content, nearestSite );
            }
            return false;
        }
        catch ( ContentNotFoundException e )
        {
            return false;
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageTemplateJson params )
    {
        CreatePageTemplateParams templateParams = params.getCreateTemplate();

        templateParams.name( ensureUniqueName( templateParams.getSite(), templateParams.getName() ) );

        PageTemplate template = this.pageTemplateService.create( templateParams );
        return jsonObjectsFactory.createContentJson( template );
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
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Reference
    public void setJsonObjectsFactory( final JsonObjectsFactory jsonObjectsFactory )
    {
        this.jsonObjectsFactory = jsonObjectsFactory;
    }
*/
}
