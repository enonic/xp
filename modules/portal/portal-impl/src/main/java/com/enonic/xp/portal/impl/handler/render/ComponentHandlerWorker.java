package com.enonic.xp.portal.impl.handler.render;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.FragmentPageResolver;
import com.enonic.xp.portal.impl.rendering.Renderer;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

final class ComponentHandlerWorker
    extends RenderHandlerWorker
{
    protected ComponentPath componentPath;

    protected RendererFactory rendererFactory;

    protected PostProcessor postProcessor;

    public ComponentHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final Content content = getContent( getContentSelector() );
        final Site site = getSite( content );

        final PageTemplate pageTemplate;
        final DescriptorKey pageController;
        Component component = null;

        if ( content.isPageTemplate() )
        {
            // content is a page-template
            pageTemplate = (PageTemplate) content;
            pageController = pageTemplate.getController();
        }
        else if ( !content.hasPage() )
        {
            // content without page -> use default page-template
            pageTemplate = getDefaultPageTemplate( content.getType(), site );
            pageController = pageTemplate.getController();
        }
        else if ( content.getPage().hasDescriptor() )
        {
            // content with controller set but no page-template (customized)
            pageTemplate = null;
            pageController = content.getPage().getDescriptor();
        }
        else if ( content.getType().isFragment() )
        {
            // fragment content, try resolving component path in Layout fragment
            pageTemplate = null;
            pageController = null;
            final Component fragmentComponent = content.getPage().getFragment();
            if ( this.componentPath.isEmpty() )
            {
                component = fragmentComponent;
            }
            else if ( fragmentComponent instanceof LayoutComponent )
            {
                component = ( (LayoutComponent) fragmentComponent ).getComponent( this.componentPath );
            }
        }
        else
        {
            // content with page-template assigned
            final Page page = getPage( content );
            pageTemplate = getPageTemplate( page );
            pageController = pageTemplate.getController();
        }

        Page effectivePage = new EffectivePageResolver( content, pageTemplate ).resolve();

        if ( component == null )
        {
            effectivePage = inlineFragments( effectivePage, this.componentPath );
            component = effectivePage.getRegions().getComponent( this.componentPath );
        }

        if ( component == null )
        {
            throw notFound( "Page component for [%s] not found", this.componentPath );
        }

        final Content effectiveContent = Content.create( content ).
            page( effectivePage ).
            build();

        this.request.setSite( site );
        this.request.setContent( effectiveContent );
        this.request.setComponent( component );
        this.request.setApplicationKey( pageController != null ? pageController.getApplicationKey() : null );
        this.request.setPageTemplate( pageTemplate );
        this.request.setPageDescriptor( null );

        final Renderer<Component> renderer = this.rendererFactory.getRenderer( component );
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "componentPath", component.getPath() );
            trace.put( "type", component.getType().toString() );
        }
        final PortalResponse response = renderer.render( component, this.request );
        return this.postProcessor.processResponseInstructions( this.request, response );
    }

    private Page inlineFragments( Page page, final ComponentPath componentPath )
    {
        // traverse page based on componentPath, inline fragments components if found
        final List<ComponentPath.RegionAndComponent> partialComponentPathParts = new ArrayList<>();

        for ( ComponentPath.RegionAndComponent pathPart : componentPath )
        {
            partialComponentPathParts.add( pathPart );
            final ComponentPath path = new ComponentPath( ImmutableList.copyOf( partialComponentPathParts ) );
            final Component component = page.getRegions().getComponent( path );

            if ( component == null )
            {
                break;
            }

            if ( component instanceof FragmentComponent )
            {
                final FragmentComponent fragment = (FragmentComponent) component;
                final Component fragmentComponent = getFragmentComponent( fragment );
                if ( fragmentComponent == null )
                {
                    break;
                }
                page = new FragmentPageResolver().inlineFragmentInPage( page, fragmentComponent, path );
            }
        }
        return page;
    }

    private Component getFragmentComponent( final FragmentComponent component )
    {
        final ContentId contentId = component.getFragment();
        if ( contentId == null )
        {
            return null;
        }

        try
        {
            final Content fragmentContent = contentService.getById( contentId );
            if ( !fragmentContent.hasPage() || !fragmentContent.getType().isFragment() )
            {
                return null;
            }
            final Page page = fragmentContent.getPage();
            return page.getFragment();
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

}
