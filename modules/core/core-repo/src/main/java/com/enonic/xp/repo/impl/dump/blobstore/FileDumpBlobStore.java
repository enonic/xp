package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpBlobStore
{
    private final Path baseDir;

    public FileDumpBlobStore( final Path baseDir )
    {
        this.baseDir = baseDir;
    }

    public Path getBasePath()
    {
        return baseDir;
    }

    public ByteSource getBytes( final BlobReference reference )
    {
        return MoreFiles.asByteSource( toPath( reference ) );
    }

    public BlobKey addRecord( final Segment segment, final ByteSource data )
    {
        final BlobReference reference = new BlobReference( segment, BlobKey.sha256( data ) );

        writeBlob( reference, data );
        return reference.getKey();
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
            throw new RepoDumpException( "Failed to add blob", e );
        }
    }

    public void writeMetaEntry( final RepositoryId repositoryId, final String subFolder, final String entryName, final byte[] data )
    {
        final Path metaPath = baseDir.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( subFolder );
        try
        {
            Files.createDirectories( metaPath );
            Files.write( metaPath.resolve( entryName ), data );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot write meta entry [" + entryName + "] for repository [" + repositoryId + "]", e );
        }
    }

    private Path toPath( final BlobReference reference )
    {
        return DumpBlobStoreUtils.getBlobPathRef( PathRef.of(), reference ).asPath( baseDir );
    }

}
