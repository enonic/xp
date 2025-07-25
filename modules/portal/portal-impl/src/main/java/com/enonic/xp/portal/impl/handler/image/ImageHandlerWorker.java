package com.enonic.xp.portal.impl.handler.image;

import java.io.IOException;
import java.util.Objects;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.exception.ThrottlingException;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.handler.AbstractAttachmentHandlerWorker;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.google.common.base.Strings.nullToEmpty;

public final class ImageHandlerWorker
    extends AbstractAttachmentHandlerWorker<Media>
{
    private static final int DEFAULT_BACKGROUND = 0xFFFFFF;

    private static final int DEFAULT_QUALITY = 85;

    private final ImageService imageService;

    public String filterParam;

    public String qualityParam;

    public String backgroundParam;

    public ScaleParams scaleParams;

    public ImageHandlerWorker( final WebRequest request, final ContentService contentService, final ImageService imageService )
    {
        super( request, contentService );
        this.imageService = imageService;
    }

    @Override
    protected Attachment resolveAttachment( final Content content, final String name )
    {
        final Attachment attachment = ( (Media) content ).getMediaAttachment();
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

        if ( !media.isImage() )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", content.getId() ) );
        }

        return media;
    }

    @Override
    protected ByteSource transform( final Media content, final BinaryReference binaryReference, final ByteSource binary,
                                    final MediaType contentType )
        throws IOException
    {
        final ImageOrientation imageOrientation = Objects.requireNonNullElse( content.getOrientation(), ImageOrientation.TopLeft );

        final int imageQuality = nullToEmpty( this.qualityParam ).isEmpty() ? DEFAULT_QUALITY : Integer.parseInt( this.qualityParam );

        final int backgroundColor = nullToEmpty( this.backgroundParam ).isEmpty()
            ? DEFAULT_BACKGROUND
            : Integer.parseInt( this.backgroundParam.startsWith( "0x" ) ? this.backgroundParam.substring( 2 ) : this.backgroundParam, 16 );
        try
        {
            final ReadImageParams readImageParams = ReadImageParams.newImageParams()
                .contentId( content.getId() )
                .binaryReference( binaryReference )
                .cropping( content.getCropping() )
                .focalPoint( content.getFocalPoint() )
                .orientation( imageOrientation )
                .scaleParams( this.scaleParams )
                .filterParam( this.filterParam )
                .backgroundColor( backgroundColor )
                .quality( imageQuality )
                .mimeType( contentType.toString() )
                .build();

            return this.imageService.readImage( readImageParams );
        }
        catch ( IllegalArgumentException e )
        {
            throw new WebException( HttpStatus.BAD_REQUEST, "Invalid parameters", e );
        }
        catch ( ThrottlingException e )
        {
            throw new WebException( HttpStatus.TOO_MANY_REQUESTS, "Try again later", e );
        }
    }

    @Override
    protected String resolveHash( final Media content, final Attachment attachment, final BinaryReference binaryReference )
    {
        if ( legacyMode )
        {
            final String hash = this.contentService.getBinaryKey( content.getId(), binaryReference );
            return MediaHashResolver.resolveLegacyImageHash( content, hash );
        }
        else
        {
            return MediaHashResolver.resolveImageHash( content, MediaHashResolver.resolveAttachmentHash( attachment ) );
        }
    }

    @Override
    protected void addTrace( final Media media )
    {
        {
            final Trace trace = Tracer.current();
            if ( trace != null )
            {
                trace.put( "contentPath", media.getPath() );
                trace.put( "type", "image" );
            }
        }
    }
}
