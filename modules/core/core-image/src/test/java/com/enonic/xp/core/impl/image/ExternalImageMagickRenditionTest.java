package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.im4j.ImageMagick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs XP's own native rendition pipeline against an ImageMagick installed outside XP, proving
 * that an operator-supplied executable produces renditions through the same code path as the
 * bundled distribution. Skipped where no such installation is present.
 */
class ExternalImageMagickRenditionTest
    extends ImageMagickTestSupport
{
    private static final Path HOMEBREW = Path.of( "/opt/homebrew/bin/magick" );

    @TempDir
    Path temporaryFolder;

    @Test
    void encodesWebpThroughAnExternalInstallation()
        throws Exception
    {
        Assumptions.assumeTrue( Files.isExecutable( HOMEBREW ), "no Homebrew ImageMagick on this machine" );

        final ImageMagick external = external( HOMEBREW.toString() );
        final BufferedImage source = new BufferedImage( 64, 48, BufferedImage.TYPE_INT_RGB );
        source.createGraphics().fillRect( 0, 0, 64, 48 );

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        new ImageMagickEncoder( external, 30, temporaryFolder, MAX_DISK_BYTES ).write( source, "webp", 85, bytes );

        final byte[] webp = bytes.toByteArray();
        assertTrue( webp.length > 0 );
        assertEquals( "RIFF", new String( webp, 0, 4, StandardCharsets.US_ASCII ) );
        assertEquals( "WEBP", new String( webp, 8, 4, StandardCharsets.US_ASCII ) );
    }

    @Test
    void decodesThroughAnExternalInstallation()
        throws Exception
    {
        Assumptions.assumeTrue( Files.isExecutable( HOMEBREW ), "no Homebrew ImageMagick on this machine" );

        final ImageMagick external = external( HOMEBREW.toString() );
        final BufferedImage source = new BufferedImage( 40, 24, BufferedImage.TYPE_INT_RGB );

        final ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        new ImageMagickEncoder( external, 30, temporaryFolder, MAX_DISK_BYTES ).write( source, "png", 85, encoded );

        final Path png = Files.write( temporaryFolder.resolve( "decoded-source.png" ), encoded.toByteArray() );
        final ImageMagickDecoder decoder =
            new ImageMagickDecoder( external, temporaryFolder, 30, 40_000_000, 100_000_000, MAX_DISK_BYTES );

        try (var opened = decoder.open( png ))
        {
            assertEquals( 40, opened.width() );
            assertEquals( 24, opened.height() );
        }
    }
}
