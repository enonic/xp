package com.enonic.wem.portal.internal.content;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.internal.base.BaseResource;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;

public abstract class RenderBaseResource
    extends BaseResource
{
    protected JsControllerFactory controllerFactory;

    protected PageDescriptorService pageDescriptorService;

    protected PageTemplateService pageTemplateService;

    protected ContentService contentService;

    protected PostProcessor postProcessor;

    @PathParam("contentPath")
    protected String contentPath;

    protected Workspace workspace;

    protected RenderingMode mode;

    @javax.ws.rs.core.Context
    protected Request request;

    @javax.ws.rs.core.Context
    protected UriInfo uriInfo;

    @PathParam("workspace")
    public void setWorkspace( final String value )
    {
        this.workspace = Workspace.from( value );
    }

    @PathParam("mode")
    public void setMode( final String mode )
    {
        this.mode = parseMode( mode );
    }

    protected final Content getSite( final Content content )
    {
        final Content siteContent = this.contentService.getNearestSite( content.getId(), resolveContext() );

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
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( pageTemplate.getDescriptor() );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor for template [%s] not found", pageTemplate.getName() );
        }

        return pageDescriptor;
    }

    protected PageTemplate getPageTemplate( final Page page )
    {
        final PageTemplate pageTemplate = pageTemplateService.getByKey( page.getTemplate(), resolveContext() );
        if ( pageTemplate == null )
        {
            throw notFound( "Page template [%s] not found", page.getTemplate() );
        }

        return pageTemplate;
    }

    protected final PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Content site )
    {
        return this.pageTemplateService.getDefault(
            GetDefaultPageTemplateParams.create().site( site.getId() ).contentType( contentType ).build(), resolveContext() );
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
            return this.contentService.getById( contentId, resolveContext() );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Context resolveContext()
    {
        return Context.create().
            workspace( this.workspace ).
            repository( ContentConstants.CONTENT_REPO ).
            build();
    }
}
