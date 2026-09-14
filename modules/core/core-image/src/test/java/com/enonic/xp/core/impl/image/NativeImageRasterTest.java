package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeImageRasterTest
{
    @TempDir
    Path folder;

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
    void javaLayoutsAndSubimagesRoundTripAcrossTransferChunks( final int type ) throws Exception
    {
        final BufferedImage parent = new BufferedImage( 4105, 4, type );
        final BufferedImage source = parent.getSubimage( 2, 1, 4101, 2 );
        for ( int y = 0; y < source.getHeight(); y++ )
        {
            for ( int x = 0; x < source.getWidth(); x++ )
            {
                source.setRGB( x, y, ( ( x * 17 + y ) & 255 ) << 24 | ( ( x * 13 ) & 255 ) << 16 |
                    ( ( x * 7 ) & 255 ) << 8 | ( ( x + y * 31 ) & 255 ) );
            }
        }
        final NativeImageRaster raster = NativeImageRaster.write( source, folder.resolve( "image.rgba" ) );
        assertEquals( (long) source.getWidth() * source.getHeight() * 4, Files.size( raster.path() ) );
        final BufferedImage result = raster.read();
        assertEquals( source.getColorModel().hasAlpha(), result.getColorModel().hasAlpha() );
        assertArrayEquals( source.getRGB( 0, 0, source.getWidth(), source.getHeight(), null, 0, source.getWidth() ),
            result.getRGB( 0, 0, result.getWidth(), result.getHeight(), null, 0, result.getWidth() ) );
    }

    @Test
    void straightAlphaAndChannelOrderAreExplicit() throws Exception
    {
        final BufferedImage source = new BufferedImage( 2, 1, BufferedImage.TYPE_INT_ARGB );
        source.setRGB( 0, 0, 0x80112233 );
        source.setRGB( 1, 0, 0x00445566 );
        final NativeImageRaster raster = NativeImageRaster.write( source, folder.resolve( "image.rgba" ) );
        assertArrayEquals( new byte[]{0x11, 0x22, 0x33, (byte) 0x80, 0x44, 0x55, 0x66, 0}, Files.readAllBytes( raster.path() ) );
        assertEquals( 0x80112233, raster.read().getRGB( 0, 0 ) );
        assertEquals( 0x00445566, raster.read().getRGB( 1, 0 ) );
        final BufferedImage opaque = NativeImageRaster.validate( raster.path(), 2, 1, false ).read();
        assertFalse( opaque.getColorModel().hasAlpha() );
        assertEquals( 0xff112233, opaque.getRGB( 0, 0 ) );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3, 5, 8})
    void rejectsTruncatedOversizedAndAdditionalFrameData( final int length ) throws Exception
    {
        final Path file = Files.write( folder.resolve( "image.rgba" ), new byte[length] );
        assertThrows( IOException.class, () -> NativeImageRaster.validate( file, 1, 1 ) );
    }

    @Test
    void rechecksLengthBeforeJavaOrNativeConsumption() throws Exception
    {
        final Path file = Files.write( folder.resolve( "image.rgba" ), new byte[4] );
        final NativeImageRaster raster = NativeImageRaster.validate( file, 1, 1 );
        Files.write( file, new byte[3] );
        assertThrows( IOException.class, raster::read );
        assertThrows( IOException.class, raster::inputArguments );
        assertThrows( ArithmeticException.class, () -> new NativeImageRaster( file, Integer.MAX_VALUE, Integer.MAX_VALUE, true ) );
    }

    @Test
    void transfersPreserveInterruption() throws Exception
    {
        final BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
        final NativeImageRaster raster = NativeImageRaster.write( image, folder.resolve( "input.rgba" ) );
        Thread.currentThread().interrupt();
        try
        {
            assertThrows( IOException.class, raster::read );
            assertThrows( IOException.class, () -> NativeImageRaster.write( image, folder.resolve( "output.rgba" ) ) );
            assertTrue( Thread.currentThread().isInterrupted() );
        }
        finally
        {
            Thread.interrupted();
        }
    }
}
