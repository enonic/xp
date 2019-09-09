package com.enonic.xp.internal.blobstore.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.blob.BlobKey;

import static org.junit.jupiter.api.Assertions.*;

public class FileBlobRecordTest
{
    @TempDir
    public Path temporaryFolder;

    @Test
    public void testAccessors()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "test" );
        final File file = Files.createFile(this.temporaryFolder.resolve( "test" ) ).toFile();

        final FileBlobRecord record = new FileBlobRecord( key, file );
        assertSame( key, record.getKey() );
        assertNotNull( record.getBytes() );
        assertEquals( 0, record.getLength() );
    }

    @Test
    public void lastModified()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "test" );
        final File file = Files.createFile(this.temporaryFolder.resolve( "test" ) ).toFile();

        final FileBlobRecord record = new FileBlobRecord( key, file );

        assertEquals( file.lastModified(), record.lastModified() );
    }
}
