package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ImageMagickEncoderTest
{
    @TempDir
    Path temporaryFolder;

    @Test
    void disabledAndInvalidParametersDoNotCreateFiles()
        throws Exception
    {
        final ImageMagickEncoder encoder = new ImageMagickEncoder( "", 1, temporaryFolder );
        assertThrows( IllegalArgumentException.class, () -> encoder.write( image(), "webp", 80, new ByteArrayOutputStream() ) );
        final ImageMagickEncoder enabled = new ImageMagickEncoder( "/nonexistent", 1, temporaryFolder );
        assertThrows( IllegalArgumentException.class, () -> enabled.write( image(), "png:/tmp/escape", 80, new ByteArrayOutputStream() ) );
        assertThrows( IllegalArgumentException.class, () -> enabled.write( image(), "webp", 101, new ByteArrayOutputStream() ) );
        assertEmpty( temporaryFolder );
    }

    @Test
    void missingExecutableCleansUp()
        throws Exception
    {
        final ImageMagickEncoder encoder = new ImageMagickEncoder( temporaryFolder.resolve( "missing" ).toString(), 1, temporaryFolder );
        assertThrows( IOException.class, () -> encoder.write( image(), "webp", 80, new ByteArrayOutputStream() ) );
        assertEmpty( temporaryFolder );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void failedProcessCleansUp()
        throws Exception
    {
        final Path executable = script( "exit 7" );
        final Path work = temporaryFolder.resolve( "work" );
        final ImageMagickEncoder encoder = new ImageMagickEncoder( executable.toString(), 1, work );
        final IOException error = assertThrows( IOException.class,
            () -> encoder.write( image(), "webp", 80, new ByteArrayOutputStream() ) );
        assertTrue( error.getMessage().contains( "exit 7" ) );
        assertEmpty( work );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void timeoutKillsProcessAndCleansUp()
        throws Exception
    {
        final Path pidFile = temporaryFolder.resolve( "pid" );
        final Path executable = script( "echo $$ > '" + pidFile + "'\nexec sleep 30" );
        final Path work = temporaryFolder.resolve( "work" );
        final ImageMagickEncoder encoder = new ImageMagickEncoder( executable.toString(), 1, work );
        assertTimeout( Duration.ofSeconds( 10 ), () -> {
            final IOException error = assertThrows( IOException.class,
                () -> encoder.write( image(), "webp", 80, new ByteArrayOutputStream() ) );
            assertTrue( error.getMessage().contains( "exceeded" ) );
        } );
        final long pid = Long.parseLong( Files.readString( pidFile ).trim() );
        assertTrue( ProcessHandle.of( pid ).isEmpty() || !ProcessHandle.of( pid ).orElseThrow().isAlive() );
        assertEmpty( work );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif"})
    @EnabledOnOs({OS.LINUX, OS.WINDOWS})
    void nativeEncoderProducesRequestedFormat( final String format )
        throws Exception
    {
        final String platform = EmbeddedImageMagick.platform( System.getProperty( "os.name" ), System.getProperty( "os.arch" ) );
        assumeTrue( java.util.Set.of( "linux-x86_64", "windows-x86_64", "windows-aarch64" ).contains( platform ) );
        final ImageMagickEncoder encoder = new ImageMagickEncoder( 30, temporaryFolder );
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        encoder.write( image(), format, 80, output );
        final byte[] bytes = output.toByteArray();
        assertTrue( bytes.length > 12 );
        assertEquals( "webp".equals( format ) ? "RIFF" : "ftyp",
                      new String( bytes, "webp".equals( format ) ? 0 : 4, 4, StandardCharsets.US_ASCII ) );
        assertEquals( "webp".equals( format ) ? "WEBP" : "avif", new String( bytes, 8, 4, StandardCharsets.US_ASCII ) );
        assertEmpty( temporaryFolder );
    }

    private Path script( final String command )
        throws IOException
    {
        final Path path = temporaryFolder.resolve( "encoder" );
        Files.writeString( path, "#!/bin/sh\n" + command + "\n" );
        assertTrue( path.toFile().setExecutable( true, true ) );
        return path;
    }

    private static BufferedImage image()
    {
        final BufferedImage image = new BufferedImage( 8, 8, BufferedImage.TYPE_INT_ARGB );
        image.setRGB( 4, 4, 0xffff0000 );
        return image;
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
