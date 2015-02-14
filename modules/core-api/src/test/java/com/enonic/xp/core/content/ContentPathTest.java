package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.support.AbstractEqualsTest;

import static com.enonic.xp.core.content.ContentPath.newPath;
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
                return new Object[]{ContentPath.from( "myPath/myContent" ), ContentPath.from( "myContent" ),
                    ContentPath.from( "/myPath/myContent2" )};
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
    public void test_toString()
        throws Exception
    {
        assertEquals( "/parent/child", ContentPath.newPath().elements( "parent", "child" ).build().toString() );
        assertEquals( "/", ContentPath.newPath().build().toString() );
        assertEquals( "", ContentPath.newPath().absolute( false ).build().toString() );
    }

    @Test
    public void getParentPath()
        throws Exception
    {
        assertEquals( ContentPath.from( "/" ), ContentPath.from( "/first" ).getParentPath() );
        assertEquals( ContentPath.from( "/first" ), ContentPath.newPath().elements( "first", "second" ).build().getParentPath() );
        assertEquals( newPath().elements( "first", "second" ).build(),
                      newPath().elements( "first", "second", "third" ).build().getParentPath() );
    }

    @Test
    public void isRoot()
        throws Exception
    {
        assertEquals( true, ContentPath.ROOT.isRoot() );
        assertEquals( true, ContentPath.from( "/" ).isRoot() );
    }

    @Test
    public void toString_when_isRoot()
        throws Exception
    {
        assertEquals( "/", ContentPath.ROOT.toString() );
        assertEquals( "/", ContentPath.from( "/" ).toString() );
    }

    @Test
    public void from()
        throws Exception
    {
        assertEquals( ContentPath.from( "a" ), ContentPath.from( "a" ) );
        assertEquals( newPath().elements( "a", "b" ).build(), ContentPath.from( "/a/b" ) );
    }

    @Test
    public void isChildOf()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "otherParent" ) ) );
        assertEquals( false, ContentPath.from( "parent" ).isChildOf( ContentPath.from( "parent/child" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent/child" ) ) );

        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
    }


    @Test
    public void getName()
        throws Exception
    {
        assertEquals( "parent", ContentPath.from( "/parent" ).getName() );
        assertEquals( "child", ContentPath.from( "/parent/child" ).getName() );
        assertEquals( null, ContentPath.from( "/" ).getName() );
    }

    @Test
    public void isAbsolute()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "/parent" ).isAbsolute() );
        assertEquals( false, ContentPath.from( "parent" ).isAbsolute() );
    }

    @Test
    public void isRelative()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "parent" ).isRelative() );
        assertEquals( false, ContentPath.from( "/parent" ).isRelative() );
    }

    @Test
    public void asRelative()
    {
        ContentPath samePath = ContentPath.from( "same" );
        assertSame( samePath, samePath.asRelative() );

        ContentPath absolute = ContentPath.from( "/absolute" );
        assertEquals( true, absolute.asRelative().isRelative() );
        assertEquals( "absolute", absolute.asRelative().toString() );
    }

    @Test
    public void asAbsolute()
    {
        ContentPath samePath = ContentPath.from( "/same" );
        assertSame( samePath, samePath.asAbsolute() );

        ContentPath relative = ContentPath.from( "relative" );
        assertEquals( true, relative.asAbsolute().isAbsolute() );
        assertEquals( "/relative", relative.asAbsolute().toString() );
    }

}
