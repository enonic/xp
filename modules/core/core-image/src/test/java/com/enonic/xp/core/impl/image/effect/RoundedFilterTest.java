package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.xp.core.impl.image.ImageFilter;

import static org.junit.Assert.*;

public class RoundedFilterTest
    extends BaseImageFilterTest
{

    private static final int BL_COLOR = -16777216;

    @Test
    public void testFilter()
    {
        ImageFilter filter = new RoundedFilter( 20, 10, BL_COLOR );
        BufferedImage result = filter.filter( getOpaque() );

        int rgb = getOpaque().getRGB( 10, 10 );
        assertNotEquals( rgb, BL_COLOR );

        int rgbFiltered = result.getRGB( 10, 10 );
        assertEquals( BL_COLOR, rgbFiltered );

        assertNotEquals( rgb, rgbFiltered );

        rgb = getOpaque().getRGB( 50, 50 );
        rgbFiltered = result.getRGB( 50, 50 );

        assertEquals( rgb, rgbFiltered );
    }
}