package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import com.enonic.wem.admin.rest.resource.BaseImageHelper;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.core.image.filter.effect.ScaleMaxFilter;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

final class ContentImageHelper
    extends BaseImageHelper
{
    public enum ImageFilter
    {
        ScaleSquareFilter,
        ScaleMax
    }

    public BufferedImage getImageFromBlob( final Blob blob, final int size, final ImageFilter imageFilter )
    {
        if ( blob == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( blob.getStream() );
        switch ( imageFilter )
        {
            case ScaleSquareFilter:
                return new ScaleSquareFilter( size ).filter( image );

            case ScaleMax:
                return new ScaleMaxFilter( size ).filter( image );

            default:
                throw new IllegalArgumentException( "Invalid image filter: " + imageFilter );
        }
    }
}
