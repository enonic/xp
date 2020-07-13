package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.blobstore.FileDumpBlobStore;
import com.enonic.xp.repository.RepositoryId;

public class FileDumpReader
    extends AbstractDumpReader
{
    private final Path dumpPath;

    private FileDumpReader( final SystemLoadListener listener, FilePaths filePaths, FileDumpBlobStore dumpBlobStore, Path dumpPath )
    {
        super( listener, filePaths, dumpBlobStore );
        this.dumpPath = dumpPath;
    }

    public static FileDumpReader create( final SystemLoadListener listener, Path basePath, final String dumpName )
    {
        return create( listener, basePath, dumpName, new DefaultFilePaths() );
    }

    public static FileDumpReader create( final SystemLoadListener listener, Path basePath, final String dumpName, FilePaths filePaths )
    {
        final Path dumpPath = basePath.resolve( dumpName );
        if ( !Files.isDirectory( dumpPath ) )
        {
            throw new RepoLoadException( "Directory is not a valid dump directory: [" + dumpPath + "]" );
        }
        return new FileDumpReader( listener, filePaths, new FileDumpBlobStore( dumpPath ), dumpPath );
    }

    @Override
    protected InputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        return Files.newInputStream( metaFile.asPath( dumpPath ) );
    }

    @Override
    protected Stream<String> listDirectories( final PathRef repoRootPath )
        throws IOException
    {
        return Files.list( repoRootPath.asPath( dumpPath ) ).filter( Files::isDirectory ).filter( folder -> {
            return !folder.toFile().isHidden(); // see JDK-8215467
        } ).map( dir -> dir.getFileName().toString() );
    }

    @Override
    protected boolean exists( final PathRef file )
    {
        return Files.exists( file.asPath( dumpPath ) );
    }

    public DumpBlobStore getDumpBlobStore()
    {
        return new FileDumpBlobStore( dumpPath );
    }

    public Path getBranchEntriesFile( final RepositoryId repositoryId, final Branch branch )
    {
        return filePaths.branchMetaPath( repositoryId, branch ).asPath( dumpPath );
    }

    public Path getVersionsFile( final RepositoryId repositoryId )
    {
        return filePaths.versionMetaPath( repositoryId ).asPath( dumpPath );
    }

    public Path getRepositoryDir( final RepositoryId repositoryId )
    {
        return filePaths.repoPath( repositoryId ).asPath( dumpPath );
    }

    public Path getMetaDataFile()
    {
        return filePaths.metaDataFile().asPath( dumpPath );
    }
}
