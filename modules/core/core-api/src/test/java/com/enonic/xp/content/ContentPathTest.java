package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ContentPathTest
{

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( ContentPath.class ).withNonnullFields( "elements" ).verify();
    }

    @Test
    public void test_toString()
        throws Exception
    {
        assertEquals( "/parent/child", ContentPath.from( "/parent/child" ).toString() );
        assertEquals( "/", ContentPath.create().build().toString() );
        assertEquals( "", ContentPath.create().absolute( false ).build().toString() );
    }

    @Test
    public void getParentPath()
        throws Exception
    {
        assertEquals( ContentPath.from( "/" ), ContentPath.from( "/first" ).getParentPath() );
        assertEquals( ContentPath.from( "/first" ), ContentPath.from( "/first/second" ).getParentPath() );
        assertEquals( ContentPath.from( "/first/second" ), ContentPath.from( "/first/second/third" ).getParentPath() );
    }

    @Test
    public void getParentPath_root()
        throws Exception
    {
        assertEquals( null, ContentPath.from( "/" ).getParentPath() );
    }

    @Test
    public void getAncestorPath()
        throws Exception
    {
        assertEquals( ContentPath.from( "/a/b" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 1 ) );
        assertEquals( ContentPath.from( "/a" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 2 ) );
        assertEquals( ContentPath.from( "/" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 3 ) );
        assertEquals( null, ContentPath.from( "/a/b/c" ).getAncestorPath( 4 ) );
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
        assertEquals( ContentPath.from( "a/b" ), ContentPath.from( "a/b" ) );
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
        assertEquals( ContentName.from( "parent" ), ContentPath.from( "/parent" ).getName() );
        assertEquals( ContentName.from( "child" ), ContentPath.from( "/parent/child" ).getName() );
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
    public void asAbsolute()
    {
        ContentPath samePath = ContentPath.from( "/same" );
        assertSame( samePath, samePath.asAbsolute() );

        ContentPath relative = ContentPath.from( "relative" );
        assertEquals( true, relative.asAbsolute().isAbsolute() );
        assertEquals( "/relative", relative.asAbsolute().toString() );
    }

}
