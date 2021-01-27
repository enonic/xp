package com.enonic.xp.portal.impl.handler.image;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
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
    private static final int DEFAULT_BACKGROUND = 0x00FFFFFF;

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
        if ( !contentNameMatch( content.getName(), name ) )
        {
            throw WebException.notFound( String.format( "Image [%s] not found for content [%s]", name, this.contentId ) );
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

        final String fileExtension = Files.getFileExtension( this.name ).toLowerCase();
        final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary, content );

        final String mimeType = getMimeType( this.name, content.getName(), attachment );

        final PortalResponse.Builder portalResponse = PortalResponse.create().
            contentType( MediaType.parse( mimeType ) );

        if ( "svgz".equals( fileExtension ) )
        {
            portalResponse.header( "Content-Encoding", "gzip" );
            portalResponse.body( binary );
        }
        else if ( "svg".equals( fileExtension ) )
        {
            portalResponse.body( binary );
        }
        else
        {
            final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                contentId( this.contentId ).
                binaryReference( binaryReference ).
                cropping( content.getCropping() ).
                scaleParams( this.scaleParams ).
                focalPoint( content.getFocalPoint() ).
                filterParam( this.filterParam ).
                backgroundColor( getBackgroundColor() ).
                mimeType( mimeType ).
                quality( getImageQuality() ).
                orientation( imageOrientation ).
                build();

            portalResponse.body( this.imageService.readImage( readImageParams ) );
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

    private String getMimeType( final String fileName, final ContentName contentName, final Attachment attachment )
    {
        return contentName.toString().equals( fileName ) ? attachment.getMimeType() : MediaTypes.instance().fromFile( fileName ).toString();
    }

    private int getImageQuality()
    {
        if ( this.backgroundParam == null )
        {
            return DEFAULT_QUALITY;
        }

        try
        {
            final int quality = Integer.parseInt( this.qualityParam );
            if ( quality <= 0 || quality > 100 )
            {
                throw WebException.badRequest( String.format( "Invalid quality %s", this.qualityParam ) );
            }
            return quality;
        }
        catch ( final Exception e )
        {
            throw WebException.badRequest( String.format( "Invalid quality %s", this.qualityParam ) );
        }
    }

    private int getBackgroundColor()
    {
        if ( this.backgroundParam == null )
        {
            return DEFAULT_BACKGROUND;
        }

        String color = this.backgroundParam;
        if ( color.startsWith( "0x" ) )
        {
            color = this.backgroundParam.substring( 2 );
        }

        try
        {
            return Integer.parseUnsignedInt( color, 16 );
        }
        catch ( final Exception e )
        {
            throw WebException.badRequest( String.format( "Invalid background %s", this.qualityParam ) );
        }
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

    private boolean contentNameMatch( final ContentName contentName, final String urlName )
    {
        final String contentNameStr = contentName.toString();
        return contentNameStr.equals( urlName ) || contentNameStr.equals( Files.getNameWithoutExtension( urlName ) );
    }
}
