package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
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
        final Path path = temporaryFolder.resolve( "test.7z" );
        try (SevenZOutputFile output = new SevenZOutputFile( path.toFile() ))
        {
            output.setContentCompression( SevenZMethod.COPY );
            final SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName( name );
            output.putArchiveEntry( entry );
            output.write( new byte[]{1, 2, 3} );
            output.closeArchiveEntry();
        }
        return path;
    }
}
