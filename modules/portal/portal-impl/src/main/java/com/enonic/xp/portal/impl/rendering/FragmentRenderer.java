package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.html.HtmlEscapers;
import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;

@org.osgi.service.component.annotations.Component(immediate = true, service = Renderer.class)
public final class FragmentRenderer
    implements Renderer<FragmentComponent>
{
    private static final String EMPTY_FRAGMENT_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></div>";

    private static final String EDIT_MODE_FRAGMENT_WRAPPER_HTML =
        "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\">{1}</div>";

    private static final String COMPONENT_PLACEHOLDER_ERROR_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE +
        "=\"{0}\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">{1}</span></div>";

    private final static Logger LOG = LoggerFactory.getLogger( FragmentRenderer.class );

    private ContentService contentService;

    private RendererFactory rendererFactory;

    private FragmentPageResolver fragmentPageResolver = new FragmentPageResolver();

    @Override
    public Class<FragmentComponent> getType()
    {
        return FragmentComponent.class;
    }

    @Override
    public PortalResponse render( final FragmentComponent component, final PortalRequest portalRequest )
    {
        final RenderMode renderMode = getRenderingMode( portalRequest );
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

        final Renderer<Component> renderer = this.rendererFactory.getRenderer( fragmentComponent );
        if ( renderer == null )
        {
            throw new RenderException( "No Renderer found for: " + fragmentComponent.getClass().getSimpleName() );
        }

        // replace resolved fragment in current PortalRequest Page
        final Page sourcePage = portalRequest.getContent().getPage();
        final Page page = fragmentPageResolver.inlineFragmentInPage( sourcePage, fragmentComponent, component.getPath() );
        final Content content = Content.create( portalRequest.getContent() ).page( page ).build();
        portalRequest.setContent( content );

        final PortalResponse fragmentResponse = renderer.render( fragmentComponent, portalRequest );
        if ( renderMode == RenderMode.EDIT && fragmentResponse != null )
        {
            if ( !( fragmentResponse.getBody() instanceof String ) || !fragmentResponse.getContentType().is( MediaType.parse( "text/html" ) ) )
            {
                return fragmentResponse;
            }

            final String body = (String) fragmentResponse.getBody();

            final String noMethodErrorMessage = "No method provided to handle request";

            if( body.contains( noMethodErrorMessage ) )
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
        final ContentId contentId = component.getFragment();
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

    private PortalResponse renderEmptyFragment( final RenderMode renderMode, final FragmentComponent component )
    {
        final String type = component.getType().toString();
        final String html = renderMode == RenderMode.EDIT ? MessageFormat.format( EMPTY_FRAGMENT_HTML, type ) : "";
        return PortalResponse.create().body( html ).contentType( MediaType.create( "text", "html" ) ).postProcess( false ).build();

    }

    private PortalResponse renderErrorComponentPlaceHolder( final FragmentComponent component, final String errorMessage )
    {
        final String escapedMessage = HtmlEscapers.htmlEscaper().escape( errorMessage );
        final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_ERROR_HTML, component.getType().toString(), escapedMessage );
        return PortalResponse.create().
            contentType( MediaType.create( "text", "html" ) ).
            postProcess( false ).
            body( html ).
            build();
    }

    private RenderMode getRenderingMode( final PortalRequest portalRequest )
    {
        return portalRequest == null ? RenderMode.LIVE : portalRequest.getMode();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
