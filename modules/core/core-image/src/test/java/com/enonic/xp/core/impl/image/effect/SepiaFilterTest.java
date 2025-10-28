package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.image.ImageFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SepiaFilterTest
    extends BaseImageFilterTest
{
    @Test
    void testOpaqueImage()
    {
        ImageFunction function = newFilters().sepia( 20 );

        BufferedImage result = function.apply( getOpaque() );

        int rgb = result.getRGB( 10, 10 );
        int alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );

        rgb = result.getRGB( 20, 20 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );
    }

    @Test
    void testImageWithTransparentAreas()
    {
        ImageFunction filter = newFilters().sepia( 20 );

        BufferedImage result = filter.apply( getTransparent() );

        // Upper left corner is transparent
        int rgb = result.getRGB( 10, 10 );
        int alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 0, alpha );

        rgb = result.getRGB( 20, 20 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 0, alpha );

        // Further down, there are opaque pixels.
        rgb = result.getRGB( 35, 35 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );
    }
}
