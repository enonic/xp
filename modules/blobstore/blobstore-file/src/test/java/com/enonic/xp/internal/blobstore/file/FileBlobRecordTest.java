package com.enonic.xp.internal.blobstore.file;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.blob.BlobKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FileBlobRecordTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    void testAccessors()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "test" );
        final Path file = Files.createFile( this.temporaryFolder.resolve( "test" ) );

        final FileBlobRecord record = new FileBlobRecord( key, file );
        assertSame( key, record.getKey() );
        assertNotNull( record.getBytes() );
        assertEquals( 0, record.getLength() );
    }

    @Test
    void lastModified()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "test" );
        final Path file = Files.createFile( this.temporaryFolder.resolve( "test" ) );

        final FileBlobRecord record = new FileBlobRecord( key, file );

        assertEquals( Files.getLastModifiedTime( file ).toMillis(), record.lastModified() );
    }
}
