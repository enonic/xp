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
        EmbeddedImageMagick.extractWindows( archive( "lib/codec.dll" ), directory );
        assertArrayEquals( new byte[]{1, 2, 3}, Files.readAllBytes( directory.resolve( "lib/codec.dll" ) ) );
        assertThrows( IOException.class, () -> EmbeddedImageMagick.extractWindows( archive( "../outside" ), directory ) );
        assertFalse( Files.exists( temporaryFolder.resolve( "outside" ) ) );
    }

    @Test
    void recognizesPlatformAliases()
    {
        assertEquals( "linux-x86_64", EmbeddedImageMagick.platform( "Linux", "amd64" ) );
        assertEquals( "windows-x86_64", EmbeddedImageMagick.platform( "Windows 11", "x86_64" ) );
        assertEquals( "windows-aarch64", EmbeddedImageMagick.platform( "Windows 11", "arm64" ) );
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
