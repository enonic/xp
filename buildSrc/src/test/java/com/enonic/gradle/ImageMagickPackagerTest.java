package com.enonic.gradle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageMagickPackagerTest
{
    @TempDir
    Path directory;

    @Test
    void locatesFilesystemWithoutExecutingAppImage() throws Exception
    {
        final byte[] filesystem = new byte[120];
        final var header = ByteBuffer.wrap( filesystem ).order( ByteOrder.LITTLE_ENDIAN );
        header.putInt( 0, 0x73717368 );
        header.putInt( 12, 131072 );
        header.putShort( 28, (short) 4 );
        header.putLong( 40, filesystem.length );
        final byte[] archive = new byte[200 + filesystem.length];
        System.arraycopy( filesystem, 0, archive, 200, filesystem.length );
        final Path input = Files.write( directory.resolve( "foreign.AppImage" ), archive );
        final Path output = directory.resolve( "filesystem" );
        ImageMagickPackager.copySquashFs( input, output );
        assertArrayEquals( filesystem, Files.readAllBytes( output ) );
        assertThrows( IOException.class, () -> ImageMagickPackager.copySquashFs(
            Files.writeString( directory.resolve( "invalid" ), "hsqs invalid header" ), output ) );
    }

    @Test
    void resolvesInternalLinksAndRejectsEscapesAndCycles() throws Exception
    {
        final Path file = directory.resolve( "binary" );
        assertEquals( file, ImageMagickPackager.resolve( "AppRun", Map.of( "bin/magick", file ),
            Map.of( "AppRun", "bin/convert", "bin/convert", "magick" ), new HashSet<>() ) );
        assertThrows( IOException.class, () -> ImageMagickPackager.resolve( "AppRun", Map.of(),
            Map.of( "AppRun", "../outside" ), new HashSet<>() ) );
        assertThrows( IOException.class, () -> ImageMagickPackager.resolve( "a", Map.of(),
            Map.of( "a", "b", "b", "a" ), new HashSet<>() ) );
        assertThrows( IOException.class, () -> ImageMagickPackager.safeName( "C:/outside" ) );
    }

    @Test
    void indexesExecutablePermissionsAndRemovesUpdater() throws Exception
    {
        Files.createDirectories( directory.resolve( "bin" ) );
        Files.writeString( directory.resolve( "bin/magick" ), "#!/bin/sh\n" );
        Files.writeString( directory.resolve( "bin/10-self-updater.hook" ), "update" );
        Files.writeString( directory.resolve( "license.txt" ), "license" );
        ImageMagickPackager.removeUpdateHooks( directory );
        ImageMagickPackager.writeIndex( directory, "bin/magick" );
        final String index = Files.readString( directory.resolve( "files.txt" ) );
        assertTrue( index.startsWith( "bin/magick\n" ) );
        assertTrue( index.contains( "x\tbin/magick\n" ) );
        assertTrue( index.contains( "-\tlicense.txt\n" ) );
        assertFalse( index.contains( "self-updater" ) );
        assertFalse( index.contains( "files.txt" ) );
    }
}
