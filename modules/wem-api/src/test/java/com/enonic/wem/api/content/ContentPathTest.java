package com.enonic.wem.api.content;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContentPathTest
{

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new ContentPath( "/myPath/myContent" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new ContentPath( "myContent" ), new ContentPath( "/myPath/myContent2" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new ContentPath( "/myPath/myContent" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new ContentPath( "/myPath/myContent" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testFrom()
        throws Exception
    {
        ContentPath path = ContentPath.from( "one/two/three" );
        assertEquals( 3, path.length() );
        assertEquals( "one", path.getElement( 0 ) );
        assertEquals( "two", path.getElement( 1 ) );
        assertEquals( "three", path.getElement( 2 ) );
        assertEquals( "one/two/three", path.toString() );

    }

    @Test
    public void getParentPath()
        throws Exception
    {
        assertEquals( null, new ContentPath( "first" ).getParentPath() );
        assertEquals( new ContentPath( "first" ), new ContentPath( "first", "second" ).getParentPath() );
        assertEquals( new ContentPath( "first", "second" ), new ContentPath( "first", "second", "third" ).getParentPath() );
    }
}
