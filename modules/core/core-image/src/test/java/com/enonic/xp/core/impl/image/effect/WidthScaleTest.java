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

class WidthScaleTest
    extends BaseImageFilterTest
{
    @Test
    void testDownscale()
    {
        BufferedImage scaled = scale( 100 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 75, scaled.getHeight() );
    }

    @Test
    void testUpscale()
    {
        BufferedImage scaled = scale( 600 );
        assertEquals( 600, scaled.getWidth() );
        assertEquals( 450, scaled.getHeight() );
    }

    private BufferedImage scale( int size )
    {
        ImageFunction scaleFunction = newScaleFunctions().width( FocalPoint.DEFAULT, size );
        return scaleFunction.apply( getOpaque() );
    }
}
