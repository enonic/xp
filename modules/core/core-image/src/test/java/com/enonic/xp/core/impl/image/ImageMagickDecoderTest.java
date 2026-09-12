package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageMagickDecoderTest
{
    @TempDir
    Path temporaryFolder;

    @ParameterizedTest
    @ValueSource(strings = {"png", "jpeg", "webp", "avif"})
    void decodesFirstRasterWithEmbeddedDistribution( final String format )
        throws Exception
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        new ImageMagickEncoder( 30, temporaryFolder ).write( image(), format, 85, bytes );
        try (var source = decoder( 1000, 1_048_576 ).open( ByteSource.wrap( bytes.toByteArray() ) ))
        {
            assertEquals( 32, source.width() );
            assertEquals( 24, source.height() );
            final BufferedImage result = source.read();
            assertEquals( 32, result.getWidth() );
            assertEquals( 24, result.getHeight() );
            if ( "png".equals( format ) )
            {
                assertEquals( 0, result.getRGB( 0, 0 ) >>> 24 );
                assertEquals( 0xffff0000, result.getRGB( 6, 6 ) );
            }
        }
        assertEmpty( temporaryFolder );
    }

    @Test
    void rejectsOversizedSourceBeforeRasterAllocationAndCleansUp()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> decoder( 100, 1_048_576 ).open( png() ) );
        assertEmpty( temporaryFolder );
        assertThrows( IllegalArgumentException.class, () -> decoder( 1000, 8 ).open( png() ) );
        assertEmpty( temporaryFolder );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void rejectsStaticAndAnimatedGifBeforeStartingProcess( final int frames )
        throws Exception
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final var writer = ImageIO.getImageWritersByFormatName( "gif" ).next();
        try (var output = ImageIO.createImageOutputStream( bytes ))
        {
            writer.setOutput( output );
            writer.prepareWriteSequence( null );
            writer.writeToSequence( new javax.imageio.IIOImage( image(), null, null ), null );
            if ( frames > 1 )
            {
                writer.writeToSequence( new javax.imageio.IIOImage( new BufferedImage( 8, 8, BufferedImage.TYPE_INT_RGB ), null, null ), null );
            }
            writer.endWriteSequence();
        }
        finally
        {
            writer.dispose();
        }
        final var decoder = new ImageMagickDecoder( "/missing", temporaryFolder, 1, 1000, 1_048_576 );
        assertThrows( IllegalArgumentException.class, () -> decoder.open( ByteSource.wrap( bytes.toByteArray() ) ) );
        assertEmpty( temporaryFolder );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<rect x='4' y='4' width='8' height='8' fill='red'/>",
        "<defs><linearGradient id='g'><stop stop-color='red'/></linearGradient></defs><rect fill='url(#g)'/>",
        "<image href='file:///etc/passwd'/>",
        "<image href='https://example.invalid/image.png'/>",
        "<image href='data:image/png;base64,AAAA'/>",
        "<g xml:base='file:///etc/'><use href='#passwd'/></g>",
        "<rect fill='url(file:///etc/passwd)'/>",
        "<style>rect { fill: u<!-- split -->rl(https://example.invalid/x); }</style>",
        "<style>@import 'https://example.invalid/x';</style>",
        "<style>rect { fill: u/**/rl(file:///etc/passwd); }</style>",
        "<script>alert(1)</script>",
        "<foreignObject/>"})
    void rejectsSvgBeforeStartingProcess( final String content )
        throws Exception
    {
        final var decoder = new ImageMagickDecoder( "/missing", temporaryFolder, 1, 1000, 1_048_576 );
        assertThrows( IllegalArgumentException.class, () -> decoder.open( ByteSource.wrap( svg( content ) ) ) );
        assertEmpty( temporaryFolder );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<!DOCTYPE svg [<!ENTITY xxe SYSTEM 'file:///etc/passwd'>]><svg>&xxe;</svg>",
        "<?xml-stylesheet href='https://example.invalid/x'?><svg/>",
        "<html/>", "%!PS-Adobe-3.0", "not an image"})
    void rejectsUnsupportedSourcesAndXmlEntities( final String content )
        throws Exception
    {
        final var decoder = new ImageMagickDecoder( "/missing", temporaryFolder, 1, 1000, 1_048_576 );
        assertThrows( IllegalArgumentException.class,
            () -> decoder.open( ByteSource.wrap( content.getBytes( StandardCharsets.UTF_8 ) ) ) );
        assertEmpty( temporaryFolder );
    }

    @Test
    void missingExecutableCleansUp()
        throws Exception
    {
        final var decoder = new ImageMagickDecoder( temporaryFolder.resolve( "missing" ).toString(), temporaryFolder, 1, 1000, 10000 );
        assertThrows( IOException.class, () -> decoder.open( png() ) );
        assertEmpty( temporaryFolder );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void timeoutKillsProcessAndCleansUp()
        throws Exception
    {
        final Path pidFile = temporaryFolder.resolve( "pid" );
        final Path executable = temporaryFolder.resolve( "decoder" );
        Files.writeString( executable, "#!/bin/sh\necho $$ > '" + pidFile + "'\nexec sleep 30\n" );
        assertTrue( executable.toFile().setExecutable( true, true ) );
        final Path work = temporaryFolder.resolve( "work" );
        final var decoder = new ImageMagickDecoder( executable.toString(), work, 1, 1000, 10000 );
        assertTimeout( Duration.ofSeconds( 10 ), () -> {
            final IOException error = assertThrows( IOException.class, () -> decoder.open( png() ) );
            assertTrue( error.getMessage().contains( "exceeded" ) );
        } );
        final long pid = Long.parseLong( Files.readString( pidFile ).trim() );
        assertTrue( ProcessHandle.of( pid ).isEmpty() || !ProcessHandle.of( pid ).orElseThrow().isAlive() );
        assertEmpty( work );
    }

    private ImageMagickDecoder decoder( final long pixels, final long bytes )
    {
        return new ImageMagickDecoder( "embedded", temporaryFolder, 30, pixels, bytes );
    }

    private static ByteSource png()
        throws IOException
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write( image(), "png", bytes );
        return ByteSource.wrap( bytes.toByteArray() );
    }

    private static BufferedImage image()
    {
        final BufferedImage image = new BufferedImage( 32, 24, BufferedImage.TYPE_INT_ARGB );
        final var graphics = image.createGraphics();
        graphics.setColor( java.awt.Color.RED );
        graphics.fillRect( 4, 4, 8, 8 );
        graphics.dispose();
        return image;
    }

    private static byte[] svg( final String content )
    {
        return ( "<svg xmlns='http://www.w3.org/2000/svg' width='32' height='24'>" + content + "</svg>" )
            .getBytes( StandardCharsets.UTF_8 );
    }

    private static void assertEmpty( final Path folder )
        throws IOException
    {
        try (var files = Files.list( folder ))
        {
            assertEquals( 0, files.count() );
        }
    }
}
