package com.enonic.wem.portal.internal.content;

import javax.inject.Inject;

import org.restlet.resource.ResourceException;

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
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.portal.internal.base.WorkspaceBaseResource;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;

public abstract class RenderBaseResource
    extends WorkspaceBaseResource
{
    protected JsControllerFactory controllerFactory;

    protected PageDescriptorService pageDescriptorService;

    protected PageTemplateService pageTemplateService;

    protected SiteTemplateService siteTemplateService;

    protected ContentService contentService;

    protected SiteService siteService;

    protected String contentPath;

    @Override
    protected void doInit()
        throws ResourceException
    {
        super.doInit();
        this.contentPath = getAttribute( "path" );
    }

    protected final Content getSite( final Content content )
    {
        final Content siteContent = this.siteService.getNearestSite( content.getId(), Context.create().
            workspace( this.workspace ).
            repository( ContentConstants.CONTENT_REPO ).
            build() );

        if ( siteContent == null )
        {
            throw notFound( "Site for content [%s] not found", content.getPath() );
        }

        return siteContent;
    }

    protected final Content getContent( final String contentSelector )
    {
        final boolean inEditMode = ( this.mode == RenderingMode.EDIT );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }

            throw notFound( "Page [%s] not found.", contentId.toString() );
        }
        else
        {
            final ContentPath contentPath = ContentPath.from( contentSelector );
            final Content content = getContentByPath( contentPath );
            if ( content != null )
            {
                return content;
            }

            throw notFound( "Page [%s] not found", contentPath.toString() );
        }
    }

    protected final Page getPage( final Content content )
    {
        if ( !content.hasPage() )
        {
            throw notFound( "Content [%s] is not a page", content.getPath().toString() );
        }

        return content.getPage();
    }

    protected final PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptorKey descriptorKey = pageTemplate.getDescriptor();
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( descriptorKey );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor for template [%s] not found", pageTemplate.getName() );
        }

        return pageDescriptor;
    }

    protected PageTemplate getPageTemplate( final Page page, final Site site )
    {
        final PageTemplate pageTemplate = pageTemplateService.getByKey( page.getTemplate(), site.getTemplate() );
        if ( pageTemplate == null )
        {
            throw notFound( "Page template [%s] not found", page.getTemplate() );
        }

        return pageTemplate;
    }

    protected final PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Site site )
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
            return this.contentService.getByPath( contentPath, Context.create().
                workspace( this.workspace ).
                repository( ContentConstants.CONTENT_REPO ).
                build() );
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
            return this.contentService.getById( contentId, Context.create().
                workspace( this.workspace ).
                repository( ContentConstants.CONTENT_REPO ).
                build() );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    @Inject
    public void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }

    @Inject
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Inject
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }

    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Inject
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
