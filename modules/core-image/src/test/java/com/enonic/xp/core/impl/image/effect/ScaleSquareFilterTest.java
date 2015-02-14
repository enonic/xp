/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.wem.api.image.ImageFilter;
import com.enonic.wem.api.image.filter.ScaleSquareFilter;

import static org.junit.Assert.*;

public class ScaleSquareFilterTest
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
        ImageFilter filter = new ScaleSquareFilter( size );
        return filter.filter( getOpaque() );
    }
}