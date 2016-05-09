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
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebResponse;

import static org.apache.commons.lang.StringUtils.substringBeforeLast;

final class ImageHandlerWorker
    extends PortalHandlerWorker<PortalWebRequest, WebResponse>
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    private final String name;

    private final ContentId contentId;

    private final String filterParam;

    private final String qualityParam;

    private final String backgroundParam;

    private final ScaleParams scaleParams;

    private final boolean cacheable;

    private final ImageService imageService;

    private final ContentService contentService;

    private final MediaInfoService mediaInfoService;

    private ImageHandlerWorker( final Builder builder )
    {
        super( builder );
        name = builder.name;
        contentId = builder.contentId;
        filterParam = builder.filterParam;
        qualityParam = builder.qualityParam;
        backgroundParam = builder.backgroundParam;
        scaleParams = builder.scaleParams;
        cacheable = builder.cacheable;
        imageService = builder.imageService;
        contentService = builder.contentService;
        mediaInfoService = builder.mediaInfoService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public WebResponse execute()
    {
        try
        {
            return doExecute();
        }
        catch ( Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }


    private WebResponse doExecute()
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

        final String mimeType = getMimeType( this.name, imageContent.getName(), attachment );
        final String format = getFormat( this.name, mimeType );
        final ImageOrientation imageOrientation = mediaInfoService.getImageOrientation( binary );

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

        final ByteSource source = this.imageService.readImage( readImageParams );

        this.webResponse.setStatus( HttpStatus.OK );
        this.webResponse.setBody( source );
        this.webResponse.setContentType( MediaType.parse( mimeType ) );

        if ( cacheable )
        {
            final AccessControlEntry publicAccessControlEntry = imageContent.getPermissions().getEntry( RoleKeys.EVERYONE );
            final boolean everyoneCanRead = publicAccessControlEntry != null && publicAccessControlEntry.isAllowed( Permission.READ );
            final boolean masterBranch = ContentConstants.BRANCH_MASTER.equals( webRequest.getBranch() );
            setResponseCacheable( everyoneCanRead && masterBranch );
        }

        return this.webResponse;
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
        final Content content = this.contentService.getById( contentId );
        if ( content == null )
        {
            throw notFound( "Content with id [%s] not found", contentId.toString() );
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

    private boolean contentNameMatch( final ContentName contentName, final String urlName )
    {
        final String contentNameStr = contentName.toString();
        return contentNameStr.equals( urlName ) || contentNameStr.equals( substringBeforeLast( urlName, "." ) );
    }

    private String getMimeType( final String fileName, final ContentName contentName, final Attachment attachment )
    {
        return contentName.toString().equals( fileName ) ? attachment.getMimeType() : MediaTypes.instance().fromFile( fileName ).toString();
    }

    public static final class Builder
        extends PortalHandlerWorker.Builder<Builder, PortalWebRequest, WebResponse>
    {
        private String name;

        private ContentId contentId;

        private String filterParam;

        private String qualityParam;

        private String backgroundParam;

        private ScaleParams scaleParams;

        private boolean cacheable;

        private ImageService imageService;

        private ContentService contentService;

        private MediaInfoService mediaInfoService;

        private Builder()
        {
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder filterParam( final String filterParam )
        {
            this.filterParam = filterParam;
            return this;
        }

        public Builder qualityParam( final String qualityParam )
        {
            this.qualityParam = qualityParam;
            return this;
        }

        public Builder backgroundParam( final String backgroundParam )
        {
            this.backgroundParam = backgroundParam;
            return this;
        }

        public Builder scaleParams( final ScaleParams scaleParams )
        {
            this.scaleParams = scaleParams;
            return this;
        }

        public Builder cacheable( final boolean cacheable )
        {
            this.cacheable = cacheable;
            return this;
        }

        public Builder imageService( final ImageService imageService )
        {
            this.imageService = imageService;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder mediaInfoService( final MediaInfoService mediaInfoService )
        {
            this.mediaInfoService = mediaInfoService;
            return this;
        }

        public ImageHandlerWorker build()
        {
            return new ImageHandlerWorker( this );
        }
    }
}
