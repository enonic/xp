package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.url.ImageUrlBuilder;
import com.enonic.xp.portal.url.PortalUrlBuilders;

public final class ImageRenderer
    implements Renderer<ImageComponent, PortalContext>
{
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
            s.append( "<div data-live-edit-type=\"" ).append( component.getType().toString() ).append( "\">" );
        }

        if ( component.getImage() != null )
        {
            final PortalUrlBuilders portalUrlBuilders = new PortalUrlBuilders( context );
            final ImageUrlBuilder imageUrlBuilder = portalUrlBuilders.createImageByIdUrl( component.getImage() );
            imageUrlBuilder.filter( "scalewidth(500)" );
            final String imageUrl = imageUrlBuilder.toString();
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

    private RenderMode getRenderingMode( final PortalContext context )
    {
        final PortalRequest req = context.getRequest();
        return req == null ? RenderMode.LIVE : req.getMode();
    }
}
