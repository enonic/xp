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

class HeightScaleTest
    extends BaseImageFilterTest
{
    @Test
    void testDownscale()
    {
        BufferedImage scaled = scale( 100 );
        assertEquals( 133, scaled.getWidth() );
        assertEquals( 100, scaled.getHeight() );
    }

    @Test
    void testUpscale()
    {
        BufferedImage scaled = scale( 600 );
        assertEquals( 800, scaled.getWidth() );
        assertEquals( 600, scaled.getHeight() );
    }

    private BufferedImage scale( int size )
    {
        ImageFunction scaleFunction = newScaleFunctions().height( FocalPoint.DEFAULT, size );
        return scaleFunction.apply( getOpaque() );
    }
}
