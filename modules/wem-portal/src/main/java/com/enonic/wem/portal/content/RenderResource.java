package com.enonic.wem.portal.content;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.rendering.RenderResult;


public abstract class RenderResource
{
    private static final String EDIT_MODE = "edit";

    @Inject
    protected JsControllerFactory controllerFactory;

    @Inject
    protected PageDescriptorService pageDescriptorService;

    @Inject
    protected PageTemplateService pageTemplateService;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Inject
    protected ContentService contentService;

    @Inject
    protected SiteService siteService;

    protected Content getSite( final Content content )
    {
        final Content siteContent = this.siteService.getNearestSite( content.getId() );
        if ( siteContent == null )
        {
            throw new SiteNotFoundException( content.getPath() );
        }
        return siteContent;
    }

    protected Content getContent( final String contentSelector, final String mode )
    {
        final boolean inEditMode = EDIT_MODE.equals( mode );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
            throw PortalWebException.notFound().message( "Page [{0}] not found.", contentId ).build();
        }
        else
        {
            final ContentPath contentPath = ContentPath.from( contentSelector );
            final Content content = getContentByPath( contentPath );
            if ( content != null )
            {
                return content;
            }
            throw PortalWebException.notFound().message( "Page [{0}] not found.", contentPath ).build();
        }
    }

    protected Page getPage( final Content content )
    {
        if ( !content.isPage() )
        {
            throw PortalWebException.notFound().message( "Page not found." ).build();
        }
        return content.getPage();
    }

    protected PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw PortalWebException.notFound().message( "Page descriptor for template [{0}] not found.", pageTemplate.getName() ).build();
        }
        return pageDescriptor;
    }

    protected PageTemplate getPageTemplate( final Page page, final Site site )
    {
        final PageTemplate pageTemplate = pageTemplateService.getByKey( page.getTemplate(), site.getTemplate() );
        if ( pageTemplate == null )
        {
            throw PortalWebException.notFound().message( "Page template [{0}] not found.", page.getTemplate() ).build();
        }
        return pageTemplate;
    }

    protected PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Site site )
    {
        try
        {
            final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( site.getTemplate() );
            return siteTemplate.getDefaultPageTemplate( contentType );
        }
        catch ( SiteTemplateNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    protected final Response toResponse( final RenderResult result )
    {
        final Response.ResponseBuilder builder = Response.status( result.getStatus() ).
            type( result.getType() ).
            entity( result.getEntity() );

        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        return builder.build();
    }
}
