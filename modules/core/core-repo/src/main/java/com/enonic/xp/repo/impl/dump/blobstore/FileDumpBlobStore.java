package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;

public class FileDumpBlobStore
    extends AbstractDumpBlobStore
{
    private final Path baseDir;

    public FileDumpBlobStore( final Path baseDir )
    {
        super( PathRef.of() );
        this.baseDir = baseDir;
    }

    @Override
    protected ByteSource getBytes( final Segment segment, final BlobKey key )
    {
        return MoreFiles.asByteSource( getBlobRef( segment, key ).asPath( baseDir ) );
    }

    @Override
    protected ByteSink getByteSink( final Segment segment, final BlobKey key )
    {
        return MoreFiles.asByteSink( getBlobRef( segment, key ).asPath( baseDir ) );
    }

    @Override
    public void addRecord( final Segment segment, final BlobRecord blobRecord )
    {
        try
        {
            final Path file = getBlobRef( segment, blobRecord.getKey() ).asPath( baseDir );
            if ( !Files.exists( file ) )
            {
                Files.createDirectories( file.getParent() );
                blobRecord.getBytes().copyTo( MoreFiles.asByteSink( file ) );
            }
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }
}
