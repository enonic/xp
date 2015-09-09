package com.enonic.xp.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;

public class FilesHelperTest
{
    private TemporaryFolder temporaryFolder;

    @Before
    public void setUp()
        throws IOException
    {
        temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
    }

    @Test
    public void test_write_readBytes()
        throws IOException
    {
        final byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        final ByteSource source = ByteSource.wrap( bytes );
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "file.txt" );
        FilesHelper.write( path, source );
        final ByteSource readSource = FilesHelper.readAllBytes( path );
        Assert.assertTrue( source.contentEquals( readSource ) );
    }

    @Test
    public void test_incorrect_values()
        throws IOException
    {
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "unknown_file.txt" );
        final ByteSource readBytes = FilesHelper.readAllBytes( path );
        Assert.assertEquals( null, readBytes );
    }

}
