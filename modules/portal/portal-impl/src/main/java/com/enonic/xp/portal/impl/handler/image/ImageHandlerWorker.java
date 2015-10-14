package com.enonic.xp.portal.impl.handler.image;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.portal.impl.handler.PortalHandlerWorker;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;

import static org.apache.commons.lang.StringUtils.substringBeforeLast;

final class ImageHandlerWorker
    extends PortalHandlerWorker
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

    @Override
    public void execute()
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
            build();

        final ByteSource source = this.imageService.readImage( readImageParams );

        this.response.status( HttpStatus.OK );
        this.response.body( source );
        this.response.contentType( MediaType.parse( mimeType ) );
        if ( cacheable )
        {
            setResponseCacheable();
        }
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
}
