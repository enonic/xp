package com.enonic.xp.portal.impl.handler.image;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
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
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.apache.commons.lang.StringUtils.substringBeforeLast;

final class ImageHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    protected String name;

    protected ContentId contentId;

    protected String filterParam;

    protected String qualityParam;

    protected String backgroundParam;

    protected ScaleParams scaleParams;

    protected boolean cacheable;

    protected ImageService imageService;

    protected ContentService contentService;

    protected MediaInfoService mediaInfoService;

    public ImageHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final Media imageContent = getImage( this.contentId );
        if ( !contentNameMatch( imageContent.getName(), name ) )
        {
            throw notFound( "Image [%s] not found for content [%s]", name, this.contentId );
        }

        final Attachment attachment = imageContent.getMediaAttachment();
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found", imageContent.getName().toString() );
        }

        final ByteSource binary = this.contentService.getBinary( this.contentId, attachment.getBinaryReference() );
        if ( binary == null )
        {
            throw notFound( "Binary [%s] not found for content [%s]", attachment.getBinaryReference(), this.contentId );
        }

        if ( request.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final String mimeType = getMimeType( this.name, imageContent.getName(), attachment );
        final String format = getFormat( this.name, mimeType );
        final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary, imageContent );

        final PortalResponse.Builder portalResponse = PortalResponse.create().
            contentType( MediaType.parse( mimeType ) );

        if ( "svgz".equals( format ) )
        {
            portalResponse.header( "Content-Encoding", "gzip" );
            portalResponse.body( binary );
        }
        else if ( "svg".equals( format ) )
        {
            portalResponse.body( binary );
        }
        else
        {
            final ReadImageParams readImageParams = ReadImageParams.newImageParams().
                contentId( this.contentId ).
                binaryReference( attachment.getBinaryReference() ).
                cropping( imageContent.getCropping() ).
                scaleParams( this.scaleParams ).
                focalPoint( imageContent.getFocalPoint() ).
                filterParam( this.filterParam ).
                backgroundColor( getBackgroundColor() ).
                format( format ).
                quality( getImageQuality() ).
                orientation( imageOrientation ).
                build();

            portalResponse.body( this.imageService.readImage( readImageParams ) );
        }

        if ( cacheable )
        {
            final AccessControlEntry publicAccessControlEntry = imageContent.getPermissions().getEntry( RoleKeys.EVERYONE );
            final boolean everyoneCanRead = publicAccessControlEntry != null && publicAccessControlEntry.isAllowed( Permission.READ );
            final boolean masterBranch = ContentConstants.BRANCH_MASTER.equals( request.getBranch() );
            setResponseCacheable( portalResponse, everyoneCanRead && masterBranch );
        }

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", imageContent.getPath() );
            trace.put( "type", "image" );
        }

        return portalResponse.build();
    }

    private String getFormat( final String fileName, final String mimeType )
        throws Exception
    {
        String format = StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
        if ( Strings.isNullOrEmpty( format ) )
        {
            format = this.imageService.getFormatByMimeType( mimeType );
        }

        return format;
    }

    private int getImageQuality()
    {
        final int quality = parseImageQuality();
        return ( quality > 0 ) && ( quality <= 100 ) ? quality : DEFAULT_QUALITY;
    }

    private int parseImageQuality()
    {
        try
        {
            return Integer.parseInt( this.qualityParam );
        }
        catch ( final Exception e )
        {
            return DEFAULT_QUALITY;
        }
    }

    private int getBackgroundColor()
    {
        if ( Strings.isNullOrEmpty( this.backgroundParam ) )
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
            return Integer.parseInt( color, 16 );
        }
        catch ( final Exception e )
        {
            return DEFAULT_BACKGROUND;
        }
    }

    private Media getImage( final ContentId contentId )
    {
        final Content content = getContentById( contentId );
        if ( content == null )
        {
            if ( this.contentService.contentExists( contentId ) )
            {
                throw forbidden( "You don't have permission to access [%s]", contentId );
            }
            else
            {
                throw notFound( "Content with id [%s] not found", contentId.toString() );
            }
        }

        if ( !( content instanceof Media ) )
        {
            throw notFound( "Content with id [%s] is not an Image", contentId.toString() );
        }

        final Media media = (Media) content;
        if ( !media.isImage() )
        {
            throw notFound( "Content with id [%s] is not an Image", contentId.toString() );
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
            return null;
        }
    }

    private boolean contentNameMatch( final ContentName contentName, final String urlName )
    {
        final String contentNameStr = contentName.toString();
        return contentNameStr.equals( urlName ) || contentNameStr.equals( substringBeforeLast( urlName, "." ) );
    }

    private String getMimeType( final String fileName, final ContentName contentName, final Attachment attachment )
    {
        return contentName.toString().equals( fileName ) ? attachment.getMimeType() : MediaTypes.instance().fromFile( fileName ).toString();
    }
}
