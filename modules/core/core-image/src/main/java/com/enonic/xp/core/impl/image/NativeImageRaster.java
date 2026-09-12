package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** An 8-bit, interleaved, straight-alpha sRGB RGBA file owned by its native stage. */
record NativeImageRaster(Path path, int width, int height, boolean alpha)
{
    private static final int TRANSFER_PIXELS = 4096;

    NativeImageRaster
    {
        if ( width < 1 || height < 1 )
        {
            throw new IllegalArgumentException( "Invalid raster dimensions" );
        }
        Math.multiplyExact( (long) width * height, 4 );
        path = path.toAbsolutePath();
    }

    static NativeImageRaster validate( final Path path, final int width, final int height ) throws IOException
    {
        return validate( path, width, height, true );
    }

    static NativeImageRaster validate( final Path path, final int width, final int height, final boolean alpha ) throws IOException
    {
        final NativeImageRaster raster = new NativeImageRaster( path, width, height, alpha );
        raster.checkLength();
        return raster;
    }

    private void checkLength() throws IOException
    {
        if ( !Files.isRegularFile( path ) || Files.size( path ) != (long) width * height * 4 )
        {
            throw new IOException( "Unexpected intermediate raster length" );
        }
    }

    List<String> inputArguments() throws IOException
    {
        checkLength();
        final List<String> arguments = new ArrayList<>( List.of( "-size", width + "x" + height,
            "-depth", "8", "-interlace", "None", "-colorspace", "sRGB", "RGBA:" + path ) );
        if ( !alpha )
        {
            arguments.addAll( List.of( "-alpha", "off" ) );
        }
        return arguments;
    }

    static List<String> outputArguments( final Path path, final boolean alpha )
    {
        return List.of( "-colorspace", "sRGB", "-alpha", alpha ? "on" : "opaque",
            "-depth", "8", "-interlace", "None", "RGBA:" + path.toAbsolutePath() );
    }

    static NativeImageRaster write( final BufferedImage image, final Path path ) throws IOException
    {
        final NativeImageRaster raster = new NativeImageRaster( path, image.getWidth(), image.getHeight(), image.getColorModel().hasAlpha() );
        final int[] pixels = new int[Math.min( raster.width, TRANSFER_PIXELS )];
        final byte[] bytes = new byte[pixels.length * 4];
        try (var output = new BufferedOutputStream( Files.newOutputStream( raster.path ) ))
        {
            for ( int y = 0; y < raster.height; y++ )
            {
                for ( int x = 0; x < raster.width; x += pixels.length )
                {
                    checkInterrupted();
                    final int count = Math.min( pixels.length, raster.width - x );
                    // getRGB normalizes color space, channel layout, palette and premultiplied alpha.
                    image.getRGB( x, y, count, 1, pixels, 0, count );
                    for ( int i = 0; i < count; i++ )
                    {
                        final int pixel = pixels[i];
                        bytes[i * 4] = (byte) ( pixel >>> 16 );
                        bytes[i * 4 + 1] = (byte) ( pixel >>> 8 );
                        bytes[i * 4 + 2] = (byte) pixel;
                        bytes[i * 4 + 3] = (byte) ( pixel >>> 24 );
                    }
                    output.write( bytes, 0, count * 4 );
                }
            }
        }
        return raster;
    }

    BufferedImage read() throws IOException
    {
        checkLength();
        final BufferedImage image = new BufferedImage( width, height, alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
        final int[] pixels = new int[Math.min( width, TRANSFER_PIXELS )];
        final byte[] bytes = new byte[pixels.length * 4];
        try (var input = new BufferedInputStream( Files.newInputStream( path ) ))
        {
            for ( int y = 0; y < height; y++ )
            {
                for ( int x = 0; x < width; x += pixels.length )
                {
                    checkInterrupted();
                    final int count = Math.min( pixels.length, width - x );
                    if ( input.readNBytes( bytes, 0, count * 4 ) != count * 4 )
                    {
                        throw new IOException( "Truncated intermediate raster" );
                    }
                    for ( int i = 0; i < count; i++ )
                    {
                        pixels[i] = ( bytes[i * 4 + 3] & 255 ) << 24 | ( bytes[i * 4] & 255 ) << 16 |
                            ( bytes[i * 4 + 1] & 255 ) << 8 | ( bytes[i * 4 + 2] & 255 );
                    }
                    image.setRGB( x, y, count, 1, pixels, 0, count );
                }
            }
            if ( input.read() != -1 )
            {
                throw new IOException( "Unexpected intermediate raster length" );
            }
        }
        return image;
    }

    private static void checkInterrupted() throws IOException
    {
        if ( Thread.currentThread().isInterrupted() )
        {
            throw new IOException( "Intermediate raster transfer interrupted" );
        }
    }
}
