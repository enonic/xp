package com.enonic.xp.portal.impl.rendering;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.html.HtmlEscapers;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.ImageComponent;

@Component(immediate = true, service = Renderer.class)
public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    private static final String EMPTY_IMAGE_HTML = "<figure " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></figure>";

    private static final String COMPONENT_PLACEHOLDER_ERROR_HTML = "<div " + RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE +
        "=\"{0}\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">{1}</span></div>";

    private static final Logger LOG = LoggerFactory.getLogger( ImageRenderer.class );

    private PortalUrlService urlService;

    private ContentService contentService;

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

        private static final String ERROR_IMAGE_NOT_FOUND = "Image could not be found";

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
                    return renderOkResponse( generateImageHtml() );
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
            final StringBuilder html = new StringBuilder();

            appendFigureTag( html );

            return html.toString();
        }

        private void appendFigureTag( final StringBuilder html )
        {
            openFigureTag( html );
            appendImageTag( html );
            appendCaptionTag( html );
            closeFigureTag( html );
        }

        private void openFigureTag( final StringBuilder html )
        {
            final String type = component.getType().toString();
            html.append( "<figure " ).append( RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE ).append( "=\"" ).append( type ).append(
                "\">" );
        }

        private void appendImageTag( final StringBuilder html )
        {
            html.append( "<img style=\"width: 100%\" src=\"" ).append( buildUrl() ).append( "\" " );

            appendAltAttribute( html );

            html.append( "/>" );
        }

        private void appendAltAttribute( final StringBuilder html )
        {
            final String altText = getImageAlternativeText();

            if ( altText != null )
            {
                html.append( "alt=\"" ).append( altText ).append( "\"" );
            }
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

        private void appendCaptionTag( final StringBuilder html )
        {
            if ( component.hasCaption() )
            {
                html.append( "<figcaption>" ).append( component.getCaption() ).append( "</figcaption>" );
            }
        }

        private void closeFigureTag( final StringBuilder html )
        {
            html.append( "</figure>" );
        }

        private PortalResponse renderOkResponse( final String html )
        {
            return PortalResponse.create().body( html ).contentType( MediaType.HTML_UTF_8 ).postProcess( false ).build();
        }

        private PortalResponse renderResponseImageNotFound()
        {
            LOG.warn( "Image content could not be found. ContentId: " + component.getImage().toString() );

            final RenderMode renderMode = portalRequest.getMode();

            if ( renderMode == RenderMode.EDIT )
            {
                return renderErrorResponse();
            }

            final String componentType = component.getType().toString();

            return renderOkResponse( MessageFormat.format( EMPTY_IMAGE_HTML, componentType ) );
        }

        private PortalResponse renderResponseNoImage()
        {
            final RenderMode renderMode = portalRequest.getMode();

            if ( renderMode == RenderMode.EDIT )
            {
                final String componentType = component.getType().toString();

                return renderOkResponse( MessageFormat.format( EMPTY_IMAGE_HTML, componentType ) );
            }

            return renderOkResponse( "" );
        }

        private PortalResponse renderErrorResponse()
        {
            final String escapedMessage = HtmlEscapers.htmlEscaper().escape( ERROR_IMAGE_NOT_FOUND );
            final String html = MessageFormat.format( COMPONENT_PLACEHOLDER_ERROR_HTML, component.getType().toString(), escapedMessage );

            return PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).postProcess( false ).body( html ).build();
        }
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
