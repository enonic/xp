package com.enonic.wem.api.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.support.PathUtils;

import static org.junit.Assert.*;

public class PathUtilsTest_windows
{
    private String os = System.getProperty( "os.name" );

    @Before
    public void setUp()
    {
        System.setProperty( "os.name", "window" );
    }

    @Test
    public void testWindowsUrls()
        throws Exception
    {
        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/C:/path/to/stuff" ) );
        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "//C:/path/to/stuff" ) );
        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "C:/path/to/stuff" ) );
        assertEquals( "/my/path/C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/my/path/C:/path/to/stuff" ) );
        assertEquals( "/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/path/to/stuff" ) );
    }

    @After
    public void cleanUp()
    {
        System.setProperty( "os.name", this.os );
    }
}