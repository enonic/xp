/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.FocalPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WideScaleTest
    extends BaseImageFilterTest
{
    @Test
    void testDownscale()
    {
        BufferedImage scaled = scale( 100, 10 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 10, scaled.getHeight() );
    }

    @Test
    void testUpscale()
    {
        BufferedImage scaled = scale( 600, 100 );
        assertEquals( 600, scaled.getWidth() );
        assertEquals( 100, scaled.getHeight() );
    }

    @Test
    void testHeightTooBig()
    {
        BufferedImage scaled = scale( 100, 800 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 75, scaled.getHeight() );
    }

    private BufferedImage scale( int width, int height )
    {
        ImageFunction scaleFunction = newScaleFunctions().wide( FocalPoint.DEFAULT, width, height );
        return scaleFunction.apply( getOpaque() );
    }
}
