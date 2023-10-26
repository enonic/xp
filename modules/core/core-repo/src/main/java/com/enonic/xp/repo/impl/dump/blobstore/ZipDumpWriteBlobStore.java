package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;

public class ZipDumpWriteBlobStore
    extends AbstractDumpBlobStore
{
    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final Map<String, BlobRecord> records = new HashMap<>();

    public ZipDumpWriteBlobStore( String dumpName, ZipArchiveOutputStream zipArchiveOutputStream )
    {
        super( PathRef.of( dumpName ) );

        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public void flush()
    {
        try
        {
            for ( var entry : records.entrySet() )
            {
                final String segmentedKey = entry.getKey();
                final BlobRecord blobRecord = entry.getValue();
                zipArchiveOutputStream.putArchiveEntry( new ZipArchiveEntry( segmentedKey ) );
                blobRecord.getBytes().copyTo( zipArchiveOutputStream );
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
    public void addRecord( final Segment segment, final BlobRecord blobRecord )
    {
        records.put( getBlobRef( segment, blobRecord.getKey() ).asString(), blobRecord );
    }
}
