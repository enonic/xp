package com.enonic.xp.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.util.Exceptions;

public final class ImageHelper
{
    private ImageHelper()
    {
    }

    public static String createImagePlaceholder( final int width, final int height )
    {
        return new ImagePlaceholderFactory( width, height ).create();
    }

    @Deprecated
    public static BufferedImage toBufferedImage( final InputStream inputStream )
    {
        try
        {
            return ImageIO.read( inputStream );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRuntime( "Failed to read BufferedImage from InputStream" ).withCause( e );
        }
    }

    @Deprecated
    public static ByteSource toByteSource( final BufferedImage image, final String format )
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            ImageIO.write( image, format, output );
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRuntime( "Failed to report BufferedImage to InputStream" ).withCause( e );
        }
        return ByteSource.wrap( output.toByteArray() );
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

    @Deprecated
    public static byte[] serializeImage( final BufferedImage bufferedImage, final String mimeType, final int quality )
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeImage( out, bufferedImage, getFormatByMimeType( mimeType ), quality == 0 ? -1 : quality );
        return out.toByteArray();
    }

    @Deprecated
    public static byte[] writeImage( final BufferedImage image, final String format, final int quality )
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeImage( out, image, format, quality );
        return out.toByteArray();
    }

    public static void writeImage( OutputStream out, final BufferedImage image, final String format, final int quality )
        throws IOException
    {
        final ImageWriter writer = getWriterByFormat( format );
        try (ImageOutputStream output = new MemoryCacheImageOutputStream( out ))
        {
            writer.setOutput( output );
            final ImageWriteParam params = writer.getDefaultWriteParam();
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

    @Deprecated
    public static BufferedImage createImage( BufferedImage src, boolean hasAlpha )
    {
        return createImage( src.getWidth(), src.getHeight(), hasAlpha );
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }

    public static BufferedImage getScaledInstance( BufferedImage img, int width, int height )
    {
        Image scaledImage = img.getScaledInstance( width, height, Image.SCALE_SMOOTH );
        final boolean hasAlpha = img.getTransparency() != Transparency.OPAQUE;
        BufferedImage targetImage = createImage( width, height, hasAlpha );
        Graphics g = targetImage.createGraphics();
        try
        {
            g.drawImage( scaledImage, 0, 0, null );
        }
        finally
        {
            g.dispose();
        }
        return targetImage;
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

    @Deprecated
    public static boolean supportsAlphaChannel( final String format )
    {
        return "png".equals( format );
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
