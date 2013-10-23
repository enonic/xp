package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import com.enonic.wem.admin.rest.resource.BaseImageHelper;
import com.enonic.wem.api.content.binary.Binary;
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

    public ContentImageHelper()
    {
    }

    public BufferedImage getImageFromBinary( final Binary binary, final int size, final ImageFilter imageFilter )
        throws Exception
    {
        if ( binary == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( binary.asInputStream() );
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
