package com.enonic.xp.vfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

public abstract class AbstractVirtualFileTest
{
    @TempDir
    public Path temporaryFolder;

    protected Path rootDir;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        populateTestData();
    }

    private void populateTestData()
        throws Exception
    {
        this.rootDir = Files.createDirectory( this.temporaryFolder.resolve( "root" ) );

        final Path dir1 = createDir( this.rootDir, "dir1" );
        final Path dir2 = createDir( this.rootDir, "dir2" );
        final Path dir3 = createDir( dir2, "dir3" );

        createTestFiles( this.rootDir, "" );
        createTestFiles( dir1, "dir1/" );
        createTestFiles( dir2, "dir2/" );
        createTestFiles( dir3, "dir2/dir3/" );
    }

    private void createTestFiles( final Path dir, final String prefix )
        throws Exception
    {
        createFile( dir, "file1.txt", "contents of " + prefix + "file1.txt" );
        createFile( dir, "file2.log", "contents of " + prefix + "file1.log" );
    }

    private Path createDir( final Path dir, final String name )
        throws IOException
    {
        return Files.createDirectory( dir.resolve( name ) );
    }

    private void createFile( final Path dir, final String name, final String contents )
        throws Exception
    {
        final Path file = dir.resolve( name );
        Files.writeString( file, contents );
    }
}
