package com.enonic.wem.portal.internal.content.page;


import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.url.ImageUrlBuilder;
import com.enonic.wem.portal.url.PortalUrlBuilders;

public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    @Override
    public Class<ImageComponent> getType()
    {
        return ImageComponent.class;
    }

    @Override
    public RenderResult render( final ImageComponent component, final JsContext context )
    {
        final RenderingMode renderingMode = getRenderingMode( context );
        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setPostProcess( false );

        final StringBuilder s = new StringBuilder();
        if ( renderingMode == RenderingMode.EDIT )
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
        if ( renderingMode == RenderingMode.EDIT )
        {
            s.append( "</div>" );
        }
        response.setBody( s.toString() );
        return new JsHttpResponseSerializer( response ).serialize();
    }

    private RenderingMode getRenderingMode( final JsContext context )
    {
        final PortalRequest req = context.getRequest();
        return req == null ? RenderingMode.LIVE : req.getMode();
    }
}
