package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentPathTest
{

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentPath.class ).withNonnullFields( "elements" ).verify();
    }

    @Test
    void test_toString()
    {
        assertEquals( "/parent/child", ContentPath.from( "/parent/child" ).toString() );
        assertEquals( "/", ContentPath.create().build().toString() );
    }

    @Test
    void getParentPath()
    {
        assertEquals( ContentPath.from( "/" ), ContentPath.from( "/first" ).getParentPath() );
        assertEquals( ContentPath.from( "/first" ), ContentPath.from( "/first/second" ).getParentPath() );
        assertEquals( ContentPath.from( "/first/second" ), ContentPath.from( "/first/second/third" ).getParentPath() );
    }

    @Test
    void getParentPath_root()
    {
        assertEquals( null, ContentPath.from( "/" ).getParentPath() );
    }

    @Test
    void getAncestorPath()
    {
        assertEquals( ContentPath.from( "/a/b" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 1 ) );
        assertEquals( ContentPath.from( "/a" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 2 ) );
        assertEquals( ContentPath.from( "/" ), ContentPath.from( "/a/b/c" ).getAncestorPath( 3 ) );
        assertEquals( null, ContentPath.from( "/a/b/c" ).getAncestorPath( 4 ) );
    }

    @Test
    void isRoot()
    {
        assertEquals( true, ContentPath.ROOT.isRoot() );
        assertEquals( true, ContentPath.from( "/" ).isRoot() );
    }

    @Test
    void toString_when_isRoot()
    {
        assertEquals( "/", ContentPath.ROOT.toString() );
        assertEquals( "/", ContentPath.from( "/" ).toString() );
    }

    @Test
    void from()
    {
        assertEquals( ContentPath.from( "a" ), ContentPath.from( "/a" ) );
        assertEquals( ContentPath.from( "a/b" ), ContentPath.from( "/a/b" ) );
    }

    @Test
    void isChildOf()
    {
        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "otherParent" ) ) );
        assertEquals( false, ContentPath.from( "parent" ).isChildOf( ContentPath.from( "parent/child" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent/child" ) ) );

        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
    }


    @Test
    void getName()
    {
        assertEquals( ContentName.from( "parent" ), ContentPath.from( "/parent" ).getName() );
        assertEquals( ContentName.from( "child" ), ContentPath.from( "/parent/child" ).getName() );
        assertEquals( null, ContentPath.from( "/" ).getName() );
    }
}
