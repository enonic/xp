package com.enonic.xp.portal.impl.handler.render;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.rendering.FragmentPageResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;

final class ComponentHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    ComponentPath componentPath;

    RendererDelegate rendererDelegate;

    PostProcessor postProcessor;

    ContentResolver contentResolver;

    ContentService contentService;

    PageResolver pageResolver;

    ComponentHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final ContentResolverResult resolvedContent = contentResolver.resolve( this.request );
        final Content content = resolvedContent.getContentOrElseThrow();
        final Site site = resolvedContent.getNearestSiteOrElseThrow();
        final PageResolverResult resolvedPage = pageResolver.resolve( content, site.getPath() );

        Page effectivePage = resolvedPage.getEffectivePageOrElseThrow( request.getMode() );
        Component component = null;

        if ( content.getType().isFragment() )
        {
            // fragment content, try resolving component path in Layout fragment
            final Component fragmentComponent = effectivePage.getFragment();

            if ( this.componentPath.isEmpty() )
            {
                component = fragmentComponent;
            }
            else if ( fragmentComponent instanceof LayoutComponent )
            {
                component = ( (LayoutComponent) fragmentComponent ).getComponent( this.componentPath );
            }
        }

        if ( component == null )
        {
            effectivePage = inlineFragments( effectivePage, this.componentPath );
            component = effectivePage.getRegions().getComponent( this.componentPath );
        }

        if ( component == null )
        {
            throw WebException.notFound( String.format( "Page component for [%s] not found", this.componentPath ) );
        }

        final Content effectiveContent = Content.create( content ).page( effectivePage ).build();

        this.request.setSite( site );
        this.request.setContent( effectiveContent );
        this.request.setComponent( component );
        this.request.setApplicationKey( resolvedPage.getApplicationKey() );

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "componentPath", component.getPath() );
            trace.put( "type", component.getType().toString() );
        }
        final PortalResponse result = rendererDelegate.render( component, this.request );
        final PortalResponse response = (isEditMode() && result.hasContributions()) ? addHasContributionsHeader( result ) : result;

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
            if ( fragmentContent.getPage() == null || !fragmentContent.getType().isFragment() )
            {
                return null;
            }
            return fragmentContent.getPage().getFragment();
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private boolean isEditMode()
    {
        return request.getMode() == RenderMode.EDIT;
    }

    private PortalResponse addHasContributionsHeader( final PortalResponse source) {
        return PortalResponse.create(source).header( "X-Has-Contributions", "true" ).build();
    }

}
