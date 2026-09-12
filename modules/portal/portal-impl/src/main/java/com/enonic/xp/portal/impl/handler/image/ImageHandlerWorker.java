package com.enonic.xp.portal.impl.handler.image;

import java.io.IOException;
import java.util.Set;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.MediaUtils;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.exception.ThrottlingException;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.portal.impl.HmacService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.handler.AbstractAttachmentHandlerWorker;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public final class ImageHandlerWorker
    extends AbstractAttachmentHandlerWorker<Media>
{
    private static final int DEFAULT_BACKGROUND = 0xFFFFFF;

    private static final int DEFAULT_QUALITY = 85;

    private final ImageService imageService;

    private final HmacService hmacService;

    public String filterParam;

    public String qualityParam;

    public String backgroundParam;

    public ScaleParams scaleParams;

    private String styleParam;

    private ImageStyle style;

    private boolean cacheOnly;

    public ImageHandlerWorker( final WebRequest request, final ContentService contentService, final ImageService imageService, final HmacService hmacService )
    {
        super( request, contentService );
        this.imageService = imageService;
        this.hmacService = hmacService;
    }

    public void setScalePath( final String segment )
    {
        final int separator = segment.indexOf( '~' );
        try
        {
            this.scaleParams = new ScaleParamsParser().parse( separator < 0 ? segment : segment.substring( 0, separator ) );
            this.styleParam = separator < 0 ? null : DescriptorKey.from( segment.substring( separator + 1 ) ).toString();
            if ( styleParam != null && scaleParams == null )
            {
                throw new IllegalArgumentException( "Image scale is required" );
            }
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.badRequest( "Invalid image scale or style", e );
        }
    }

    @Override
    public PortalResponse execute()
        throws IOException
    {
        if ( request.getParams().containsKey( "style" ) )
        {
            throw WebException.badRequest( "Image style must be specified in the scale path segment" );
        }
        if ( styleParam != null )
        {
            if ( Set.of( "scale", "format", "quality", "filter", "background" ).stream()
                    .anyMatch( request.getParams()::containsKey ) )
            {
                throw WebException.badRequest( "Image styles cannot be combined with processing parameters" );
            }
            try
            {
                this.style = requireNonNull( imageService.getStyle( styleParam ), "Image style is required" );
                scaleParams.withAspectRatio( style.getAspectRatio() );
            }
            catch ( IllegalArgumentException e )
            {
                throw WebException.badRequest( "Invalid image style", e );
            }
        }
        return super.execute();
    }

    @Override
    protected boolean shouldBypassTransformation( final MediaType attachmentMimeType )
    {
        if ( style != null )
        {
            // The image service selects and validates the source decoder after the cache lookup.
            return false;
        }
        return super.shouldBypassTransformation( attachmentMimeType );
    }

    @Override
    protected MediaType resolveContentType( final Media content, final MediaType attachmentMimeType )
    {
        if ( shouldConvert( content, name ) )
        {
            final String extension = Files.getFileExtension( name );
            if ( "webp".equalsIgnoreCase( extension ) )
            {
                return MediaType.WEBP;
            }
            if ( "avif".equalsIgnoreCase( extension ) )
            {
                return MediaType.AVIF;
            }
        }
        return super.resolveContentType( content, attachmentMimeType );
    }

    @Override
    protected Attachment resolveAttachment( final Content content, final String name )
    {
        // Validate explicit output extensions before pass-through sources can bypass conversion.
        final String extension = Files.getFileExtension( name );
        if ( style == null && ( "webp".equalsIgnoreCase( extension ) || "avif".equalsIgnoreCase( extension ) ) &&
            shouldConvert( content, name ) )
        {
            throw WebException.badRequest( "WebP and AVIF encoding requires a predefined image style" );
        }
        final Attachment attachment = content.getAttachments().byLabel( "source" );
        if ( attachment == null )
        {
            throw WebException.notFound( String.format( "Attachment [%s] not found", content.getName() ) );
        }
        return attachment;
    }

    @Override
    protected boolean shouldConvert( final Content content, final String name )
    {
        final String contentName = content.getName().toString();
        final boolean result = !contentName.equals( name );
        if ( result && !contentName.equals( Files.getNameWithoutExtension( name ) ) )
        {
            throw WebException.notFound( String.format( "Image [%s] not found for content [%s]", name, content.getId() ) );
        }
        return result;
    }

    @Override
    protected void writeResponseContent( final PortalResponse.Builder portalResponse, final MediaType contentType, final ByteSource body )
    {
        if ( cacheOnly )
        {
            portalResponse.removeHeader( HttpHeaders.CACHE_CONTROL );
        }
        portalResponse.contentType( contentType );
        portalResponse.body( body );
    }

    @Override
    protected Media cast( final Content content )
    {
        if ( !( content instanceof final Media media ) )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", content.getId() ) );
        }

        if ( !( media.getType().isImageMedia() || media.getType().isVectorMedia() ) )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", content.getId() ) );
        }

        return media;
    }

    @Override
    protected ByteSource transform( final Media content, final BinaryReference binaryReference, final MediaType contentType )
        throws IOException
    {
        final PropertySet mediaData = content.getData().getSet( ContentPropertyNames.MEDIA );
        final ImageOrientation imageOrientation = requireNonNullElse(
            MediaUtils.readOrientation( mediaData ), ImageOrientation.TopLeft );

        try
        {
            final int imageQuality =
                nullToEmpty( this.qualityParam ).isEmpty() ? DEFAULT_QUALITY : Integer.parseInt( this.qualityParam );

            final int backgroundColor = nullToEmpty( this.backgroundParam ).isEmpty()
                ? DEFAULT_BACKGROUND
                : Integer.parseInt( this.backgroundParam.startsWith( "0x" ) ? this.backgroundParam.substring( 2 ) : this.backgroundParam,
                                    16 );

            final Attachment attachment =
                requireNonNull( content.getAttachments().byLabel( "source" ), "Media content must have an attachment" );

            final String currentFingerprint = MediaHashResolver.resolveStyledImageHash(
                MediaHashResolver.resolveImageHash( content, MediaHashResolver.resolveAttachmentHash( attachment ) ), style, scaleParams, hmacService );
            final boolean hashMatches = MediaHashResolver.matchesFingerprint( currentFingerprint, fingerprint );
            this.cacheOnly = !hashMatches && ( !nullToEmpty( fingerprint ).isBlank() ||
                contentType.is( MediaType.WEBP ) || contentType.is( MediaType.AVIF ) );

            final ReadImageParams.Builder readImageParams = ReadImageParams.newImageParams()
                .contentId( content.getId() )
                .binaryReference( binaryReference )
                .cropping( MediaUtils.readCropping( mediaData ) )
                .focalPoint( MediaUtils.readFocalPoint( mediaData ) )
                .attachmentSha512( attachment.getSha512() )
                .orientation( imageOrientation )
                .scaleParams( this.scaleParams )
                .mimeType( contentType.toString() )
                .style( styleParam )
                .expectedStyle( style )
                .cacheOnly( cacheOnly );
            if ( style == null )
            {
                readImageParams.filterParam( this.filterParam ).backgroundColor( backgroundColor ).quality( imageQuality );
            }

            return this.imageService.readImage( readImageParams.build() );
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.badRequest( "Invalid parameters", e );
        }
        catch ( ThrottlingException e )
        {
            throw new WebException( HttpStatus.TOO_MANY_REQUESTS, "Try again later", e );
        }
    }

    @Override
    protected String resolveHash( final Media content, final Attachment attachment, final BinaryReference binaryReference )
    {
        if ( legacyMode && style == null )
        {
            return null;
        }
        else
        {
            return MediaHashResolver.resolveStyledImageHash(
                MediaHashResolver.resolveImageHash( content, MediaHashResolver.resolveAttachmentHash( attachment ) ), style, scaleParams, hmacService );
        }
    }

    @Override
    protected void addTrace( final Media media )
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "contentPath", media.getPath().toString() );
            trace.attribute( "type", "image" );
        } );
    }
}
