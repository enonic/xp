package com.enonic.xp.portal.impl.handler.render;

import java.util.List;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public class PageResolver
{
    private final PageTemplateService pageTemplateService;

    private final PageDescriptorService pageDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    public PageResolver( final PageTemplateService pageTemplateService, final PageDescriptorService pageDescriptorService,
                         final LayoutDescriptorService layoutDescriptorService )
    {
        this.pageTemplateService = pageTemplateService;
        this.pageDescriptorService = pageDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
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
                effectivePage = buildPageFromFragment( page );
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
                    throw newWebException( mode, String.format( "Template [%s] is missing and no default template found for content",
                                                                page.getTemplate() ) );
                }
            }
            else
            {
                throw newWebException( mode, "Content page has neither template nor descriptor" );
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
                throw newWebException( mode, "No default template found for content" );
            }
        }

        if ( controller == null )
        {
            return new PageResolverResult( effectivePage, null, null );
        }

        return buildPageWithRegionsFromController( effectivePage, controller );
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
        final DescriptorKey controller = pageTemplate.getController();

        if ( controller != null )
        {
            return controller;
        }
        else
        {
            throw newWebException( mode, String.format( "Template [%s] has no page descriptor", pageTemplate.getName() ) );
        }
    }

    private static WebException newWebException( final RenderMode mode, final String message )
    {
        throw new WebException( mode == RenderMode.INLINE || mode == RenderMode.EDIT ? HttpStatus.IM_A_TEAPOT : HttpStatus.NOT_FOUND,
                                message );
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

    private Page buildPageFromFragment( final Page effectivePage )
    {
        final Component fragmentComponent = effectivePage.getFragment();

        if ( fragmentComponent instanceof LayoutComponent )
        {
            final Page.Builder pageBuilder = Page.create( effectivePage );
            pageBuilder.fragment( processLayoutComponent( (LayoutComponent) fragmentComponent ) );
            return pageBuilder.build();
        }

        return effectivePage;
    }

    private PageResolverResult buildPageWithRegionsFromController( final Page effectivePage, final DescriptorKey controller )
    {
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( controller );

        if ( pageDescriptor == null || pageDescriptor.getModifiedTime() == null )
        {
            return new PageResolverResult( effectivePage, controller, pageDescriptor );
        }

        final Page resultingPage = buildPageWithRegions( effectivePage, pageDescriptor );

        return new PageResolverResult( resultingPage, controller, pageDescriptor );
    }

    private Page buildPageWithRegions( final Page sourcePage, final PageDescriptor pageDescriptor )
    {
        final Page.Builder pageBuilder = Page.create( sourcePage );
        final PageRegions.Builder pageRegionsBuilder = PageRegions.create();

        if ( pageDescriptor.getRegions() != null )
        {
            pageDescriptor.getRegions().forEach( regionDescriptor -> {
                pageRegionsBuilder.add( getOrCreatePageRegion( regionDescriptor, sourcePage ) );
            } );
        }

        return pageBuilder.regions( pageRegionsBuilder.build() ).build();
    }

    private Region getOrCreatePageRegion( final RegionDescriptor regionDescriptor, final Page sourcePage )
    {
        final Region existingRegion = sourcePage.getRegion( regionDescriptor.getName() );

        return existingRegion == null
            ? Region.create().name( regionDescriptor.getName() ).build()
            : processExistingPageRegion( existingRegion );
    }

    private Region processExistingPageRegion( final Region existingRegion )
    {
        final Region.Builder builder = Region.create( existingRegion );
        final List<Component> components = existingRegion.getComponents();

        for ( int i = 0; i < components.size(); i++)
        {
            builder.set( i, processComponent( components.get( i ) ) );
        }

        return builder.build();
    }

    private Component processComponent( final Component component )
    {
        if ( component instanceof LayoutComponent )
        {
            return processLayoutComponent( (LayoutComponent) component );
        }
        else
        {
            return component;
        }
    }

    private LayoutComponent processLayoutComponent( final LayoutComponent component )
    {
        final LayoutDescriptor layoutDescriptor =
            component.hasDescriptor() ? layoutDescriptorService.getByKey( component.getDescriptor() ) : null;

        if ( layoutDescriptor == null || layoutDescriptor.getModifiedTime() == null )
        {
            return component;
        }

        return buildLayoutWithRegions( component, layoutDescriptor );
    }

    private LayoutComponent buildLayoutWithRegions( final LayoutComponent existingLayout, final LayoutDescriptor layoutDescriptor )
    {
        final LayoutComponent.Builder layoutBuilder = LayoutComponent.create( existingLayout );
        final LayoutRegions.Builder regionsBuilder = LayoutRegions.create();

        if ( layoutDescriptor.getRegions() != null )
        {
                layoutDescriptor.getRegions().forEach( region -> {
                final Region existingRegion = existingLayout.getRegion( region.getName() );
                final Region regionToAdd = existingRegion == null ? Region.create().name( region.getName() ).build() : existingRegion;
                regionsBuilder.add( regionToAdd );
            } );
        }

        return layoutBuilder.regions( regionsBuilder.build() ).build();
    }

}
