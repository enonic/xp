package com.enonic.xp.portal.impl.handler.render;

import java.util.List;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.schema.content.ContentTypeName;

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

    public PageResolverResult resolve( final Content content, final ContentPath sitePath )
    {
        if ( content instanceof PageTemplate )
        {
            final PageTemplate pageTemplate = (PageTemplate) content;
            final DescriptorKey controller = pageTemplate.getController();
            return controller == null
                ? noPageInTemplateResult( pageTemplate )
                : buildPageWithRegionsFromController( pageTemplate.getPage(), controller );
        }
        else if ( content.getType().isShortcut() )
        {
            return PageResolverResult.errorResult( "Shortcut" );
        }

        final Page page = content.getPage();
        if ( page != null )
        {
            if ( page.getFragment() != null )
            {
                return new PageResolverResult( buildPageFromFragment( page ), null, null );
            }

            if ( page.getDescriptor() != null )
            {
                final DescriptorKey controller = page.getDescriptor();
                return buildPageWithRegionsFromController( page, controller );
            }

            if ( page.getTemplate() != null )
            {
                final PageTemplate pageTemplate = getPageTemplateOrFindDefault( page.getTemplate(), content.getType(), sitePath );

                if ( pageTemplate != null )
                {
                    return buildPageFromTemplate( page, pageTemplate );
                }
                else
                {
                    return PageResolverResult.errorResult(
                        String.format( "Template [%s] is missing and no default template found for content", page.getTemplate() ) );
                }
            }
            else
            {
                return PageResolverResult.errorResult( "Content page has neither template nor descriptor" );
            }
        }
        else
        {
            final PageTemplate pageTemplate = this.pageTemplateService.getDefault(
                GetDefaultPageTemplateParams.create().sitePath( sitePath ).contentType( content.getType() ).build() );

            if ( pageTemplate != null )
            {
                return buildPageFromTemplate( null, pageTemplate );
            }
            else
            {
                return PageResolverResult.errorResult( "No default template found for content" );
            }
        }
    }

    private PageResolverResult buildPageFromTemplate( final Page page, final PageTemplate pageTemplate )
    {
        final DescriptorKey controller = pageTemplate.getController();
        return controller == null
            ? noPageInTemplateResult( pageTemplate )
            : buildPageWithRegionsFromController( mergePageFromPageTemplate( pageTemplate, page ), controller );
    }

    private static PageResolverResult noPageInTemplateResult( final PageTemplate pageTemplate )
    {
        return PageResolverResult.errorResult( String.format( "Template [%s] has no page descriptor", pageTemplate.getName() ) );
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

    private static Page mergePageFromPageTemplate( final PageTemplate pageTemplate, final Page page )
    {
        final Page templatePage = pageTemplate.getPage();
        final PageTemplateKey templateKey = pageTemplate.getKey();
        if ( templatePage != null )
        {
            final Page.Builder pageBuilder = Page.create( templatePage );

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

    private Page buildPageFromFragment( final Page page )
    {
        final Component fragmentComponent = page.getFragment();

        if ( fragmentComponent instanceof LayoutComponent )
        {
            return Page.create( page ).fragment( processLayoutComponent( (LayoutComponent) fragmentComponent ) ).build();
        }

        return page;
    }

    private PageResolverResult buildPageWithRegionsFromController( final Page page, final DescriptorKey controller )
    {
        final PageDescriptor pageDescriptor = pageDescriptorService.getByKey( controller );
        final Page resultingPage =
            pageDescriptor == null || pageDescriptor.getModifiedTime() == null ? page : buildPageWithRegions( page, pageDescriptor );
        return new PageResolverResult( resultingPage, controller.getApplicationKey(), pageDescriptor );
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

        for ( int i = 0; i < components.size(); i++ )
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
