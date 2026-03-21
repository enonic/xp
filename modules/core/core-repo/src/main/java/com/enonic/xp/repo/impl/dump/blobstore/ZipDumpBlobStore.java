package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.reader.BlobStoreAccess;
import com.enonic.xp.repo.impl.dump.writer.DedupZipArchiveOutputStream;

public class ZipDumpBlobStore
{
    private final Map<String, BlobReference> records = new HashMap<>();

    private final PathRef basePath;

    private final BlobStoreAccess sourceBlobStore;

    private final DedupZipArchiveOutputStream zipArchiveOutputStream;

    public ZipDumpBlobStore( final PathRef basePath, final BlobStoreAccess sourceBlobStore,
                             final DedupZipArchiveOutputStream zipArchiveOutputStream )
    {
        this.basePath = basePath;
        this.sourceBlobStore = sourceBlobStore;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public void add( BlobReference reference )
    {
        final String name = DumpBlobStoreUtils.getBlobPathRef( basePath, reference ).asString();
        if ( zipArchiveOutputStream.deduplication.contains( name ) )
        {
            return;
        }
        records.putIfAbsent( name, reference );
    }

    public void flush()
        throws IOException
    {
        // It is not possible to write into multiple zip entries at the same time.
        // We delay writing the blobs until metadata entry write is complete.
        try
        {
            final List<BlobReference> notFound = new ArrayList<>();
            for ( var entry : records.entrySet() )
            {
                final BlobReference reference = entry.getValue();

                final BlobRecord record = sourceBlobStore.getRecord( reference.segment(), reference.key() );

                if ( record == null )
                {
                    // Try to write as many blobs as possible, report missing blobs only at the very end.
                    notFound.add( reference );
                    continue;
                }

                // In archive blobs are stored without repository segmentation. But zip files are capable to store entries with the same name.
                // So, we need to skip duplicates.
                // Note 1: There is very minimal extra memory usage as ZipArchiveOutputStream stores references to the same names anyway.
                // Note 2: We delay adding zipEntryName into deduplication set until we are sure that blob is written.
                // In case the same blob may be found in another repository.
                zipArchiveOutputStream.put( entry.getKey(), record.getBytes() );
            }
            if ( !notFound.isEmpty() )
            {
                throw new RepoDumpException( "Blobs not found: " + notFound );
            }
        }
        finally
        {
            records.clear();
        }
    }
}
