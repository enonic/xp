package com.enonic.wem.api;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PathTest
{

    @Test
    public void isEmpty()
    {
        assertEquals( false, new Path( "first", '/' ).isEmpty() );
        assertEquals( true, new Path( "", '/' ).isEmpty() );
    }

    @Test
    public void isRoot()
    {
        assertEquals( true, new Path( "/", '/' ).isRoot() );
    }

    @Test
    public void isAbsolute()
    {
        assertEquals( true, new Path( "/first", '/' ).isAbsolute() );
        assertEquals( false, new Path( "first", '/' ).isAbsolute() );
    }

    @Test
    public void hasTrailingDivider()
    {
        assertEquals( true, new Path( "first/", '/' ).hasTrailingDivider() );
        assertEquals( false, new Path( "first", '/' ).hasTrailingDivider() );
        assertEquals( false, new Path( "/", '/' ).hasTrailingDivider() );
    }

    @Test
    public void trimTrailingDivider()
    {
        assertEquals( "first", new Path( "first/", '/' ).trimTrailingDivider().toString() );
    }

    @Test
    public void tostring()
    {
        assertEquals( "", new Path( "", '/' ).toString() );
        assertEquals( "/", new Path( "/", '/' ).toString() );
        assertEquals( "first", new Path( "first", '/' ).toString() );
        assertEquals( "/first", new Path( "/first", '/' ).toString() );
        assertEquals( "first/", new Path( "first/", '/' ).toString() );
        assertEquals( "first/second", new Path( "first/second", '/' ).toString() );
        assertEquals( "/first/second", new Path( "/first/second", '/' ).toString() );
        assertEquals( "/first/second/", new Path( "/first/second/", '/' ).toString() );
        assertEquals( "first/second/", new Path( "first/second/", '/' ).toString() );
    }
}
