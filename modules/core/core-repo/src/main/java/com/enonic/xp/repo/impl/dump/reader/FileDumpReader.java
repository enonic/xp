package com.enonic.xp.repo.impl.dump.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpReader
    implements DumpReader
{
    private final Path dumpDirectory;

    private final BlobStore dumpBlobStore;

    private final NodeVersionFactory factory;

    public FileDumpReader( final Path basePath, final String dumpName )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.factory = new NodeVersionFactory();
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public void load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor )
    {
        final File metaFile = getMetaFile( repositoryId, branch );

        try
        {
            Files.readLines( metaFile, Charsets.UTF_8, processor );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        final BlobRecord record = this.dumpBlobStore.getRecord( Segment.from( "version" ), BlobKey.from( nodeVersionId.toString() ) );

        if ( record == null )
        {
            throw new RepoDumpException( "Cannot find referred version id " + nodeVersionId + " in dump" );
        }

        return this.factory.create( record.getBytes() );
    }

    private File getMetaFile( final RepositoryId repositoryId, final Branch branch )
    {
        final File metaFile = createMetaPath( repositoryId, branch ).toFile();

        if ( !metaFile.exists() )
        {
            throw new RepoDumpException( "Meta-file with path [" + metaFile.getPath() + "] does not exists" );
        }

        return metaFile;
    }

    private Path createMetaPath( final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( this.dumpDirectory.toString(), repositoryId.toString(), branch.toString() );
    }

}
