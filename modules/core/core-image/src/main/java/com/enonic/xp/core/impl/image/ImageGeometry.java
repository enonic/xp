package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.impl.image.effect.ScaleCalculator;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ImageHelper;

/** The same oriented crop and resize geometry is used for admission and both execution backends. */
record ImageGeometry(int cropX, int cropY, int cropWidth, int cropHeight, boolean cropped,
                     ScaleCalculator.Values scale, long peakPixels)
{
    static ImageGeometry calculate( final int width, final int height, final NormalizedImageParams params,
                                    final ImageScaleFunction scale, final long maxPixels )
    {
        check( width, height, maxPixels );
        final boolean swap = switch ( params.getOrientation() )
        {
            case LeftTop, RightTop, RightBottom, LeftBottom -> true;
            default -> false;
        };
        final int orientedWidth = swap ? height : width;
        final int orientedHeight = swap ? width : height;
        final Cropping crop = params.getCropping();
        final int x = Math.clamp( (long) ( orientedWidth * crop.left() ), 0, orientedWidth - 1 );
        final int y = Math.clamp( (long) ( orientedHeight * crop.top() ), 0, orientedHeight - 1 );
        final int w = Math.clamp( (long) ( orientedWidth * crop.width() ), 1, orientedWidth - x );
        final int h = Math.clamp( (long) ( orientedHeight * crop.height() ), 1, orientedHeight - y );
        final ScaleCalculator.Values values = scale == null ? null : scale.calculate( w, h );
        long peak = (long) width * height;
        if ( values != null )
        {
            check( values.newWidth, values.newHeight, maxPixels );
            check( values.viewWidth, values.viewHeight, maxPixels );
            peak = Math.max( peak, (long) values.newWidth * values.newHeight );
        }
        return new ImageGeometry( x, y, w, h, !crop.isUnmodified(), values, peak );
    }

    private static void check( final int width, final int height, final long maxPixels )
    {
        if ( width < 1 || height < 1 || (long) width * height > maxPixels )
        {
            throw new IllegalArgumentException( "Output image exceeds processing.maxPixels or has invalid dimensions" );
        }
    }

    BufferedImage apply( BufferedImage image )
    {
        if ( cropped )
        {
            image = image.getSubimage( cropX, cropY, cropWidth, cropHeight );
        }
        if ( scale != null )
        {
            image = ImageHelper.getScaledInstance( image, scale.newWidth, scale.newHeight );
            if ( scale.subimage() )
            {
                image = image.getSubimage( scale.widthOffset, scale.heightOffset, scale.viewWidth, scale.viewHeight );
            }
        }
        return image;
    }
}
