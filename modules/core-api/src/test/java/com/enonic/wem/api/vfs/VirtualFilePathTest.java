package com.enonic.wem.api.vfs;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class VirtualFilePathTest
{
    @Test
    public void fromPath()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePath.from( Paths.get( "this", "is", "my", "test" ) );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void fromString()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePath.from( "/this/is/my/test" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void double_slashes()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePath.from( "//this/is/my//test" );

        assertEquals( "/this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void relative()
        throws Exception
    {
        final VirtualFilePath virtualFilePath = VirtualFilePath.from( "this/is/my/test" );

        assertEquals( "this/is/my/test", virtualFilePath.getPath() );
    }

    @Test
    public void subtract_absolute()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePath.from( "/this/is/my/test" );
        final VirtualFilePath subtract = VirtualFilePath.from( "/this/is" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test
    public void subtract_relative()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePath.from( "this/is/my/test" );
        final VirtualFilePath subtract = VirtualFilePath.from( "this/is" );

        assertEquals( "my/test", path.subtractPath( subtract ).getPath() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void subtract_not_part_of()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePath.from( "this/is/my/test" );
        final VirtualFilePath subtract = VirtualFilePath.from( "dummy/path" );
        path.subtractPath( subtract ).getPath();
    }

    @Test(expected = IllegalArgumentException.class)
    public void subtract_longer_than()
        throws Exception
    {
        final VirtualFilePath path = VirtualFilePath.from( "this/is/my/test" );
        final VirtualFilePath subtract = VirtualFilePath.from( "this/is/my/test/longer" );
        path.subtractPath( subtract ).getPath();
    }

}