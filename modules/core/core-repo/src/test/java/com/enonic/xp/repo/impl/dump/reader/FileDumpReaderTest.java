package com.enonic.xp.repo.impl.dump.reader;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDumpReaderTest
    extends BaseDumpReaderTest
{

    private FileDumpReader fileDumpReader;

    @BeforeEach
    void setUp()
        throws Exception
    {
        this.dumpFolder = Files.createDirectory( temporaryFolder.resolve( "myDump" ) );
        createMetaDataFile( dumpFolder );
        this.fileDumpReader = FileDumpReader.create( null, temporaryFolder, "myDump" );
    }

    @Test
    void repositories()
        throws Exception
    {
        final Path meta = createFolder( this.dumpFolder, "meta" );
        createFolder( meta, "repo1" );
        createFolder( meta, "repo2" );

        final RepositoryIds repositories = fileDumpReader.getRepositories();
        assertEquals( 2, repositories.getSize() );
    }

    @Test
    void ignore_file_in_repo_dir()
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
    void branches()
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
    void ignore_file_in_branch_folder()
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
    void hidden_folder()
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

}
