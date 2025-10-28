package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.image.ImageFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RoundedFilterTest
    extends BaseImageFilterTest
{

    private static final int BL_COLOR = -16777216;

    @Test
    void testFilter()
    {
        ImageFunction filter = newFilters().rounded( 20, 10, BL_COLOR );
        BufferedImage result = filter.apply( getOpaque() );

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
