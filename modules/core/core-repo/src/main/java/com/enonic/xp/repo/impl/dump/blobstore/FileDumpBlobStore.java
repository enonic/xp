package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.repo.impl.dump.PathRef;

public class FileDumpBlobStore
    extends AbstractDumpBlobStore
{
    private final Path baseDir;

    public FileDumpBlobStore( final Path baseDir, BlobStore sourceBlobStore )
    {
        super( PathRef.of(), sourceBlobStore );
        this.baseDir = baseDir;
    }

    @Override
    protected ByteSource getBytes( final BlobReference reference )
    {
        return MoreFiles.asByteSource( getBlobPathRef( reference ).asPath( baseDir ) );
    }

    @Override
    protected ByteSink getByteSink( final BlobReference reference )
    {
        return MoreFiles.asByteSink( getBlobPathRef( reference ).asPath( baseDir ) );
    }

    @Override
    public void addRecord( final BlobContainer blobContainer )
    {
        try
        {
            final Path file = getBlobPathRef( blobContainer.getReference() ).asPath( baseDir );
            if ( !Files.exists( file ) )
            {
                Files.createDirectories( file.getParent() );
                copyBlob( blobContainer, Files.newOutputStream( file ) );
            }
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }
}
