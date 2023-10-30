package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpWriteBlobStore
    extends AbstractDumpBlobStore
{
    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final Map<PathRef, BlobContainer> records = new HashMap<>();

    public ZipDumpWriteBlobStore( String dumpName, ZipArchiveOutputStream zipArchiveOutputStream, BlobStore sourceBlobStore )
    {
        super( PathRef.of( dumpName ), sourceBlobStore );
        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public void flush()
    {
        try
        {
            for ( var entry : records.entrySet() )
            {
                zipArchiveOutputStream.putArchiveEntry( new ZipArchiveEntry( entry.getKey().toString() ) );
                copyBlob( entry.getValue(), zipArchiveOutputStream );
                zipArchiveOutputStream.closeArchiveEntry();
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        finally
        {
            records.clear();
        }
    }

    @Override
    public void addRecord( final BlobContainer blobContainer )
    {
        records.put( getBlobPathRef( blobContainer.getReference() ), blobContainer );
    }
}
