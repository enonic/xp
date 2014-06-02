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
import com.enonic.wem.portal.exception.PortalWebException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.substringAfterLast;


abstract class AbstractImageResource
    extends UnderscoreResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    public interface Params
    {
        String getFilterParam();

        String getQualityParam();

        String getBackgroundColorParam();
    }

    @Inject
    ImageFilterBuilder imageFilterBuilder;

    @Inject
    AttachmentService attachmentService;

    @Inject
    BlobService blobService;

    @Inject
    ContentService contentService;

    Content getContent( final ContentPath contentPath )
    {
        final Content content = contentService.getByPath( contentPath, ContentConstants.DEFAULT_CONTEXT);
        if ( content != null )
        {
            return content;
        }

        throw PortalWebException.notFound().message( "Content with path [{0}] not found.", contentPath ).build();
    }

    Content getContent( final ContentId contentId )
    {
        return contentService.getById( contentId, ContentConstants.DEFAULT_CONTEXT);
    }

    Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        // TODO : Better not found handling
        try
        {
            return attachmentService.get( contentId, attachmentName );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    Blob getBlob( final BlobKey blobKey )
    {
        return blobService.get( blobKey );
    }

    BufferedImage applyFilters( final Params params, final BufferedImage sourceImage, final String format )
    {
        final String filter = params.getFilterParam();
        if ( isNullOrEmpty( filter ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), filter );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, getBackgroundColor( params ) );
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

    int resolveQuality( final Params paras )
    {
        final String qualityParam = paras.getQualityParam();
        if ( isNullOrEmpty( qualityParam ) )
        {
            return DEFAULT_QUALITY;
        }
        final Integer value = Ints.tryParse( qualityParam );
        return ( value >= 0 ) && ( value <= 100 ) ? value : DEFAULT_QUALITY;
    }

    private int getBackgroundColor( final Params params )
    {
        String value = params.getBackgroundColorParam();
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
