package com.enonic.xp.portal.impl.rendering;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.html.HtmlBuilder;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, service = Renderer.class)
public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageRenderer.class );

    private final PortalUrlService urlService;

    private final ContentService contentService;

    @Activate
    public ImageRenderer( @Reference final PortalUrlService urlService, @Reference final ContentService contentService )
    {
        this.urlService = urlService;
        this.contentService = contentService;
    }

    @Override
    public Class<ImageComponent> getType()
    {
        return ImageComponent.class;
    }

    @Override
    public PortalResponse render( final ImageComponent component, final PortalRequest portalRequest )
    {
        return new ImageComponentRenderer( component, portalRequest ).render();
    }

    private final class ImageComponentRenderer
    {
        private final ImageComponent component;

        private final PortalRequest portalRequest;

        private ImageComponentRenderer( final ImageComponent component, final PortalRequest portalRequest )
        {
            this.component = component;
            this.portalRequest = portalRequest;
        }

        private PortalResponse render()
        {
            try
            {
                if ( component.hasImage() )
                {
                    return renderResponse( generateImageHtml(), HttpStatus.OK );
                }
                return renderResponseNoImage();
            }
            catch ( ContentNotFoundException ex )
            {
                return renderResponseImageNotFound();
            }
        }

        private String generateImageHtml()
        {
            final HtmlBuilder htmlBuilder = new HtmlBuilder();

            htmlBuilder.open( "figure" )
                .attribute( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE, component.getType().toString() )
                .open( "img" )
                .attribute( "style", "width: 100%" )
                .attribute( "src", buildUrl() );

            final String altText = getImageAlternativeText();

            if ( altText != null )
            {
                htmlBuilder.attribute( "alt", altText );
            }
            htmlBuilder.closeEmpty();

            if ( component.hasCaption() )
            {
                htmlBuilder.open( "figcaption" ).escapedText( component.getCaption() ).close();
            }

            htmlBuilder.close();

            return htmlBuilder.toString();
        }

        private String buildUrl()
        {
            final ImageUrlParams params =
                new ImageUrlParams().portalRequest( portalRequest ).id( component.getImage().toString() ).scale( "width(768)" );

            return urlService.imageUrl( params );
        }

        private String getImageAlternativeText()
        {
            final Content image = contentService.getById( component.getImage() );

            final String altText = image.getData().getString( "altText" );

            if ( altText != null && !altText.isEmpty() )
            {
                return altText;
            }

            return getImageAttachmentName( image );
        }

        private String getImageAttachmentName( final Content image )
        {
            if ( !( image instanceof Media ) )
            {
                return null;
            }

            final Attachment attachment = ( (Media) image ).getMediaAttachment();

            if ( attachment != null )
            {
                return attachment.getName();
            }

            return null;
        }

        private PortalResponse renderResponse( final String html, final HttpStatus status )
        {
            return PortalResponse.create().body( html ).contentType( MediaType.HTML_UTF_8 ).status( status ).postProcess( false ).build();
        }

        private PortalResponse renderResponseImageNotFound()
        {
            LOG.warn( "Image content could not be found. ContentId: {}", component.getImage() );

            final RenderMode renderMode = portalRequest.getMode();

            if ( renderMode == RenderMode.EDIT )
            {
                return renderErrorResponse();
            }

            return renderEmptyFigure( HttpStatus.NOT_FOUND );
        }

        private PortalResponse renderResponseNoImage()
        {
            final RenderMode renderMode = portalRequest.getMode();

            if ( renderMode == RenderMode.EDIT )
            {
                return renderEmptyFigure( HttpStatus.OK );
            }

            return renderResponse( "", HttpStatus.NOT_FOUND );
        }

        private PortalResponse renderEmptyFigure( final HttpStatus status )
        {
            final String html = new HtmlBuilder().open( "figure" )
                .attribute( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE, component.getType().toString() )
                .text( "" )
                .close()
                .toString();
            return renderResponse( html, status );
        }

        private PortalResponse renderErrorResponse()
        {
            final String html = new HtmlBuilder().open( "div" )
                .attribute( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE, component.getType().toString() )
                .attribute( "data-portal-placeholder", "true" )
                .attribute( "data-portal-placeholder-error", "true" )
                .open( "span" )
                .attribute( "class", "data-portal-placeholder-error" )
                .text( "Image could not be found" )
                .close()
                .close()
                .toString();

            return PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).postProcess( false ).body( html ).build();
        }
    }
}
