package com.enonic.xp.portal.impl.resource.render;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.GetDefaultPageTemplateParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public abstract class RenderResource
    extends BaseSubResource
{
    protected final Site getSite( final Content content )
    {
        final Site site = this.services.getContentService().getNearestSite( content.getId() );

        if ( site == null )
        {
            throw notFound( "Site for content [%s] not found", content.getPath() );
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

        throw notFound( "Page [%s] not found", contentPath.toString() );
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
        return this.services.getPageTemplateService().getDefault(
            GetDefaultPageTemplateParams.create().site( site.getId() ).contentType( contentType ).build() );
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
        catch ( ContentNotFoundException e )
        {
            return null;
        }
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
