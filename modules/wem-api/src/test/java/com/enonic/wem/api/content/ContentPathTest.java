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
                return ContentPath.from( "/myPath/myContent" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentPath.from( "myContent" ), ContentPath.from( "/myPath/myContent2" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentPath.from( "/myPath/myContent" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentPath.from( "/myPath/myContent" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testFrom()
        throws Exception
    {
        ContentPath path = ContentPath.from( "one/two/three" );
        assertEquals( 3, path.elementCount() );
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

    @Test
    public void isRoot()
        throws Exception
    {
        assertEquals( true, new ContentPath( "/" ).isRoot() );
    }

    @Test
    public void toString_when_isRoot()
        throws Exception
    {
        assertEquals( "/", ContentPath.ROOT.toString() );
    }

    @Test
    public void from()
        throws Exception
    {
        assertEquals( new ContentPath( "a" ), ContentPath.from( "a" ) );
        assertEquals( new ContentPath( "a", "b" ), ContentPath.from( "a/b" ) );
    }

    @Test
    public void isChildOf()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "otherParent" ) ) );
        assertEquals( false, ContentPath.from( "parent" ).isChildOf( ContentPath.from( "parent/child" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent/child" ) ) );
    }
}
