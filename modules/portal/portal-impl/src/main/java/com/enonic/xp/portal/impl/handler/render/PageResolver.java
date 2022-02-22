package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public class PageResolver
{
    private final PageTemplateService pageTemplateService;

    public PageResolver( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    public PageResolverResult resolve( final RenderMode mode, final Content content, final Site site )
    {
        final Page page = content.getPage();
        final PageTemplate pageTemplate;
        final DescriptorKey controller;
        final Page effectivePage;

        if ( content instanceof PageTemplate )
        {
            pageTemplate = (PageTemplate) content;
            controller = getControllerFromTemplate( pageTemplate, mode );
            effectivePage = pageTemplate.getPage();
        }
        else if ( page != null )
        {
            if ( page.getFragment() != null )
            {
                controller = null;
                effectivePage = page;
            }
            else if ( page.getDescriptor() != null )
            {
                controller = page.getDescriptor();
                effectivePage = page;
            }
            else if ( page.getTemplate() != null )
            {
                pageTemplate = getPageTemplateOrFindDefault( page.getTemplate(), content.getType(), site.getPath() );

                if ( pageTemplate != null )
                {
                    controller = getControllerFromTemplate( pageTemplate, mode );
                    effectivePage = mergePageFromPageTemplate( pageTemplate, page );
                }
                else
                {
                    controller = errorOrNull( null, mode,
                                              String.format( "Template [%s] is missing and no default template found for content",
                                                             page.getTemplate() ) );
                    effectivePage = page;
                }
            }
            else
            {
                controller = errorOrNull( null, mode, "Content page has neither template nor descriptor" );
                effectivePage = page;
            }
        }
        else
        {
            pageTemplate = this.pageTemplateService.getDefault(
                GetDefaultPageTemplateParams.create().sitePath( site.getPath() ).contentType( content.getType() ).build() );

            if ( pageTemplate != null )
            {
                controller = getControllerFromTemplate( pageTemplate, mode );
                effectivePage = mergePageFromPageTemplate( pageTemplate, null );
            }
            else
            {
                controller = errorOrNull( null, mode, "No default template found for content" );
                effectivePage = null;
            }
        }

        return new PageResolverResult( effectivePage, controller );
    }

    private PageTemplate getPageTemplateOrFindDefault( final PageTemplateKey pageTemplate, final ContentTypeName contentType,
                                                       final ContentPath sitePath )
    {
        try
        {
            return this.pageTemplateService.getByKey( pageTemplate );
        }
        catch ( ContentNotFoundException e )
        {
            return this.pageTemplateService.getDefault(
                GetDefaultPageTemplateParams.create().sitePath( sitePath ).contentType( contentType ).build() );
        }
    }

    private static DescriptorKey getControllerFromTemplate( final PageTemplate pageTemplate, final RenderMode mode )
    {
        return errorOrNull( pageTemplate.getController(), mode,
                            String.format( "Template [%s] has no page descriptor", pageTemplate.getName() ) );
    }

    private static <T> T errorOrNull( final T object, final RenderMode mode, final String message )
    {
        if ( object == null )
        {
            switch ( mode )
            {
                case EDIT:
                    // we can render default empty page in Live-Edit, for selecting controller when page customized
                    return null;
                case INLINE:
                case PREVIEW:
                    throw new WebException( HttpStatus.IM_A_TEAPOT, message );
                default:
                    throw new WebException( HttpStatus.INTERNAL_SERVER_ERROR, message );
            }
        }
        else
        {
            return object;
        }
    }

    private static Page mergePageFromPageTemplate( final PageTemplate pageTemplate, final Page page )
    {
        final Page templatePage = pageTemplate.getPage();
        final PageTemplateKey templateKey = pageTemplate.getKey();
        if ( templatePage != null )
        {
            final Page.Builder pageBuilder = Page.create( templatePage ).descriptor( null ).template( templateKey );

            if ( page != null )
            {
                if ( page.getConfig() != null )
                {
                    pageBuilder.config( page.getConfig() );
                }
                if ( page.getRegions() != null )
                {
                    pageBuilder.regions( page.getRegions() );
                }
            }
            return pageBuilder.build();
        }
        else
        {
            return page != null ? page : Page.create().template( templateKey ).build();
        }
    }

}
