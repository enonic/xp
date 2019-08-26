package com.enonic.xp.repo.impl.dump.reader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

import static org.junit.jupiter.api.Assertions.*;

public class FileDumpReaderTest
{
    @TempDir
    public Path temporaryFolder;

    private FileDumpReader fileDumpReader;

    private File dumpFolder;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.dumpFolder = java.nio.file.Files.createDirectory( this.temporaryFolder.resolve( "myDump" ) ).toFile();
        createMetaDataFile( dumpFolder );
        this.fileDumpReader = new FileDumpReader( temporaryFolder.getRoot().toFile().toPath(), "myDump", null );
    }

    @Test
    public void repositories()
        throws Exception
    {
        final File meta = createFolder( this.dumpFolder, "meta" );
        createFolder( meta, "repo1" );
        createFolder( meta, "repo2" );

        final RepositoryIds repositories = fileDumpReader.getRepositories();
        assertEquals( 2, repositories.getSize() );
    }

    @Test
    public void ignore_file_in_repo_dir()
        throws Exception
    {
        final File meta = createFolder( this.dumpFolder, "meta" );
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
        final File meta = createFolder( this.dumpFolder, "meta" );
        final File repo1 = createFolder( meta, "repo1" );
        createFolder( repo1, "master" );
        createFolder( repo1, "draft" );

        final Branches branches = fileDumpReader.getBranches( RepositoryId.from( "repo1" ) );
        assertEquals( 2, branches.getSize() );
    }

    @Test
    public void ignore_file_in_branch_folder()
        throws Exception
    {
        final File meta = createFolder( this.dumpFolder, "meta" );
        final File repo1 = createFolder( meta, "repo1" );
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
        final File meta = createFolder( this.dumpFolder, "meta" );
        final File repo1 = createFolder( meta, "repo1" );
        final File hiddenFolder = createFolder( repo1, ".myBranch" );

        if ( isWindows() )
        {
            hideTheFileWindowsWay( hiddenFolder );
        }

        createFolder( repo1, "myBranch" );

        final Branches branches = fileDumpReader.getBranches( RepositoryId.from( "repo1" ) );
        assertEquals( 1, branches.getSize() );
    }

    private void hideTheFileWindowsWay( final File hiddenFolder )
        throws IOException
    {
        java.nio.file.Files.setAttribute( hiddenFolder.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS );
    }

    private boolean isWindows()
    {
        return System.getProperty( "os.name" ).toLowerCase().startsWith( "windows" );
    }

    private void createMetaDataFile( final File parent )
        throws IOException
    {
        final String content = "{\"xpVersion\":\"X.Y.Z.SNAPSHOT\",\"timestamp\":\"1970-01-01T00:00:00.000Z\",\"modelVersion\":\"1.0.0\"}";
        Files.write( content, new File( parent, "dump.json" ), Charset.defaultCharset() );
    }

    private File createFolder( final File parent, final String name )
    {
        final File newFolder = Paths.get( parent.toString(), name ).toFile();
        final boolean ok = newFolder.mkdirs();

        if ( !ok )
        {
            throw new RuntimeException( "Cannot create folder " + name + " in " + parent.toString() );
        }

        return newFolder;
    }

    private File createFile( final File parent, final String name )
    {
        final File file = Paths.get( parent.toString(), name ).toFile();
        try
        {
            file.createNewFile();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return file;
    }

}
