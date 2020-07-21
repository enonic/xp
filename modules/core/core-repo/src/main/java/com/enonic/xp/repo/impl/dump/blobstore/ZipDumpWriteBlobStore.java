package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpWriteBlobStore
    extends AbstractDumpBlobStore
{
    private static final Logger LOG = LoggerFactory.getLogger( ZipDumpWriteBlobStore.class );

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final Map<String, ByteSource> records = new HashMap<>();

    public ZipDumpWriteBlobStore( String dumpName, ZipArchiveOutputStream zipArchiveOutputStream )
    {
        super( PathRef.of( dumpName ) );

        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    @Override
    public DumpBlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    public void flush()
    {
        try
        {
            for ( Map.Entry<String, ByteSource> entry : records.entrySet() )
            {
                final String segmentedKey = entry.getKey();
                final ByteSource in = entry.getValue();
                zipArchiveOutputStream.putArchiveEntry( new ZipArchiveEntry( segmentedKey ) );
                in.copyTo( zipArchiveOutputStream );
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
    protected ByteSource getBytes( final Segment segment, final BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ByteSink getByteSink( final Segment segment, final BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeRecord( final Segment segment, final BlobKey key, final ByteSource in )
    {
        records.put( getBlobRef( segment, key ).asString(), in );
    }
}
