package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.json.content.ContentListJson;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.PageTemplateSpec;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;

@javax.ws.rs.Path(ResourceConstants.REST_ROOT + "content/page/template")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class PageTemplateResource
    implements AdminResource
{
    protected PageTemplateService pageTemplateService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer;

    private ContentPrincipalsResolver principalsResolver;

    @GET
    public ContentJson getByKey( @QueryParam("key") final String pageTemplateKeyAsString )
        throws IOException
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( pageTemplateKeyAsString );
        final PageTemplate pageTemplate = pageTemplateService.getByKey( pageTemplateKey );
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
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
        return new ContentListJson( pageTemplates.toContents(), metaData, newContentIconUrlResolver(),
                                    mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("listByCanRender")
    public ContentListJson listByCanRender( @QueryParam("siteId") String siteIdAsString,
                                            @QueryParam("contentTypeName") String contentTypeName )
    {
        final ContentId siteId = ContentId.from( siteIdAsString );
        final PageTemplates pageTemplates = pageTemplateService.getBySite( siteId );
        final PageTemplateSpec spec = PageTemplateSpec.newPageTemplateParams().canRender( ContentTypeName.from( contentTypeName ) ).build();
        final PageTemplates filteredPageTemplates = pageTemplates.filter( spec );
        final ContentListMetaData metaData = ContentListMetaData.create().
            totalHits( filteredPageTemplates.getSize() ).
            hits( filteredPageTemplates.getSize() ).
            build();
        return new ContentListJson( filteredPageTemplates.toContents(), metaData, newContentIconUrlResolver(),
                                    mixinReferencesToFormItemsTransformer, principalsResolver );
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
        return new ContentJson( pageTemplate, newContentIconUrlResolver(), mixinReferencesToFormItemsTransformer, principalsResolver );
    }

    @GET
    @javax.ws.rs.Path("isRenderable")
    public boolean isRenderable( @QueryParam("contentId") String contentIdAsString )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        try
        {
            final Content content = contentService.getById( contentId );
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
                    if ( pageTemplate.canRender( content.getType() ) )
                    {
                        return pageTemplate.hasPage();
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
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinReferencesToFormItemsTransformer = new MixinReferencesToFormItemsTransformer( mixinService );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }
}
