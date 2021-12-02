package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageHelper;

public class ScaledFunction
    implements ImageScaleFunction
{
    private final ScaleCalculator scaleCalculator;

    public ScaledFunction( ScaleCalculator scaleCalculator )
    {
        this.scaleCalculator = scaleCalculator;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        final ScaleCalculator.Values values = scaleCalculator.calc( source.getWidth(), source.getHeight() );

        BufferedImage targetImage = ImageHelper.getScaledInstance( source, values.newWidth, values.newHeight );
        if ( values.subimage() )
        {
            return targetImage.getSubimage( values.widthOffset, values.heightOffset, values.viewWidth, values.viewHeight );
        }
        else
        {
            return targetImage;
        }
    }

    @Override
    public int estimateResolution( final int sourceWidth, final int sourceHeight )
    {
        final ScaleCalculator.Values values = scaleCalculator.calc( sourceWidth, sourceHeight );

        return Math.multiplyExact( values.newWidth, values.newHeight );
    }
}
