package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
        byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "file.txt" );
        FilesHelper.write( path, bytes );
        final byte[] readBytes = FilesHelper.readAllBytes( path );
        Assert.assertArrayEquals( bytes, readBytes );
    }

    @Test
    public void test_incorrect_values()
        throws IOException
    {
        byte[] bytes = new byte[]{2, 3, 5, 7, 13};
        Path path = Paths.get( temporaryFolder.getRoot().toString(), "unknown_file.txt" );
        final byte[] readBytes = FilesHelper.readAllBytes( path );
        Assert.assertArrayEquals( null, readBytes );
    }

}
