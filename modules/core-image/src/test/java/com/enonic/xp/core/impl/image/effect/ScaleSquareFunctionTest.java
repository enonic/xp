/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.filter.ScaleSquareFunction;

import static org.junit.Assert.*;

public class ScaleSquareFunctionTest
    extends BaseImageFilterTest
{
    @Test
    public void testDownscale()
    {
        BufferedImage scaled = scale( 100 );
        assertEquals( 100, scaled.getWidth() );
        assertEquals( 100, scaled.getHeight() );
    }

    @Test
    public void testUpscale()
    {
        BufferedImage scaled = scale( 600 );
        assertEquals( 600, scaled.getWidth() );
        assertEquals( 600, scaled.getHeight() );
    }

    private BufferedImage scale( int size )
    {
        ImageScaleFunction scaleFunction = new ScaleSquareFunction( size );
        return scaleFunction.scale( getOpaque() );
    }
}