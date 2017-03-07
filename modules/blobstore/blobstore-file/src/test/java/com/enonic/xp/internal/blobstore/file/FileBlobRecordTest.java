package com.enonic.xp.internal.blobstore.file;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.xp.blob.BlobKey;

import static org.junit.Assert.*;

public class FileBlobRecordTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testAccessors()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "test" );
        final File file = this.temporaryFolder.newFile( "test" );

        final FileBlobRecord record = new FileBlobRecord( key, file );
        assertSame( key, record.getKey() );
        assertNotNull( record.getBytes() );
        assertEquals( 0, record.getLength() );
    }
}
