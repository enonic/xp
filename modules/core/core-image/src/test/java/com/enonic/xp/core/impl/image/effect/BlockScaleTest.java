package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockScaleTest
    extends BaseImageFilterTest
{
    @Test
    void w_upscale_h_downscale()
    {
        final BufferedImage original = ImageHelper.createImage( 300, 500, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 150 ).apply( original );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 150, scaled.getHeight() );
    }

    @Test
    void w_downscale_h_upscale()
    {
        final BufferedImage original = ImageHelper.createImage( 300, 500, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 150, 550 ).apply( original );
        assertEquals( 150, scaled.getWidth() );
        assertEquals( 550, scaled.getHeight() );
    }

    @Test
    void wide_rectangle_upscale()
    {
        final BufferedImage original = ImageHelper.createImage( 400, 300, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 320 ).apply( original );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 320, scaled.getHeight() );
    }

    @Test
    void wide_rectangle_downscale()
    {
        final BufferedImage original = ImageHelper.createImage( 400, 300, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 250, 150 ).apply( original );
        assertEquals( 250, scaled.getWidth() );
        assertEquals( 150, scaled.getHeight() );
    }

    @Test
    void tall_rectangle_upscale()
    {
        final BufferedImage original = ImageHelper.createImage( 300, 400, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 320 ).apply( original );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 320, scaled.getHeight() );
    }

    @Test
    void tall_rectangle_downscale()
    {
        final BufferedImage original = ImageHelper.createImage( 300, 400, false );

        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 250, 150 ).apply( original );
        assertEquals( 250, scaled.getWidth() );
        assertEquals( 150, scaled.getHeight() );
    }

    @Test
    void upscale_square()
    {
        final BufferedImage original = ImageHelper.createImage( 400, 300, false );
        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 550 ).apply( original );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 550, scaled.getHeight() );
    }

}
