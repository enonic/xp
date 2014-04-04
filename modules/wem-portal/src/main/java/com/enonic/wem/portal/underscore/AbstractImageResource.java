package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.GetAttachmentParams;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.blob;
import static com.enonic.wem.api.command.Commands.content;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.substringAfterLast;


abstract class AbstractImageResource
    extends UnderscoreResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    @Inject
    ImageFilterBuilder imageFilterBuilder;

    @Inject
    AttachmentService attachmentService;

    abstract String getFilterParam();

    abstract String getQualityParam();

    abstract String getBackgroundColorParam();


    Content getContent( final ContentPath contentPath )
    {
        final Content content = client.execute( content().get().byPath( contentPath ) );
        if ( content != null )
        {
            return content;
        }

        throw PortalWebException.notFound().message( "Content with path [{0}] not found.", contentPath ).build();
    }

    Content getContent( final ContentId contentId )
    {
        return client.execute( content().get().byId( contentId ) );
    }

    Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        // TODO : Better not found handling
        try
        {
            final GetAttachmentParams params = new GetAttachmentParams().contentId( contentId ).attachmentName( attachmentName );
            return attachmentService.get( params );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    Blob getBlob( final BlobKey blobKey )
    {
        return client.execute( blob().get( blobKey ) );
    }

    BufferedImage applyFilters( final BufferedImage sourceImage, final String format )
    {
        final String filter = getFilterParam();
        if ( isNullOrEmpty( filter ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), filter );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, getBackgroundColor() );
        }
        else
        {
            return targetImage;
        }
    }

    BufferedImage toBufferedImage( final InputStream dataStream )
    {
        try
        {
            return ImageIO.read( dataStream );
        }
        catch ( IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    String getFormat( final String fileName )
    {
        return substringAfterLast( fileName, "." ).toLowerCase();
    }

    String getFormat( final String fileName, final String defaultFormat )
    {
        if ( isNotBlank( fileName ) && fileName.contains( "." ) )
        {

            return substringAfterLast( fileName, "." ).toLowerCase();
        }
        else
        {
            return defaultFormat;
        }
    }

    int resolveQuality()
    {
        if ( isNullOrEmpty( getQualityParam() ) )
        {
            return DEFAULT_QUALITY;
        }
        final Integer value = Ints.tryParse( getQualityParam() );
        return ( value >= 0 ) && ( value <= 100 ) ? value : DEFAULT_QUALITY;
    }

    private int getBackgroundColor()
    {
        String value = getBackgroundColorParam();
        if ( isNotEmpty( value ) )
        {
            if ( value.startsWith( "0x" ) )
            {
                value = value.substring( 2 );
            }

            try
            {
                return Integer.parseInt( value, 16 );
            }
            catch ( Exception e )
            {
                // Do nothing
            }
        }
        return DEFAULT_BACKGROUND;
    }
}
