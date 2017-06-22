package com.enonic.xp.lib.http;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class RefFileByteSourceTest
{
    private File tempFile;

    @Before
    public void setUp()
        throws Exception
    {
        tempFile = File.createTempFile( "xphttp", ".tmp" );
    }

    @After
    public final void shutdown()
        throws Exception
    {
        tempFile.delete();
    }

    @Test
    public void testFileByteSource()
        throws Throwable
    {
        final RefFileByteSource fileByteSource = new RefFileByteSource( tempFile );

        assertNotNull( fileByteSource.openStream() );
        assertEquals( 0, fileByteSource.size() );
        assertEquals( 0, fileByteSource.read().length );

        assertTrue( fileByteSource.getFile().exists() );
        fileByteSource.finalize();
        assertFalse( fileByteSource.getFile().exists() );
    }

}