package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.restlet.resource.ResourceException;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;

public abstract class ImageResource
    extends UnderscoreResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;

    @Inject
    protected AttachmentService attachmentService;

    @Inject
    protected BlobService blobService;

    @Inject
    protected ContentService contentService;

    protected String filterParam;

    protected int quality;

    protected int backgroundColor;

    @Override
    protected void doInit()
        throws ResourceException
    {
        super.doInit();

        this.filterParam = getQueryValue( "filter" );
        this.backgroundColor = parseBackgroundColor( getQueryValue( "background" ) );
        this.quality = parseQuality( getQueryValue( "quality" ) );
    }

    protected final BufferedImage toBufferedImage( final InputStream dataStream )
    {
        try
        {
            return ImageIO.read( dataStream );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    protected final byte[] serializeImage( final BufferedImage image, final String format )
    {
        try
        {
            return ImageHelper.writeImage( image, format, this.quality );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private static int parseQuality( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return DEFAULT_QUALITY;
        }

        final Integer num = Ints.tryParse( value );
        return ( num >= 0 ) && ( num <= 100 ) ? num : DEFAULT_QUALITY;
    }

    private static int parseBackgroundColor( final String value )
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

    protected final String getFormat( final String fileName )
    {
        return StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
    }

    protected final BufferedImage applyFilters( final BufferedImage sourceImage, final String format )
    {
        if ( Strings.isNullOrEmpty( this.filterParam ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), this.filterParam );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, this.backgroundColor );
        }
        else
        {
            return targetImage;
        }
    }

    protected final Blob getBlob( final BlobKey blobKey )
    {
        return this.blobService.get( blobKey );
    }

    protected final Attachment getAttachment( final ContentId contentId )
    {
        try
        {
            return this.attachmentService.getAll( contentId ).first();
        }
        catch ( final ContentNotFoundException e )
        {
            throw notFound( "Attachment for content [%s] not found", contentId.toString() );
        }
    }

    protected final Content getContent( final ContentId contentId )
    {
        final Content content = this.contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT );
        if ( content != null )
        {
            return content;
        }

        throw notFound( "Content with id [%s] not found", contentId.toString() );
    }

    protected final Content getContent( final ContentPath contentPath )
    {
        final Content content = this.contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT );
        if ( content != null )
        {
            return content;
        }

        throw notFound( "Content with path [%s] not found", contentPath.toString() );
    }

    protected final Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        try
        {
            return this.attachmentService.get( contentId, attachmentName );
        }
        catch ( ContentNotFoundException e )
        {
            throw notFound( "Attachment [%s] for content [%s] not found", attachmentName, contentId.toString() );
        }
    }
}
