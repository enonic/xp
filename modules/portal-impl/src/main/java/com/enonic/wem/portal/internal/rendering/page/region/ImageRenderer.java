package com.enonic.wem.portal.internal.rendering.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.region.ImageComponent;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.wem.portal.internal.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    private PortalUrlService urlService;

    @Override
    public Class<ImageComponent> getType()
    {
        return ImageComponent.class;
    }

    @Override
    public RenderResult render( final ImageComponent component, final PortalContext context )
    {
        final RenderMode renderMode = getRenderingMode( context );
        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setPostProcess( false );

        final StringBuilder s = new StringBuilder();
        if ( renderMode == RenderMode.EDIT )
        {
            s.append( "<div " + PORTAL_COMPONENT_ATTRIBUTE + "=\"" ).append( component.getType().toString() ).append( "\">" );
        }

        if ( component.getImage() != null )
        {
            final String imageUrl = buildUrl( context, component.getImage() );
            s.append( "<figure>" );
            s.append( "<img style=\"display: block; width: 100%\" src=\"" ).append( imageUrl ).append( "\"/>" );
            if ( component.hasCaption() )
            {
                s.append( "<figcaption>" ).append( component.getCaption() ).append( "</figcaption>" );
            }
            s.append( "</figure>" );

        }
        if ( renderMode == RenderMode.EDIT )
        {
            s.append( "</div>" );
        }
        response.setBody( s.toString() );
        return new PortalResponseSerializer( response ).serialize();
    }

    private String buildUrl( final PortalContext context, final ContentId id )
    {
        final ImageUrlParams params = new ImageUrlParams().context( context );
        params.id( id.toString() );
        params.filter( "scalewidth(500)" );
        return this.urlService.imageUrl( params );
    }

    private RenderMode getRenderingMode( final PortalContext context )
    {
        return context == null ? RenderMode.LIVE : context.getMode();
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
