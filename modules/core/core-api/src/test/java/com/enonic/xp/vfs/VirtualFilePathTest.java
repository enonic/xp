package com.enonic.xp.vfs;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VirtualFilePathTest
{
    @Test
    public void fromPath()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( Paths.get( "this", "is", "my", "test" ) );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void fromString()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "/this/is/my/test", "/" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void double_slashes()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "//this/is/my//test", "/" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void relative()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "this/is/my/test", "/" );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void subtract_absolute()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "/this/is", "/" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test
    public void subtract_relative()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "this/is", "/" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test
    public void subtract_not_part_of()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "dummy/path", "/" );
        assertThrows(IllegalArgumentException.class, () -> path.subtractPath( subtract ).getPath());
    }

    @Test
    public void subtract_longer_than()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "this/is/my/test/longer", "/" );
        assertThrows(IllegalArgumentException.class, () -> path.subtractPath( subtract ).getPath());
    }

    @Test
    public void join_strings()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "with", "appended", "elements" );

        assertEquals( "this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

    @Test
    public void join_strings_absolute()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "with", "appended", "elements" );

        assertEquals( "/this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

    @Test
    public void join_strings_with_separator()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "/with", "/appended", "/elements" );

        assertEquals( "/this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

}
