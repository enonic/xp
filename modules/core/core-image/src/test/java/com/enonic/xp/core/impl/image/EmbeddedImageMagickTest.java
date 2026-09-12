package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmbeddedImageMagickTest
{
    @TempDir
    Path temporaryFolder;

    @Test
    void extractsPortableFilesAndRejectsTraversal()
        throws Exception
    {
        final Path directory = Files.createDirectory( temporaryFolder.resolve( "native" ) );
        EmbeddedImageMagick.extractZip( archive( "lib/codec.dll" ), directory );
        assertArrayEquals( new byte[]{1, 2, 3}, Files.readAllBytes( directory.resolve( "lib/codec.dll" ) ) );
        assertThrows( IOException.class, () -> EmbeddedImageMagick.extractZip( archive( "../outside" ), directory ) );
        assertFalse( Files.exists( temporaryFolder.resolve( "outside" ) ) );
    }

    @Test
    void recognizesPlatformAliases()
    {
        assertEquals( "osx-aarch64", EmbeddedImageMagick.platform( "Mac OS X", "aarch64" ) );
        assertEquals( "osx-aarch64", EmbeddedImageMagick.platform( "Mac OS X", "arm64" ) );
        assertEquals( "linux-x86_64", EmbeddedImageMagick.platform( "Linux", "amd64" ) );
        assertEquals( "linux-aarch64", EmbeddedImageMagick.platform( "Linux", "arm64" ) );
        assertEquals( "linux-aarch64", EmbeddedImageMagick.platform( "Linux", "aarch64" ) );
        assertEquals( "windows-x86_64", EmbeddedImageMagick.platform( "Windows 11", "x86_64" ) );
        assertEquals( "windows-aarch64", EmbeddedImageMagick.platform( "Windows 11", "arm64" ) );
    }

    @Test
    void includesArmDistributionsOnEveryBuildPlatform()
        throws Exception
    {
        try (var linux = EmbeddedImageMagick.class.getResourceAsStream( "/native/imagemagick/linux-aarch64.AppImage" );
             var windows = EmbeddedImageMagick.class.getResourceAsStream( "/native/imagemagick/windows-aarch64.zip" );
             var mac = EmbeddedImageMagick.class.getResourceAsStream( "/native/imagemagick/osx-aarch64.zip" ))
        {
            assertNotNull( linux );
            assertNotNull( windows );
            assertNotNull( mac );
            final byte[] header = linux.readNBytes( 20 );
            assertEquals( 20, header.length );
            assertArrayEquals( new byte[]{0x7f, 'E', 'L', 'F'}, java.util.Arrays.copyOf( header, 4 ) );
            assertEquals( 183, java.nio.ByteBuffer.wrap( header ).order( java.nio.ByteOrder.LITTLE_ENDIAN ).getShort( 18 ) );
        }
    }

    @Test
    void removesSelfUpdaterBeforeLaunchingBundledEncoder()
        throws Exception
    {
        final Path binaries = Files.createDirectories( temporaryFolder.resolve( "bin" ) );
        final Path updater = Files.writeString( binaries.resolve( "10-self-updater.hook" ), "update" );
        final Path codecHook = Files.writeString( binaries.resolve( "codec.hook" ), "codec setup" );
        EmbeddedImageMagick.removeUpdateHooks( temporaryFolder );
        assertFalse( Files.exists( updater ) );
        assertTrue( Files.exists( codecHook ) );
    }

    private Path archive( final String name )
        throws IOException
    {
        final Path path = temporaryFolder.resolve( "test.zip" );
        try (ZipOutputStream output = new ZipOutputStream( Files.newOutputStream( path ) ))
        {
            output.putNextEntry( new ZipEntry( name ) );
            output.write( new byte[]{1, 2, 3} );
            output.closeEntry();
        }
        return path;
    }
}
