package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpReadBlobStore
    extends AbstractDumpBlobStore
{
    private final ZipFile zipFile;

    public ZipDumpReadBlobStore( ZipFile zipFile, PathRef basePath )
    {
        super( basePath );

        this.zipFile = zipFile;
    }

    @Override
    protected ByteSource getBytes( final Segment segment, final BlobKey key )
    {
        return new ByteSource()
        {
            @Override
            public InputStream openStream()
                throws IOException
            {
                return zipFile.getInputStream( zipFile.getEntry( getBlobRef( segment, key ).asString() ) );
            }
        };
    }

    @Override
    protected ByteSink getByteSink( final Segment segment, final BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeRecord( final Segment segment, final BlobKey key, final ByteSource in )
    {
        throw new UnsupportedOperationException();
    }
}
