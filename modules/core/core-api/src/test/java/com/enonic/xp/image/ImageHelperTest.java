package com.enonic.xp.image;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageHelperTest
{
    @Test
    void createImagePlaceholder()
    {
        final String str = ImageHelper.createImagePlaceholder( 2, 3 );
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAADCAYAAAC56t6BAAAAC0lEQVR4nGNgwAcAAB4AAfb96ZYAAAAASUVORK5CYII=",
                      str );
    }

    @Test
    void getFormatByMimeType()
        throws Exception
    {
        assertEquals( "png", ImageHelper.getFormatByMimeType( "image/png" ) );
        assertEquals( "JPEG", ImageHelper.getFormatByMimeType( "image/jpeg" ) );
        assertEquals( "gif", ImageHelper.getFormatByMimeType( "image/gif" ) );
    }

    @Test
    void getScaledInstance_preserves_grayscale_color_model()
    {
        final BufferedImage gray = paintGradient( new BufferedImage( 100, 100, BufferedImage.TYPE_BYTE_GRAY ) );

        final BufferedImage scaled = ImageHelper.getScaledInstance( gray, 50, 50 );

        assertEquals( BufferedImage.TYPE_BYTE_GRAY, scaled.getType() );
        assertEquals( ColorSpace.TYPE_GRAY, scaled.getColorModel().getColorSpace().getType() );
        assertEquals( 1, scaled.getColorModel().getNumComponents() );

        // Scaled gradient should still be near-monotone (R=G=B), with no chroma noise.
        final int rgb = scaled.getRGB( scaled.getWidth() / 2, scaled.getHeight() / 2 );
        final int r = ( rgb >> 16 ) & 0xFF;
        final int g = ( rgb >> 8 ) & 0xFF;
        final int b = rgb & 0xFF;
        assertTrue( r == g && g == b, "Expected grayscale pixel, got rgb=" + r + "," + g + "," + b );
    }

    @Test
    void getScaledInstance_preserves_rgb_color_model()
    {
        final BufferedImage rgb = paintGradient( new BufferedImage( 100, 100, BufferedImage.TYPE_INT_RGB ) );

        final BufferedImage scaled = ImageHelper.getScaledInstance( rgb, 50, 50 );

        assertEquals( BufferedImage.TYPE_INT_RGB, scaled.getType() );
        assertEquals( ColorSpace.TYPE_RGB, scaled.getColorModel().getColorSpace().getType() );
    }

    @Test
    void createCompatibleImage_matches_source_color_model()
    {
        final BufferedImage gray = new BufferedImage( 10, 10, BufferedImage.TYPE_BYTE_GRAY );
        final BufferedImage compatible = ImageHelper.createCompatibleImage( gray, 4, 6 );

        assertEquals( 4, compatible.getWidth() );
        assertEquals( 6, compatible.getHeight() );
        assertEquals( gray.getColorModel(), compatible.getColorModel() );
    }

    private static BufferedImage paintGradient( final BufferedImage image )
    {
        final Graphics2D g = image.createGraphics();
        try
        {
            g.setPaint( new GradientPaint( 0, 0, Color.BLACK, image.getWidth(), image.getHeight(), Color.WHITE ) );
            g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
        }
        finally
        {
            g.dispose();
        }
        return image;
    }
}
