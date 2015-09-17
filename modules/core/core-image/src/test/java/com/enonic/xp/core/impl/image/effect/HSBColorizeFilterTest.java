package com.enonic.xp.core.impl.image.effect;

import java.awt.image.RGBImageFilter;

import org.junit.Test;

import static org.junit.Assert.*;

public class HSBColorizeFilterTest
    extends BaseImageFilterTest
{

    private static final int FILTER_COLOR = 0xFFFFFF;

    private static final int SOURCE_COLOR = 0xFA0000;

    private static final int RESULT_COLOR = 0xFAFAFA;

    @Test
    public void testEmpty()
    {
        final RGBImageFilter filter = new HSBColorizeFilter( FILTER_COLOR );
        final int result = filter.filterRGB( 0, 0, 0 );
        //String.format("#%06X", (0xFFFFFF & result)); // to see hex representation

        assertEquals( 0, result );
    }

    @Test
    public void testFilter()
    {
        final RGBImageFilter filter = new HSBColorizeFilter( FILTER_COLOR );
        final int result = filter.filterRGB( 0, 0, SOURCE_COLOR );
        //String.format("#%06X", (0xFFFFFF & result)); // to see hex representation

        assertEquals( RESULT_COLOR, result );
    }

    @Test
    public void testWhiteOnWhiteFilter()
    {
        final RGBImageFilter filter = new HSBColorizeFilter( FILTER_COLOR );
        final int result = filter.filterRGB( 0, 0, FILTER_COLOR );
        //String.format("#%06X", (0xFFFFFF & result)); // to see hex representation

        assertEquals( FILTER_COLOR, result );
    }
}