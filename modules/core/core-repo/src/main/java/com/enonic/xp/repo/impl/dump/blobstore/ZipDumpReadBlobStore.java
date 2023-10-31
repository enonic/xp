package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.io.ByteSource;

import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpReadBlobStore
    implements DumpBlobStore
{
    private final ZipFile zipFile;

    private final PathRef basePath;

    public ZipDumpReadBlobStore( final ZipFile zipFile, final PathRef basePath )
    {
        this.basePath = basePath;
        this.zipFile = zipFile;
    }

    @Override
    public ByteSource getBytes( final BlobReference reference )
    {
        return new ByteSource()
        {
            @Override
            public InputStream openStream()
                throws IOException
            {
                return zipFile.getInputStream( zipFile.getEntry( DumpBlobStoreUtils.getBlobPathRef( basePath, reference ).asString() ) );
            }
        };
    }

    @Override
    public void addRecord( final BlobReference reference )
    {
        throw new UnsupportedOperationException();
    }
}
