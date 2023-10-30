package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.io.ByteSource;

import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpReadBlobStore
    extends AbstractDumpBlobStore
{
    private final ZipFile zipFile;

    public ZipDumpReadBlobStore( ZipFile zipFile, PathRef basePath )
    {
        super( basePath, null );

        this.zipFile = zipFile;
    }

    @Override
    protected ByteSource getBytes( final BlobReference reference )
    {
        return new ByteSource()
        {
            @Override
            public InputStream openStream()
                throws IOException
            {
                return zipFile.getInputStream( zipFile.getEntry( getBlobPathRef( reference ).asString() ) );
            }
        };
    }
}
