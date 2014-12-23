package com.enonic.wem.export.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathUtilsTest
{

    @Test
    public void testName()
        throws Exception
    {
        System.setProperty( "os.name", "a window distribution" );

        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/C:/path/to/stuff" ) );
        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "//C:/path/to/stuff" ) );
        assertEquals( "C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "C:/path/to/stuff" ) );
        assertEquals( "/my/path/C:/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/my/path/C:/path/to/stuff" ) );
        assertEquals( "/path/to/stuff", PathUtils.removeLeadingWindowsSlash( "/path/to/stuff" ) );
    }
}