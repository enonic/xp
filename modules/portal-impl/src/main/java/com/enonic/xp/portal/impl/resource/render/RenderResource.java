package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.Site;

public abstract class RenderResource
    extends BaseSubResource
{
    protected final Site getSite( final Content content )
    {
        final Site site = this.services.getContentService().getNearestSite( content.getId() );

        if ( site == null )
        {
            throw notFound( "Site for content [%s] not found", content.getPath() );
        }

        return site;
    }

    protected final Content getContent( final String contentSelector )
    {
        final boolean inEditMode = ( this.mode == RenderMode.EDIT );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector.substring( 1 ) );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
        }

        final ContentPath contentPath = ContentPath.from( contentSelector ).asAbsolute();
        final Content content = getContentByPath( contentPath );
        if ( content != null )
        {
            return content;
        }

        if ( contentExists( contentSelector ) )
        {
            throw forbidden( "You don't have permission to access [%s]", contentPath.toString() );
        }
        else
        {
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

    protected PageTemplate getPageTemplate( final Page page )
    {
        if ( page.getTemplate() == null )
        {
            throw notFound( "No template set for content" );
        }

        final PageTemplate pageTemplate = this.services.getPageTemplateService().getByKey( page.getTemplate() );
        if ( pageTemplate == null )
        {
            throw notFound( "Page template [%s] not found", page.getTemplate() );
        }

        return pageTemplate;
    }

    protected final PageTemplate getDefaultPageTemplate( final ContentTypeName contentType, final Site site )
    {
        final GetDefaultPageTemplateParams getDefPageTemplate = GetDefaultPageTemplateParams.create().
            site( site.getId() ).
            contentType( contentType ).
            build();
        final PageTemplate pageTemplate = this.services.getPageTemplateService().getDefault( getDefPageTemplate );
        if ( pageTemplate == null && this.mode != RenderMode.EDIT )
        {
            // we can render default empty page in Live-Edit, for selecting controller when page customized
            throw notFound( "No template found for content" );
        }

        return pageTemplate;
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.services.getContentService().getByPath( contentPath );
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
            return this.services.getContentService().getById( contentId );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private boolean contentExists( final String contentSelector )
    {
        final ContentId contentId = ContentId.from( contentSelector.substring( 1 ) );
        final ContentPath contentPath = ContentPath.from( contentSelector ).asAbsolute();
        final ContentService contentService = this.services.getContentService();
        return contentService.contentExists( contentId ) || contentService.contentExists( contentPath );
    }

    protected final PageDescriptor getPageDescriptor( final PageTemplate pageTemplate )
    {
        final PageDescriptor pageDescriptor = this.services.getPageDescriptorService().getByKey( pageTemplate.getController() );
        if ( pageDescriptor == null )
        {
            throw notFound( "Page descriptor for template [%s] not found", pageTemplate.getName() );
        }

        return pageDescriptor;
    }
}
