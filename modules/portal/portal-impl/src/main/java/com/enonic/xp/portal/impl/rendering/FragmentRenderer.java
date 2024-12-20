package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.html.HtmlEscapers;
import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;

public final class FragmentRenderer
{
    private static final String EMPTY_FRAGMENT_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String EDIT_MODE_FRAGMENT_WRAPPER_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\">{1}</div>";

    private static final String COMPONENT_PLACEHOLDER_ERROR_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE +
        "=\"{0}\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">{1}</span></div>";

    private static final Logger LOG = LoggerFactory.getLogger( FragmentRenderer.class );

    private final ContentService contentService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final RendererDelegate rendererDelegate;

    private final FragmentPageResolver fragmentPageResolver = new FragmentPageResolver();

    public FragmentRenderer( final ContentService contentService, final LayoutDescriptorService layoutDescriptorService,
                             final RendererDelegate rendererDelegate )
    {
        this.contentService = contentService;
        this.rendererDelegate = rendererDelegate;
        this.layoutDescriptorService = layoutDescriptorService;
    }

    public PortalResponse render( final FragmentComponent component, final PortalRequest portalRequest )
    {
        final RenderMode renderMode = portalRequest.getMode();
        final String type = component.getType().toString();

        if ( component.getFragment() == null )
        {
            return renderEmptyFragment( renderMode, component );
        }

        final Component fragmentComponent = getFragmentComponent( component );
        if ( fragmentComponent == null )
        {
            LOG.warn( "Fragment content could not be found. ContentId: " + component.getFragment().toString() );

            if ( renderMode == RenderMode.EDIT )
            {
                final String errorMessage = "Fragment content could not be found";
                return renderErrorComponentPlaceHolder( component, errorMessage );
            }
            else
            {
                return renderEmptyFragment( renderMode, component );
            }
        }

        // replace resolved fragment in current PortalRequest Page
        final Page sourcePage = portalRequest.getContent().getPage();
        final Page page = fragmentPageResolver.inlineFragmentInPage( sourcePage, fragmentComponent, component.getPath() );
        final Content content = Content.create( portalRequest.getContent() ).page( page ).build();
        portalRequest.setContent( content );

        final PortalResponse fragmentResponse = rendererDelegate.render( fragmentComponent, portalRequest );
        if ( renderMode == RenderMode.EDIT && fragmentResponse != null )
        {
            if ( !( fragmentResponse.getBody() instanceof String ) ||
                !fragmentResponse.getContentType().is( MediaType.HTML_UTF_8.withoutParameters() ) )
            {
                return fragmentResponse;
            }

            final String body = (String) fragmentResponse.getBody();

            final String noMethodErrorMessage = "No method provided to handle request";

            if ( body.contains( noMethodErrorMessage ) )
            {
                return renderErrorComponentPlaceHolder( component, noMethodErrorMessage );
            }

            return wrapFragmentForEditMode( fragmentResponse, type );
        }
        return fragmentResponse;
    }

    private PortalResponse wrapFragmentForEditMode( final PortalResponse response, final String type )
    {
        final String body = (String) response.getBody();
        final String wrappedBody = MessageFormat.format( EDIT_MODE_FRAGMENT_WRAPPER_HTML, type, body );
        return PortalResponse.create( response ).body( wrappedBody ).build();
    }

    private Component getFragmentComponent( final FragmentComponent component )
    {
        try
        {
            final Content fragmentContent = contentService.getById( component.getFragment() );
            return getFragmentFromContent( fragmentContent );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Component getFragmentFromContent( final Content fragmentContent )
    {
        if ( fragmentContent.getType().isFragment() || fragmentContent.getPage() != null )
        {
            return processFragmentComponent( fragmentContent.getPage().getFragment() );
        }

        return null;
    }

    private Component processFragmentComponent( final Component fragmentComponent )
    {
        if ( fragmentComponent instanceof LayoutComponent )
        {
            return processLayoutComponent( (LayoutComponent) fragmentComponent );
        }

        return fragmentComponent;
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

    private PortalResponse renderEmptyFragment( final RenderMode renderMode, final FragmentComponent component )
    {
        final String type = component.getType().toString();
        final String html = renderMode == RenderMode.EDIT ? MessageFormat.format( EMPTY_FRAGMENT_HTML, type ) : "";
        return PortalResponse.create().body( html ).contentType( MediaType.HTML_UTF_8 ).postProcess( false ).build();
    }

    private PortalResponse renderErrorComponentPlaceHolder( final FragmentComponent component, final String errorMessage )
    {
        final String escapedMessage = HtmlEscapers.htmlEscaper().escape( errorMessage );
        final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_ERROR_HTML, component.getType().toString(), escapedMessage );
        return PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).postProcess( false ).body( html ).build();
    }
}
