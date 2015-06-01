package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.page.region.ImageComponent;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = Renderer.class)
public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    private static final String EMPTY_IMAGE_HTML = "<figure " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></figure>";

    private PortalUrlService urlService;

    @Override
    public Class<ImageComponent> getType()
    {
        return ImageComponent.class;
    }

    @Override
    public RenderResult render( final ImageComponent component, final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final RenderMode renderMode = getRenderingMode( portalRequest );
        portalResponse.setContentType( "text/html" );
        portalResponse.setPostProcess( false );

        final StringBuilder html = new StringBuilder();

        final String type = component.getType().toString();
        if ( component.getImage() != null )
        {
            final String imageUrl = buildUrl( portalRequest, component.getImage() );
            html.append( "<figure " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"" + type + "\">" );
            html.append( "<img style=\"width: 100%\" src=\"" ).append( imageUrl ).append( "\"/>" );
            if ( component.hasCaption() )
            {
                html.append( "<figcaption>" ).append( component.getCaption() ).append( "</figcaption>" );
            }
            html.append( "</figure>" );

        }
        else if ( renderMode == RenderMode.EDIT )
        {
            html.append( MessageFormat.format( EMPTY_IMAGE_HTML, type ) );
        }

        portalResponse.setBody( html.toString() );
        return new PortalResponseSerializer( portalResponse ).serialize();
    }

    private String buildUrl( final PortalRequest portalRequest, final ContentId id )
    {
        final ImageUrlParams params = new ImageUrlParams().portalRequest( portalRequest );
        params.id( id.toString() );
        params.scale( "width(500)" );
        return this.urlService.imageUrl( params );
    }

    private RenderMode getRenderingMode( final PortalRequest portalRequest )
    {
        return portalRequest == null ? RenderMode.LIVE : portalRequest.getMode();
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
