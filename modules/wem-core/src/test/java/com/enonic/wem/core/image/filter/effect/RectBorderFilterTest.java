package com.enonic.wem.core.image.filter.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.wem.core.image.filter.ImageFilter;

import static org.junit.Assert.*;

public class RectBorderFilterTest
    extends BaseImageFilterTest
{
    @Test
    public void testBorder()
    {
        ImageFilter filter = new RectBorderFilter( 10, 10 );
        BufferedImage result = filter.filter( getTransparent() );

        int rgb = result.getRGB( 0, 0 );
        int alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );

        rgb = result.getRGB( 9, 9 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 255, alpha );

        rgb = result.getRGB( 10, 10 );
        alpha = ( rgb >> 24 ) & 0xff;
        assertEquals( 0, alpha );
    }
}