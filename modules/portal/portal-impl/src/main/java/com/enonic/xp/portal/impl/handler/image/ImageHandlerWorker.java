package com.enonic.xp.portal.impl.handler.image;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static com.google.common.base.Strings.nullToEmpty;

final class ImageHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    private static final int DEFAULT_BACKGROUND = 0xFFFFFF;

    private static final int DEFAULT_QUALITY = 85;

    protected String name;

    protected ContentId contentId;

    protected String filterParam;

    protected String qualityParam;

    protected String backgroundParam;

    protected ScaleParams scaleParams;

    protected ImageService imageService;

    protected ContentService contentService;

    protected MediaInfoService mediaInfoService;

    protected String fingerprint;

    protected String privateCacheControlHeaderConfig;

    protected String publicCacheControlHeaderConfig;

    ImageHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final Media content = getImage( this.contentId );
        final String contentName = content.getName().toString();

        final boolean contentNameEquals = contentName.equals( this.name );

        if ( !( contentNameEquals || contentName.equals( Files.getNameWithoutExtension( this.name ) ) ) )
        {
            throw WebException.notFound( String.format( "Image [%s] not found for content [%s]", this.name, this.contentId ) );
        }

        final Attachment attachment = content.getMediaAttachment();
        if ( attachment == null )
        {
            throw WebException.notFound( String.format( "Attachment [%s] not found", content.getName() ) );
        }

        final BinaryReference binaryReference = attachment.getBinaryReference();

        final ByteSource binary = this.contentService.getBinary( this.contentId, binaryReference );
        if ( binary == null )
        {
            throw WebException.notFound( String.format( "Binary [%s] not found for content [%s]", binaryReference, this.contentId ) );
        }

        if ( request.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final String attachmentMimeType = attachment.getMimeType();

        final PortalResponse.Builder portalResponse = PortalResponse.create();

        if ( "svgz".equals( attachment.getExtension() ) )
        {
            portalResponse.contentType( MediaType.SVG_UTF_8.withoutParameters() );
            portalResponse.header( "Content-Encoding", "gzip" );
            portalResponse.body( binary );
        }
        else if ( attachmentMimeType.equals( "image/svg+xml" ) || attachmentMimeType.equals( "image/gif" ) )
        {
            portalResponse.contentType( MediaType.parse( attachmentMimeType ) );
            portalResponse.body( binary );
        }
        else
        {
            final ImageOrientation imageOrientation = Objects.requireNonNullElseGet( content.getOrientation(),
                                                                                     () -> Objects.requireNonNullElse(
                                                                                         mediaInfoService.getImageOrientation( binary ),
                                                                                         ImageOrientation.TopLeft ) );

            final MediaType mimeType =
                contentNameEquals ? MediaType.parse( attachmentMimeType ) : MediaTypes.instance().fromFile( this.name );

            portalResponse.contentType( mimeType );

            try
            {
                final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                    contentId( this.contentId ).
                    binaryReference( binaryReference ).
                    cropping( content.getCropping() ).
                    scaleParams( this.scaleParams ).
                    focalPoint( content.getFocalPoint() ).
                    filterParam( this.filterParam ).
                    backgroundColor( getBackgroundColor() ).
                    mimeType( mimeType.toString() ).
                    quality( getImageQuality() ).
                    orientation( imageOrientation ).
                    build();

                portalResponse.body( this.imageService.readImage( readImageParams ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new WebException( HttpStatus.BAD_REQUEST, "Invalid parameters", e );
            }
        }

        if ( !nullToEmpty( this.fingerprint ).isBlank() )
        {
            final boolean isPublic = content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) &&
                ContentConstants.BRANCH_MASTER.equals( request.getBranch() );
            final String cacheControlHeaderConfig = isPublic ? publicCacheControlHeaderConfig : privateCacheControlHeaderConfig;

            if ( !nullToEmpty( cacheControlHeaderConfig ).isBlank() && this.fingerprint.equals( resolveHash( content ) ) )
            {
                portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeaderConfig );
            }
        }

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", content.getPath() );
            trace.put( "type", "image" );
        }

        return portalResponse.build();
    }

    private int getImageQuality()
    {
        if ( nullToEmpty( this.qualityParam ).isEmpty() )
        {
            return DEFAULT_QUALITY;
        }

        return Integer.parseInt( this.qualityParam );
    }

    private int getBackgroundColor()
    {
        if ( nullToEmpty( this.backgroundParam ).isEmpty() )
        {
            return DEFAULT_BACKGROUND;
        }

        final String color = this.backgroundParam.startsWith( "0x" ) ? this.backgroundParam.substring( 2 ) : this.backgroundParam;

        return Integer.parseInt( color, 16 );
    }

    private String resolveHash( final Media media )
    {
        final String binaryKey = this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return Hashing.sha1().
            newHasher().
            putString( String.valueOf( binaryKey ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 ).
            hash().
            toString();
    }

    private Media getImage( final ContentId contentId )
    {
        final Content content = getContentById( contentId );

        if ( !( content instanceof Media ) )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", contentId ) );
        }

        final Media media = (Media) content;
        if ( !media.isImage() )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", contentId ) );
        }

        return media;
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final Exception e )
        {
            throw WebException.notFound( String.format( "Content with id [%s] not found", contentId ) );
        }
    }
}
