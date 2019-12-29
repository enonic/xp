package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDumpReaderTest
{
    @TempDir
    public Path temporaryFolder;

    private FileDumpReader fileDumpReader;

    private Path dumpFolder;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.dumpFolder = Files.createDirectory( this.temporaryFolder.resolve( "myDump" ) );
        createMetaDataFile( dumpFolder );
        this.fileDumpReader = new FileDumpReader( temporaryFolder.toFile().toPath(), "myDump", null );
    }

    @Test
    public void repositories()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        createFolder( meta, "repo1" );
        createFolder( meta, "repo2" );

        final RepositoryIds repositories = fileDumpReader.getRepositories();
        assertEquals( 2, repositories.getSize() );
    }

    @Test
    public void ignore_file_in_repo_dir()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        createFolder( meta, "repo1" );
        createFolder( meta, "repo2" );
        createFile( meta, "fisk" );

        final RepositoryIds repositories = fileDumpReader.getRepositories();
        assertEquals( 2, repositories.getSize() );
    }

    @Test
    public void branches()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        final Path repo1 = createFolder( meta, "repo1" );
        createFolder( repo1, "master" );
        createFolder( repo1, "draft" );

        final Branches branches = fileDumpReader.getBranches( RepositoryId.from( "repo1" ) );
        assertEquals( 2, branches.getSize() );
    }

    @Test
    public void ignore_file_in_branch_folder()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        final Path repo1 = createFolder( meta, "repo1" );
        createFolder( repo1, "master" );
        createFolder( repo1, "draft" );
        createFile( meta, "fisk" );

        final Branches branches = fileDumpReader.getBranches( RepositoryId.from( "repo1" ) );
        assertEquals( 2, branches.getSize() );
    }

    @Test
    public void hidden_folder()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        final Path repo1 = createFolder( meta, "repo1" );
        final Path hiddenFolder = createFolder( repo1, ".myBranch" );

        if ( isWindows() )
        {
            hideTheFileWindowsWay( hiddenFolder );
        }

        createFolder( repo1, "myBranch" );

        final Branches branches = fileDumpReader.getBranches( RepositoryId.from( "repo1" ) );
        assertEquals( 1, branches.getSize() );
    }

    private void hideTheFileWindowsWay( final Path hiddenFolder )
        throws IOException
    {
        Files.setAttribute( hiddenFolder, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS );
    }

    private boolean isWindows()
    {
        return System.getProperty( "os.name" ).toLowerCase().startsWith( "windows" );
    }

    private void createMetaDataFile( final Path parent )
        throws IOException
    {
        final String content = "{\"xpVersion\":\"X.Y.Z.SNAPSHOT\",\"timestamp\":\"1970-01-01T00:00:00.000Z\",\"modelVersion\":\"1.0.0\"}";
        Files.writeString( parent.resolve( "dump.json" ), content );
    }

    private Path createFolder( final Path parent, final String name )
        throws IOException
    {
        return Files.createDirectory( parent.resolve( name ) );
    }

    private Path createFile( final Path parent, final String name )
        throws IOException
    {
        return Files.createFile( parent.resolve( name ) );
    }

}
