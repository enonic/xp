/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.wem.core.image.filter.ImageFilter;

import static org.junit.Assert.*;

public class ScaleHeightFilterTest
    extends BaseImageFilterTest
{
    @Test
    public void testDownscale()
    {
        BufferedImage scaled = scale( 100 );
        assertEquals( 133, scaled.getWidth() );
        assertEquals( 100, scaled.getHeight() );
    }

    @Test
    public void testUpscale()
    {
        BufferedImage scaled = scale( 600 );
        assertEquals( 800, scaled.getWidth() );
        assertEquals( 600, scaled.getHeight() );
    }

    private BufferedImage scale( int size )
    {
        ImageFilter filter = new ScaleHeightFilter( size );
        return filter.filter( getOpaque() );
    }
}