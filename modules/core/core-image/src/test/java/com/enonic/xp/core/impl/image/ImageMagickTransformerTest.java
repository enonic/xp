package com.enonic.xp.core.impl.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ImageMagickTransformerTest
{
    @TempDir
    Path temporaryFolder;

    @Test
    void nativeStagesPassRasterFilesWithoutJavaPixelReads() throws Exception
    {
        final var bytes = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write( image(), "png", bytes );
        final var decoder = new ImageMagickDecoder( "embedded", temporaryFolder, 30, 10000, 100000 );
        try (var source = decoder.open( com.google.common.io.ByteSource.wrap( bytes.toByteArray() ) ))
        {
            final var decoded = org.mockito.Mockito.spy( source.raster() );
            try (var result = transformer().transform( decoded, plan( 32, 24, params( "invert" ), 10000 ) ))
            {
                final var transformed = org.mockito.Mockito.spy( result.raster() );
                final var output = new java.io.ByteArrayOutputStream();
                new ImageMagickEncoder( 30, temporaryFolder ).write( transformed, "png", 85, false, output );
                assertEquals( 32, javax.imageio.ImageIO.read( new java.io.ByteArrayInputStream( output.toByteArray() ) ).getWidth() );
                org.mockito.Mockito.verify( decoded, org.mockito.Mockito.never() ).read();
                org.mockito.Mockito.verify( transformed, org.mockito.Mockito.never() ).read();
            }
        }
        assertEmpty( temporaryFolder );
    }

    @ParameterizedTest
    @ValueSource(strings = {"block(3)", "blur(2)", "border(2,0x00ff00)", "bump", "colorize(1,0.5,0)", "edge", "emboss",
        "fliph", "flipv", "rotate90", "rotate180", "rotate270", "gamma(1.2)", "grayscale", "hsbadjust(0.2,0.1,-0.1)",
        "hsbcolorize(0x00ff00)", "invert", "rgbadjust(0.2,-0.1,0)", "rounded(8)", "rounded(8,2,0x00ff00)", "sepia(20)", "sharpen"})
    void allRegisteredFiltersRunInEmbeddedImageMagick( final String filter )
        throws Exception
    {
        final var params = params( filter );
        final var plan = plan( 32, 24, params, 10000 );
        final BufferedImage result = transformer().apply( image(), plan );
        assertEquals( plan.width(), result.getWidth() );
        assertEquals( plan.height(), result.getHeight() );
        assertTrue( result.getColorModel().hasAlpha() );
        assertEmpty( temporaryFolder );
    }

    @ParameterizedTest
    @CsvSource({"width,16,0,16,12", "height,12,0,16,12", "max,16,0,16,12", "square,10,0,10,10",
        "wide,16,20,16,12", "wide,16,8,16,8", "block,16,9,16,9"})
    void scaleUsesXpGeometry( final String name, final int first, final int second, final int width, final int height )
        throws Exception
    {
        final var params = new NormalizedImageParams( requestBuilder().mimeType( "image/png" )
            .scaleParams( new ScaleParams( name, second == 0 ? new Object[]{first} : new Object[]{first, second} ) ).build() );
        final var plan = plan( 32, 24, params, 10000 );
        final var result = transformer().apply( image(), plan );
        assertEquals( width, result.getWidth() );
        assertEquals( height, result.getHeight() );
    }

    @ParameterizedTest
    @EnumSource(ImageOrientation.class)
    void orientationMatchesExifPixelPositions( final ImageOrientation orientation )
        throws Exception
    {
        final BufferedImage image = new BufferedImage( 3, 2, BufferedImage.TYPE_INT_ARGB );
        for ( int i = 0; i < 6; i++ ) image.setRGB( i % 3, i / 3, 0xff000000 | ( i + 1 ) * 0x10101 );
        final var params = new NormalizedImageParams( requestBuilder().mimeType( "image/png" )
            .orientation( orientation ).build() );
        final var result = transformer().apply( image, plan( 3, 2, params, 10000 ) );
        final int[][] expected = {{1, 2, 3, 4, 5, 6}, {3, 2, 1, 6, 5, 4}, {6, 5, 4, 3, 2, 1}, {4, 5, 6, 1, 2, 3},
            {1, 4, 2, 5, 3, 6}, {4, 1, 5, 2, 6, 3}, {6, 3, 5, 2, 4, 1}, {3, 6, 2, 5, 1, 4}};
        assertEquals( orientation.getValue() <= 4 ? 3 : 2, result.getWidth() );
        for ( int i = 0; i < 6; i++ )
        {
            assertEquals( expected[orientation.getValue() - 1][i], result.getRGB( i % result.getWidth(), i / result.getWidth() ) & 255 );
        }
    }

    @Test
    void croppingAndFocalPointChooseExpectedRegion()
        throws Exception
    {
        final BufferedImage image = new BufferedImage( 8, 4, BufferedImage.TYPE_INT_ARGB );
        for ( int y = 0; y < 4; y++ ) for ( int x = 0; x < 8; x++ ) image.setRGB( x, y, 0xff000000 | x * 0x202020 );
        final var params = new NormalizedImageParams( requestBuilder().mimeType( "image/png" )
            .cropping( Cropping.create().left( 0.25 ).build() ).focalPoint( new FocalPoint( 1, 0.5 ) )
            .scaleParams( new ScaleParams( "block", new Object[]{2, 4} ) ).build() );
        final var result = transformer().apply( image, plan( 8, 4, params, 10000 ) );
        assertEquals( 2, result.getWidth() );
        assertEquals( 4, result.getHeight() );
        assertEquals( image.getRGB( 6, 1 ), result.getRGB( 0, 1 ) );
        assertEquals( image.getRGB( 7, 1 ), result.getRGB( 1, 1 ) );
    }

    @Test
    void roundedBorderPreservesTransparentCornersAndSourceAlpha()
        throws Exception
    {
        final var result = transformer().apply( image(), plan( 32, 24, params( "rounded(8,2,0x00ff00)" ), 10000 ) );
        assertEquals( 0, result.getRGB( 0, 0 ) >>> 24 );
        assertEquals( 0xff00ff00, result.getRGB( 16, 0 ) );
        assertEquals( 0xffff0000, result.getRGB( 16, 12 ) );
        final var noBorder = transformer().apply( image(), plan( 32, 24, params( "rounded(8)" ), 10000 ) );
        assertEquals( 0, noBorder.getRGB( 16, 2 ) >>> 24 );
    }

    @ParameterizedTest
    @ValueSource(strings = {"rounded(0)", "rounded(0,2)", "rounded(1000)"})
    void roundedRadiusExtremesRetainTheImageCenter( final String filter )
        throws Exception
    {
        final var result = transformer().apply( image(), plan( 32, 24, params( filter ), 10000 ) );
        assertEquals( 0xffff0000, result.getRGB( 16, 12 ) );
    }

    @Test
    void colorsAndFilterOrderAreAppliedBeforeFlattening()
        throws Exception
    {
        final var params = new NormalizedImageParams( requestBuilder().mimeType( "image/jpeg" )
            .filterParam( "invert;border(2,0x00ff00)" ).backgroundColor( 0x0000ff ).build() );
        final var result = transformer().apply( image(), plan( 32, 24, params, 10000 ) );
        assertFalse( result.getColorModel().hasAlpha() );
        assertEquals( 0xff00ff00, result.getRGB( 0, 0 ) );
        assertEquals( 0xff0000ff, result.getRGB( 3, 3 ) );
        assertEquals( 0xff00ffff, result.getRGB( 16, 12 ) );
    }

    @Test
    void intermediateResizeIsLimitedEvenWhenFinalCropIsSmall()
    {
        final var params = new NormalizedImageParams( requestBuilder().mimeType( "image/png" )
            .scaleParams( new ScaleParams( "block", new Object[]{10, 10} ) ).build() );
        assertThrows( IllegalArgumentException.class, () -> plan( 100, 1, params, 500 ) );
    }

    @ParameterizedTest
    @ValueSource(strings = {"blur(101)", "block(0)", "rounded(1001)", "gamma(NaN)", "rgbadjust(Infinity,0,0)", "-write('/tmp/escape')"})
    void invalidFiltersCannotBecomeNativeArguments( final String filter )
    {
        assertThrows( IllegalArgumentException.class, () -> plan( 32, 24, params( filter ), 10000 ) );
    }

    @Test
    void missingExecutableCleansUp()
        throws Exception
    {
        final var transformer = new ImageMagickTransformer( temporaryFolder.resolve( "missing" ).toString(), 1, temporaryFolder );
        assertThrows( IOException.class, () -> transformer.apply( image(), plan( 32, 24, params( "invert" ), 10000 ) ) );
        assertEmpty( temporaryFolder );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void timeoutKillsProcessAndCleansUp()
        throws Exception
    {
        final Path executable = temporaryFolder.resolve( "transformer" );
        final Path pidFile = temporaryFolder.resolve( "pid" );
        Files.writeString( executable, "#!/bin/sh\necho $$ > '" + pidFile + "'\nexec sleep 30\n" );
        assertTrue( executable.toFile().setExecutable( true, true ) );
        final Path work = temporaryFolder.resolve( "work" );
        final var transformer = new ImageMagickTransformer( executable.toString(), 1, work );
        assertTimeout( Duration.ofSeconds( 10 ), () -> {
            final IOException error = assertThrows( IOException.class,
                () -> transformer.apply( image(), plan( 32, 24, params( "invert" ), 10000 ) ) );
            assertTrue( error.getMessage().contains( "exceeded" ) );
        } );
        final long pid = Long.parseLong( Files.readString( pidFile ).trim() );
        assertTrue( ProcessHandle.of( pid ).isEmpty() || !ProcessHandle.of( pid ).orElseThrow().isAlive() );
        assertEmpty( work );
    }

    private ImageMagickTransformPlan plan( final int width, final int height, final NormalizedImageParams params, final long maxPixels )
    {
        final var config = mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        final var filters = new ImageFilterBuilderImpl();
        filters.activate( config );
        filters.build( params.getFilterParam() );
        final var scales = new ImageScaleFunctionBuilderImpl();
        scales.activate( config );
        return new ImageMagickTransformPlan( width, height, params,
            "full".equals( params.getScaleParams().getName() ) ? null :
                scales.build( params.getScaleParams(), ImageServiceImpl.toCropRelativeFocalPoint( params.getFocalPoint(), params.getCropping() ) ),
            maxPixels );
    }

    private static ReadImageParams.Builder requestBuilder()
    {
        return ReadImageParams.newImageParams().contentId( com.enonic.xp.content.ContentId.from( "image" ) )
            .binaryReference( com.enonic.xp.util.BinaryReference.from( "source" ) );
    }

    private static NormalizedImageParams params( final String filter )
    {
        return new NormalizedImageParams( requestBuilder().mimeType( "image/png" ).filterParam( filter ).build() );
    }

    private ImageMagickTransformer transformer()
    {
        return new ImageMagickTransformer( "embedded", 30, temporaryFolder );
    }

    private static BufferedImage image()
    {
        final var image = new BufferedImage( 32, 24, BufferedImage.TYPE_INT_ARGB );
        final var graphics = image.createGraphics();
        graphics.setColor( Color.RED );
        graphics.fillRect( 4, 4, 24, 16 );
        graphics.dispose();
        return image;
    }

    private static void assertEmpty( final Path folder )
        throws IOException
    {
        try (var files = Files.list( folder )) { assertEquals( 0, files.count() ); }
    }
}
