package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.filter.ScaleMaxFilter;
import com.enonic.xp.image.filter.ScaleSquareFilter;
import com.enonic.xp.util.Exceptions;

final class ContentImageHelper
    extends BaseImageHelper
{
    public enum ImageFilter
    {
        SCALE_SQUARE_FILTER,
        SCALE_MAX_FILTER
    }

    BufferedImage readImage( final ByteSource blob, final int size, final ImageFilter imageFilter )
    {
        try (final InputStream inputStream = blob.openStream())
        {
            return readImage( inputStream, size, imageFilter );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private BufferedImage readImage( final InputStream inputStream, final int size, final ImageFilter imageFilter )
    {
        final BufferedImage image = ImageHelper.toBufferedImage( inputStream );
        if ( size > 0 && ( image.getWidth() >= size ) )
        {
            switch ( imageFilter )
            {
                case SCALE_SQUARE_FILTER:
                    return new ScaleSquareFilter( size ).filter( image );

                case SCALE_MAX_FILTER:
                    return new ScaleMaxFilter( size ).filter( image );

                default:
                    throw new IllegalArgumentException( "Invalid image filter: " + imageFilter );
            }
        }
        else
        {
            return image;
        }
    }
}
