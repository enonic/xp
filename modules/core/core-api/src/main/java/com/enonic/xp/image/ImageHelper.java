package com.enonic.xp.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class ImageHelper
{
    private ImageHelper()
    {
    }

    public static String createImagePlaceholder( final int width, final int height )
    {
        return new ImagePlaceholderFactory( width, height ).create();
    }

    public static ImageWriter getWriterByFormat( final String format )
    {
        final Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName( format );
        if ( iter.hasNext() )
        {
            return iter.next();
        }
        else
        {
            throw new IllegalArgumentException( "Image format [" + format + "] is not supported" );
        }
    }

    public static String getFormatByMimeType( final String mimeType )
        throws IOException
    {
        final Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType( mimeType );
        if ( !i.hasNext() )
        {
            throw new IllegalArgumentException( "The image-based media type " + mimeType + " is not supported for writing" );
        }
        return i.next().getOriginatingProvider().getFormatNames()[0];
    }

    public static void writeImage( OutputStream out, final BufferedImage image, final String format, final int quality )
        throws IOException
    {
        writeImage( out, image, format, quality, false );
    }

    public static void writeImage( OutputStream out, final BufferedImage image, final String format, final int quality,
                                   boolean progressive )
        throws IOException
    {
        final ImageWriter writer = getWriterByFormat( format );
        try (ImageOutputStream output = new MemoryCacheImageOutputStream( out ))
        {
            writer.setOutput( output );
            final ImageWriteParam params = writer.getDefaultWriteParam();
            if ( progressive )
            {
                params.setProgressiveMode( ImageWriteParam.MODE_DEFAULT );
            }
            setCompressionQuality( params, quality );
            writer.write( null, new IIOImage( image, null, null ), params );
        }
        finally
        {
            writer.dispose();
        }
    }

    private static void setCompressionQuality( ImageWriteParam params, int quality )
    {
        if ( quality == -1 )
        {
            return;
        }
        try
        {
            params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            params.setCompressionQuality( quality / 100f );
        }
        catch ( Exception e )
        {
            // DO nothing since compression not supported
        }
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }

    public static BufferedImage getScaledInstance( BufferedImage img, int width, int height )
    {
        // The legacy path used Image.getScaledInstance(SCALE_SMOOTH), which routes through Java's
        // color management and shifts brightness on non-sRGB profiles (e.g. grayscale +55 levels,
        // issue #7688), and degenerates to nearest-neighbor on upscale. We instead use a progressive
        // bilinear halving for downscale and a single bicubic step for the final / upscale pass.
        final int destType = chooseDestinationType( img );

        BufferedImage current = img;
        int cw = current.getWidth();
        int ch = current.getHeight();
        while ( cw > width * 2 || ch > height * 2 )
        {
            final int nw = Math.max( width, cw / 2 );
            final int nh = Math.max( height, ch / 2 );
            current = scaleStep( current, nw, nh, destType, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
            cw = nw;
            ch = nh;
        }
        return scaleStep( current, width, height, destType, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
    }

    private static int chooseDestinationType( BufferedImage img )
    {
        // Grayscale is browser-universal and visibly cheaper to store, so preserve it through scaling.
        // CMYK and other non-sRGB profiles get flattened to sRGB because browser support for non-sRGB
        // JPEGs is unreliable (Firefox in particular).
        if ( img.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY )
        {
            return BufferedImage.TYPE_BYTE_GRAY;
        }
        return img.getTransparency() != Transparency.OPAQUE ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
    }

    private static BufferedImage scaleStep( BufferedImage src, int width, int height, int destType, Object interpolation )
    {
        final BufferedImage out = new BufferedImage( width, height, destType );
        final Graphics2D g = out.createGraphics();
        try
        {
            g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, interpolation );
            g.drawImage( src, 0, 0, width, height, null );
        }
        finally
        {
            g.dispose();
        }
        return out;
    }

    public static BufferedImage removeAlphaChannel( final BufferedImage img, final int color )
    {
        if ( !img.getColorModel().hasAlpha() )
        {
            return img;
        }

        final BufferedImage targetImage = createImage( img.getWidth(), img.getHeight(), false );
        final Graphics g = targetImage.createGraphics();
        try
        {
            g.setColor( new Color( color, false ) );
            g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
            g.drawImage( img, 0, 0, null );
        }
        finally
        {
            g.dispose();
        }

        return targetImage;
    }

    public static BufferedImage scaleSquare( final BufferedImage source, final int size )
    {
        return scaleSquare( source, size, 0.5, 0.5 );
    }

    public static BufferedImage scaleSquare( final BufferedImage source, final int size, final double xOffset, final double yOffset )
    {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage cropped;
        if ( width < height )
        {
            int heightDiff = height - width;
            int offset = (int) ( height * yOffset ) - ( width / 2 ); // center offset
            offset = inRange( offset, 0, heightDiff ); // adjust to view limits

            cropped = source.getSubimage( 0, offset, width, width );
        }
        else
        {
            int widthDiff = width - height;
            int offset = (int) ( width * xOffset ) - ( height / 2 ); // center offset
            offset = inRange( offset, 0, widthDiff ); // adjust to view limits

            cropped = source.getSubimage( offset, 0, height, height );
        }

        return getScaledInstance( cropped, size, size );
    }

    private static int inRange( final int value, final int min, final int max )
    {
        return Math.max( Math.min( value, max ), min );
    }
}
