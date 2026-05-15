package com.enonic.xp.image;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void getScaledInstance_preserves_grayscale_brightness()
    {
        // Fill a gray image with a known raw sample ramp 0..255 and verify the mean raw sample value
        // survives the downscale. The legacy SCALE_SMOOTH path shifted this by ~+55 levels (#7688).
        final BufferedImage gray = new BufferedImage( 256, 32, BufferedImage.TYPE_BYTE_GRAY );
        for ( int x = 0; x < 256; x++ )
        {
            for ( int y = 0; y < 32; y++ )
            {
                gray.getRaster().setSample( x, y, 0, x );
            }
        }

        final BufferedImage scaled = ImageHelper.getScaledInstance( gray, 128, 16 );

        assertEquals( BufferedImage.TYPE_BYTE_GRAY, scaled.getType() );
        assertEquals( 127.5, meanRawSample( scaled ), 2.0, "raw gray sample mean should survive scaling" );
    }

    @Test
    void getScaledInstance_preserves_alpha_for_argb_source()
    {
        final BufferedImage argb = new BufferedImage( 80, 80, BufferedImage.TYPE_INT_ARGB );
        final Graphics2D g = argb.createGraphics();
        try
        {
            g.setColor( new Color( 255, 0, 0, 128 ) );
            g.fillRect( 0, 0, 80, 80 );
        }
        finally
        {
            g.dispose();
        }

        final BufferedImage scaled = ImageHelper.getScaledInstance( argb, 40, 40 );

        assertEquals( BufferedImage.TYPE_INT_ARGB, scaled.getType() );
        assertTrue( scaled.getColorModel().hasAlpha(), "alpha channel should be preserved" );
    }

    @Test
    void getScaledInstance_preserves_alpha_for_grayscale_alpha_source()
    {
        // Grayscale color space with an alpha channel (e.g. grayscale PNGs). The destination
        // type chooser must route this to an alpha-capable destination rather than TYPE_BYTE_GRAY,
        // otherwise transparent pixels would be flattened into an opaque thumbnail.
        final ColorSpace grayCs = ColorSpace.getInstance( ColorSpace.CS_GRAY );
        final ComponentColorModel grayAlphaCm =
            new ComponentColorModel( grayCs, new int[]{8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE );
        final WritableRaster raster = Raster.createWritableRaster(
            new PixelInterleavedSampleModel( DataBuffer.TYPE_BYTE, 40, 40, 2, 40 * 2, new int[]{0, 1} ), new Point() );
        final BufferedImage grayAlpha = new BufferedImage( grayAlphaCm, raster, false, null );

        // Fill: left half opaque mid-gray, right half fully transparent.
        for ( int y = 0; y < 40; y++ )
        {
            for ( int x = 0; x < 40; x++ )
            {
                raster.setSample( x, y, 0, 128 );
                raster.setSample( x, y, 1, x < 20 ? 255 : 0 );
            }
        }

        final BufferedImage scaled = ImageHelper.getScaledInstance( grayAlpha, 20, 20 );

        assertThat( scaled.getColorModel().hasAlpha() ).as( "alpha channel must be preserved for grayscale+alpha source" ).isTrue();
        final int rightAlpha = ( scaled.getRGB( scaled.getWidth() - 1, scaled.getHeight() / 2 ) >>> 24 ) & 0xFF;
        assertThat( rightAlpha ).as( "transparent source pixel must remain transparent after scaling" ).isLessThanOrEqualTo( 8 );
    }

    @Test
    void getScaledInstance_rejects_non_positive_dimensions()
    {
        final BufferedImage rgb = new BufferedImage( 40, 40, BufferedImage.TYPE_INT_RGB );

        assertThatThrownBy( () -> ImageHelper.getScaledInstance( rgb, -1, 20 ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> ImageHelper.getScaledInstance( rgb, 20, -1 ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> ImageHelper.getScaledInstance( rgb, 0, 20 ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> ImageHelper.getScaledInstance( rgb, 20, 0 ) ).isInstanceOf( IllegalArgumentException.class );
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
    void getScaledInstance_upscale_interpolates_not_nearest_neighbor()
    {
        // 4-pixel ramp upscaled 16x — nearest-neighbor would produce only the four input values.
        // Bicubic interpolation should give a continuous range of in-between values.
        final BufferedImage ramp = new BufferedImage( 4, 1, BufferedImage.TYPE_INT_RGB );
        ramp.setRGB( 0, 0, 0x000000 );
        ramp.setRGB( 1, 0, 0x404040 );
        ramp.setRGB( 2, 0, 0xC0C0C0 );
        ramp.setRGB( 3, 0, 0xFFFFFF );

        final BufferedImage upscaled = ImageHelper.getScaledInstance( ramp, 64, 16 );

        int distinct = 0;
        boolean[] seen = new boolean[256];
        for ( int x = 0; x < upscaled.getWidth(); x++ )
        {
            final int v = upscaled.getRGB( x, 0 ) & 0xFF;
            if ( !seen[v] )
            {
                seen[v] = true;
                distinct++;
            }
        }
        assertTrue( distinct > 8, "expected smooth interpolation, got only " + distinct + " distinct values" );
    }

    private static double meanRawSample( final BufferedImage img )
    {
        long sum = 0;
        long count = 0;
        for ( int y = 0; y < img.getHeight(); y++ )
        {
            for ( int x = 0; x < img.getWidth(); x++ )
            {
                sum += img.getRaster().getSample( x, y, 0 );
                count++;
            }
        }
        return sum / (double) count;
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
