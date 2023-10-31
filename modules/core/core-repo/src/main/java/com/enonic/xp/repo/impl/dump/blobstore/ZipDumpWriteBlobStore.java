package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpWriteBlobStore
    implements DumpBlobStore
{
    private final Set<BlobReference> records = new HashSet<>();

    private final PathRef basePath;

    private final BlobStore sourceBlobStore;

    public ZipDumpWriteBlobStore( final PathRef basePath, final BlobStore sourceBlobStore )
    {
        this.basePath = basePath;
        this.sourceBlobStore = sourceBlobStore;
    }

    public void flush( final ZipArchiveOutputStream zipArchiveOutputStream )
    {
        try
        {
            for ( var reference : records )
            {
                zipArchiveOutputStream.putArchiveEntry(
                    new ZipArchiveEntry( DumpBlobStoreUtils.getBlobPathRef( basePath, reference ).asString() ) );
                DumpBlobStoreUtils.getBytesByReference( sourceBlobStore, reference ).copyTo( zipArchiveOutputStream );
                zipArchiveOutputStream.closeArchiveEntry();
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void addRecord( final BlobReference reference )
    {
        records.add( reference );
    }

    @Override
    public ByteSource getBytes( final BlobReference reference )
    {
        throw new UnsupportedOperationException();
    }
}
