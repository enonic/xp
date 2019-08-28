package com.enonic.xp.vfs;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;


public abstract class AbstractVirtualFileTest
{
    @TempDir
    public Path temporaryFolder;

    protected File rootDir;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        populateTestData();
    }

    private void populateTestData()
        throws Exception
    {
        this.rootDir = Files.createDirectory( this.temporaryFolder.resolve( "root" ) ).toFile();

        final File dir1 = createDir( this.rootDir, "dir1" );
        final File dir2 = createDir( this.rootDir, "dir2" );
        final File dir3 = createDir( dir2, "dir3" );

        createTestFiles( this.rootDir, "" );
        createTestFiles( dir1, "dir1/" );
        createTestFiles( dir2, "dir2/" );
        createTestFiles( dir3, "dir2/dir3/" );
    }

    private void createTestFiles( final File dir, final String prefix )
        throws Exception
    {
        createFile( dir, "file1.txt", "contents of " + prefix + "file1.txt" );
        createFile( dir, "file2.log", "contents of " + prefix + "file1.log" );
    }

    private File createDir( final File dir, final String name )
    {
        final File file = new File( dir, name );
        assertTrue( file.mkdirs(), "Failed to create directory " + name + " under " + dir.getAbsolutePath() );
        return file;
    }

    private void createFile( final File dir, final String name, final String contents )
        throws Exception
    {
        final File file = new File( dir, name );
        com.google.common.io.Files.write( contents, file, StandardCharsets.UTF_8 );
    }
}
