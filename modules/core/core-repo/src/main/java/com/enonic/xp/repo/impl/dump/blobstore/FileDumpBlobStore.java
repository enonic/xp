package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;

public class FileDumpBlobStore
{
    private final Path baseDir;

    private final BlobStore sourceBlobStore;

    public FileDumpBlobStore( final Path baseDir, final BlobStore sourceBlobStore )
    {
        this.baseDir = baseDir;
        this.sourceBlobStore = sourceBlobStore;
    }

    public ByteSource getBytes( final BlobReference reference )
    {
        return MoreFiles.asByteSource( toPath( reference ) );
    }

    public void addRecord( final BlobReference reference )
    {
        writeBlob( reference, sourceBlobStore.getRecord( reference.getSegment(), reference.getKey() ).getBytes() );
    }

    public BlobKey addRecord( final Segment segment, final ByteSource data )
    {
        final BlobReference reference = new BlobReference( segment, BlobKey.from( data ) );

        writeBlob( reference, data );
        return reference.getKey();
    }

    public DumpBlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        return new DumpBlobRecord( segment, key, this );
    }
    void overrideBlob( final BlobReference reference, byte[] bytes )
        throws IOException
    {
        Files.write( toPath( reference ), bytes );
    }

    private void writeBlob( final BlobReference reference, final ByteSource data )
    {
        final Path path = toPath( reference );
        try
        {
            if ( !Files.exists( path ) )
            {
                Files.createDirectories( path.getParent() );

                try (var output = Files.newOutputStream( path ))
                {
                    data.copyTo( output );
                }
            }
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }

    private Path toPath( final BlobReference reference )
    {
        return DumpBlobStoreUtils.getBlobPathRef( PathRef.of(), reference ).asPath( baseDir );
    }
}
