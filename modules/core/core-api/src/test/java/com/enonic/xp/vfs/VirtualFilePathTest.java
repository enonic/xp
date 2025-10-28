package com.enonic.xp.vfs;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VirtualFilePathTest
{
    @Test
    void fromPath()
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( Path.of( "this", "is", "my", "test" ) );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    void fromString()
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "/this/is/my/test", "/" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    void double_slashes()
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "//this/is/my//test", "/" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    void relative()
    {
        final VirtualFilePath virtualFilePath = VirtualFilePaths.from( "this/is/my/test", "/" );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    void subtract_absolute()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "/this/is", "/" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test
    void subtract_relative()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "this/is", "/" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test
    void subtract_not_part_of()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "dummy/path", "/" );
        assertThrows(IllegalArgumentException.class, () -> path.subtractPath( subtract ).getPath());
    }

    @Test
    void subtract_longer_than()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );
        final VirtualFilePath subtract = VirtualFilePaths.from( "this/is/my/test/longer", "/" );
        assertThrows(IllegalArgumentException.class, () -> path.subtractPath( subtract ).getPath());
    }

    @Test
    void join_strings()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "with", "appended", "elements" );

        assertEquals( "this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

    @Test
    void join_strings_absolute()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "with", "appended", "elements" );

        assertEquals( "/this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

    @Test
    void join_strings_with_separator()
    {
        final VirtualFilePath path = VirtualFilePaths.from( "/this/is/my/test", "/" );

        final VirtualFilePath joinedPath = path.join( "/with", "/appended", "/elements" );

        assertEquals( "/this/is/my/test/with/appended/elements", joinedPath.getPath() );

    }

}
