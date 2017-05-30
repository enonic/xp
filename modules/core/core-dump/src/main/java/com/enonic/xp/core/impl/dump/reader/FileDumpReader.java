package com.enonic.xp.core.impl.dump.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.impl.dump.RepoDumpException;
import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpReader
    implements DumpReader
{
    private final Path dumpDirectory;

    public FileDumpReader( final Path basePath, final String dumpName )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );
    }

    private java.nio.file.Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public void open( final RepositoryId repositoryId, final Branch branch )
    {

        final File metaFile = getMetaFile( repositoryId, branch );

    }


    public void load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<List<String>> processor )
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
    public void close()
    {

    }

    @Override
    public DumpEntry next()
    {
        return null;
    }

    private File getMetaFile( final RepositoryId repositoryId, final Branch branch )
    {
        final File metaFile = createMetaPath( repositoryId, branch ).toFile();

        if ( metaFile.exists() )
        {
            throw new RepoDumpException( "Meta-file with path [" + metaFile.getPath() + "] already exists" );
        }

        metaFile.getParentFile().mkdirs();

        if ( !metaFile.getParentFile().exists() )
        {
            throw new RepoDumpException( "Not able to create parent-directory [" + metaFile.getParentFile().getPath() + "]" );
        }

        return metaFile;
    }


    private Path createMetaPath( final RepositoryId repositoryId, final Branch branch )
    {
        return Paths.get( this.dumpDirectory.toString(), repositoryId.toString(), branch.toString() );
    }

}
