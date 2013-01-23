package com.enonic.wem.core.image.filter.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.wem.core.image.filter.AwtImageFilter;
import com.enonic.wem.core.image.filter.AwtImageFilterTest;
import com.enonic.wem.core.image.filter.ImageFilter;

import static org.junit.Assert.*;

public class SepiaFilterTest
    extends AwtImageFilterTest
{
    @Test
    public void testOpaqueImage()
    {
        java.awt.image.ImageFilter operation = new SepiaFilter( 20 );
        ImageFilter filter = new AwtImageFilter( operation );

        BufferedImage result = filter.filter( getOpaque() );

        int rgb = result.getRGB( 10, 10 );
        int alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );

        rgb = result.getRGB( 20, 20 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );
    }

    @Test
    public void testImageWithTransparentAreas()
    {
        java.awt.image.ImageFilter operation = new SepiaFilter( 20 );
        ImageFilter filter = new AwtImageFilter( operation );

        BufferedImage result = filter.filter( getTransparent() );

        // Uppder left corner is transparent
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