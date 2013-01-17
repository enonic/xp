/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.wem.core.image.filter.ImageFilter;

import static org.junit.Assert.*;

public class ScaleWidthFilterTest
    extends BaseImageFilterTest
{
    @Test
    public void testDownscale()
    {
        BufferedImage scaled = scale( 100 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 75, scaled.getHeight() );
    }

    @Test
    public void testUpscale()
    {
        BufferedImage scaled = scale( 600 );
        assertEquals( 600, scaled.getWidth() );
        assertEquals( 450, scaled.getHeight() );
    }

    private BufferedImage scale( int size )
    {
        ImageFilter filter = new ScaleWidthFilter( size );
        return filter.filter( getOpaque() );
    }
}
