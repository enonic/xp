/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.xp.image.ImageScaleFunction;

import static org.junit.Assert.*;

public class ScaleWideFunctionTest
    extends BaseImageFilterTest
{
    @Test
    public void testDownscale()
    {
        BufferedImage scaled = scale( 100, 10 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 10, scaled.getHeight() );
    }

    @Test
    public void testUpscale()
    {
        BufferedImage scaled = scale( 600, 100 );
        assertEquals( 600, scaled.getWidth() );
        assertEquals( 100, scaled.getHeight() );
    }

    @Test
    public void testHeightTooBig()
    {
        BufferedImage scaled = scale( 100, 800 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 75, scaled.getHeight() );
    }

    private BufferedImage scale( int width, int height )
    {
        ImageScaleFunction scaleFunction = new ScaleWideFunction( width, height, 0.5f );
        return scaleFunction.scale( getOpaque() );
    }
}