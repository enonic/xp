package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
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

    public DumpBlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        return new DumpBlobRecord( segment, key, this );
    }

    protected ByteSource getBytes( final Segment segment, final BlobKey key )
    {
        return MoreFiles.asByteSource( getBlobRef( segment, key ).asPath( baseDir ) );
    }

    protected ByteSink getByteSink( final Segment segment, final BlobKey key )
    {
        return MoreFiles.asByteSink( getBlobRef( segment, key ).asPath( baseDir ) );
    }

    protected void writeRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException
    {
        final Path file = getBlobRef( segment, key ).asPath( baseDir );
        if ( !Files.exists( file ) )
        {
            Files.createDirectories( file.getParent() );
            in.copyTo( MoreFiles.asByteSink( file ) );
        }
    }
}
