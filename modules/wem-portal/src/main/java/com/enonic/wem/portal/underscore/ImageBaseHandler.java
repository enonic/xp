package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.base.BaseHandler;

public abstract class ImageBaseHandler
    extends BaseHandler
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    @Inject
    protected BlobService blobService;

    @Inject
    protected ContentService contentService;

    @Inject
    protected AttachmentService attachmentService;

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;

    protected final BufferedImage toBufferedImage( final InputStream dataStream )
        throws Exception
    {
        return ImageIO.read( dataStream );
    }

    protected final byte[] serializeImage( final BufferedImage image, final String format, final int quality )
        throws Exception
    {
        return ImageHelper.writeImage( image, format, quality );
    }

    protected final Content getContent( final ContentId contentId )
    {
        final Content content = this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT );
        if ( content != null )
        {
            return content;
        }

        throw notFound();
    }

    protected final Content getContent( final ContentPath contentPath )
    {
        final Content content = this.contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT );
        if ( content != null )
        {
            return content;
        }

        throw notFound();
    }

    protected final Attachment getAttachment( final ContentId contentId )
    {
        return this.attachmentService.getAll( contentId ).first();
    }

    protected final Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        return this.attachmentService.get( contentId, attachmentName );
    }

    protected final Blob getBlob( final BlobKey blobKey )
    {
        return this.blobService.get( blobKey );
    }

    protected final String getFormat( final String fileName )
    {
        return StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
    }

    protected static int parseBackgroundColor( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return DEFAULT_BACKGROUND;
        }

        String color = value;
        if ( color.startsWith( "0x" ) )
        {
            color = value.substring( 2 );
        }

        try
        {
            return Integer.parseInt( color, 16 );
        }
        catch ( Exception e )
        {
            return DEFAULT_BACKGROUND;
        }
    }

    protected static int parseQuality( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return DEFAULT_QUALITY;
        }

        final Integer num = Ints.tryParse( value );
        return ( num >= 0 ) && ( num <= 100 ) ? num : DEFAULT_QUALITY;
    }

    protected final BufferedImage applyFilters( final BufferedImage sourceImage, final String format, final String filter,
                                                final int background )
    {
        if ( Strings.isNullOrEmpty( filter ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), filter );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, background );
        }
        else
        {
            return targetImage;
        }
    }
}
