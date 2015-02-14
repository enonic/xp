package com.enonic.xp.vfs;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import junit.framework.Assert;

public abstract class AbstractVirtualFileTest
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    protected File rootDir;

    @Before
    public final void setup()
        throws Exception
    {
        populateTestData();
    }

    private void populateTestData()
        throws Exception
    {
        this.rootDir = this.tempFolder.newFolder( "root" );

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
        Assert.assertTrue( "Failed to create directory " + name + " under " + dir.getAbsolutePath(), file.mkdirs() );
        return file;
    }

    private void createFile( final File dir, final String name, final String contents )
        throws Exception
    {
        final File file = new File( dir, name );
        Files.write( contents, file, Charsets.UTF_8 );
    }
}
