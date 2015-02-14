package com.enonic.xp.core.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class ImageHelper
{
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
        writer.setOutput( new MemoryCacheImageOutputStream( out ) );
        final ImageWriteParam params = writer.getDefaultWriteParam();
        setCompressionQuality( params, quality );
        writer.write( null, new IIOImage( image, null, null ), params );
        writer.dispose();
    }

    private static void setCompressionQuality( ImageWriteParam params, int quality )
    {
        if ( quality <= 0 )
        {
            quality = 1;
        }

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
}
