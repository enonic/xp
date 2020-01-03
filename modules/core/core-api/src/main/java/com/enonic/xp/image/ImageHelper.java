package com.enonic.xp.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.Exceptions;

@PublicApi
public final class ImageHelper
{
    private ImageHelper()
    {
    }

    public static String createImagePlaceholder( final int width, final int height )
    {
        try
        {
            final BufferedImage image = createImage( width, height, true );
            final byte[] bytes = writeImage( image, "png", 0 );

            return "data:image/png;base64," + Base64.getEncoder().encodeToString( bytes );
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRuntime( "Failed to create image placeholder" ).withCause( e );
        }
    }

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
        final MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream( out );
        writer.setOutput( output );
        final ImageWriteParam params = writer.getDefaultWriteParam();
        setCompressionQuality( params, quality );
        writer.write( null, new IIOImage( image, null, null ), params );
        writer.dispose();
        output.close();
    }

    private static void setCompressionQuality( ImageWriteParam params, int quality )
    {
        if ( quality > 100 )
        {
            quality = 100;
        }

        try
        {
            params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            params.setCompressionQuality( (float) quality / 100f );
        }
        catch ( Exception e )
        {
            // DO nothing since compression not supported
        }
    }

    public static BufferedImage createImage( BufferedImage src, boolean hasAlpha )
    {
        return createImage( src.getWidth(), src.getHeight(), hasAlpha );
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }


    public static BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight )
    {
        int width = Math.max( 1, targetWidth );
        int height = Math.max( 1, targetHeight );

        Image scaledImage = img.getScaledInstance( width, height, Image.SCALE_SMOOTH );
        final boolean hasAlpha = img.getTransparency() != Transparency.OPAQUE;
        BufferedImage targetImage = createImage( width, height, hasAlpha );
        Graphics g = targetImage.createGraphics();
        g.drawImage( scaledImage, 0, 0, null );
        g.dispose();
        return targetImage;
    }

    public static BufferedImage removeAlphaChannel( final BufferedImage img, final int color )
    {
        if ( !img.getColorModel().hasAlpha() )
        {
            return img;
        }

        final BufferedImage target = createImage( img, false );
        final Graphics2D g = target.createGraphics();
        g.setColor( new Color( color, false ) );
        g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
        g.drawImage( img, 0, 0, null );
        g.dispose();

        return target;
    }

    public static boolean supportsAlphaChannel( final String format )
    {
        return format.equals( "png" );
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
