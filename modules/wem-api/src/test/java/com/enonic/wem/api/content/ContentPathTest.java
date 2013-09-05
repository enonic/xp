package com.enonic.wem.api.content;

import org.junit.Test;

import com.enonic.wem.api.space.SpaceName;

import static com.enonic.wem.api.content.ContentPath.newPath;
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
    public void testFrom_without_spaceName()
        throws Exception
    {
        ContentPath path = ContentPath.from( "one/two/three" );
        assertEquals( 3, path.elementCount() );
        assertEquals( "one", path.getElement( 0 ) );
        assertEquals( "two", path.getElement( 1 ) );
        assertEquals( "three", path.getElement( 2 ) );
        assertEquals( "/one/two/three", path.toString() );

    }

    @Test
    public void testFrom_with_spaceName()
        throws Exception
    {
        ContentPath path = ContentPath.from( "space:/one/two/three" );
        assertEquals( SpaceName.from( "space" ), path.getSpace() );
        assertEquals( 3, path.elementCount() );
        assertEquals( "one", path.getElement( 0 ) );
        assertEquals( "two", path.getElement( 1 ) );
        assertEquals( "three", path.getElement( 2 ) );
        assertEquals( "space:/one/two/three", path.toString() );
    }

    @Test
    public void test_toString()
        throws Exception
    {
        System.out.println( ContentPath.newPath().spaceName( "mySpace" ).elements( "parent", "child" ).build().toString() );
        System.out.println( ContentPath.newPath().spaceName( "mySpace" ).build().toString() );
    }

    @Test
    public void getParentPath()
        throws Exception
    {
        assertEquals( ContentPath.from( "space:/" ), ContentPath.from( "space:/first" ).getParentPath() );
        assertEquals( ContentPath.from( "first" ), ContentPath.newPath().elements( "first", "second" ).build().getParentPath() );
        assertEquals( newPath().elements( "first", "second" ).build(),
                      newPath().elements( "first", "second", "third" ).build().getParentPath() );
    }

    @Test
    public void getParentPath_when_embedded()
        throws Exception
    {
        assertEquals( ContentPath.from( "mySpace:/parent" ), ContentPath.from( "mySpace:/parent/__embedded/myEmbedded" ).getParentPath() );
        assertEquals( ContentPath.from( "mySpace:/parent/parent" ),
                      ContentPath.from( "mySpace:/parent/parent/__embedded/myEmbedded" ).getParentPath() );
    }

    @Test
    public void isRoot()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "/" ).isRoot() );
    }

    @Test
    public void toString_when_isRoot()
        throws Exception
    {
        assertEquals( "/", ContentPath.ROOT.toString() );
        assertEquals( "space:/", ContentPath.from( "space:/" ).toString() );
    }

    @Test
    public void from()
        throws Exception
    {
        assertEquals( ContentPath.from( "a" ), ContentPath.from( "a" ) );
        assertEquals( newPath().elements( "a", "b" ).build(), ContentPath.from( "a/b" ) );
    }

    @Test
    public void isChildOf()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "otherParent" ) ) );
        assertEquals( false, ContentPath.from( "parent" ).isChildOf( ContentPath.from( "parent/child" ) ) );
        assertEquals( false, ContentPath.from( "parent/child" ).isChildOf( ContentPath.from( "parent/child" ) ) );

        assertEquals( true, ContentPath.from( "myspace:parent/child" ).isChildOf( ContentPath.from( "myspace:parent" ) ) );
        assertEquals( false, ContentPath.from( "myspace:parent/child" ).isChildOf( ContentPath.from( "otherspace:parent" ) ) );
    }


    @Test
    public void isRelative()
        throws Exception
    {
        assertFalse( ContentPath.from( "myspace:/" ).isRelative() );
        assertTrue( ContentPath.from( "/" ).isRelative() );
    }

    @Test
    public void isAbsolute()
        throws Exception
    {
        assertTrue( ContentPath.from( "myspace:/" ).isAbsolute() );
        assertFalse( ContentPath.from( "/" ).isAbsolute() );
    }

    @Test
    public void absolutePaths()
        throws Exception
    {
        assertEquals( SpaceName.from( "myspace" ), ContentPath.from( "myspace:/" ).getSpace() );
        assertEquals( SpaceName.from( "myspace" ), ContentPath.from( "myspace:/path" ).getSpace() );
        assertEquals( SpaceName.from( "myspace" ), ContentPath.from( "myspace:/path/child" ).getSpace() );
        assertEquals( SpaceName.from( "myspace" ), ContentPath.from( "myspace:path/child" ).getSpace() );
    }

    @Test
    public void getName()
        throws Exception
    {
        assertEquals( "parent", ContentPath.from( "myspace:/parent" ).getName() );
        assertEquals( "child", ContentPath.from( "myspace:/parent/child" ).getName() );
        assertEquals( null, ContentPath.from( "myspace:/" ).getName() );
    }

    @Test
    public void isPathToEmbeddedContent()
        throws Exception
    {
        assertEquals( true, ContentPath.from( "myspace:/parent/__embedded/myEmbedded" ).isPathToEmbeddedContent() );
        assertEquals( false, ContentPath.from( "myspace:/parent/child" ).isPathToEmbeddedContent() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_no_name_for_embedded_content_then_exception_is_thrown()
        throws Exception
    {
        ContentPath.from( "myspace:/parent/__embedded/" ).isPathToEmbeddedContent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPathToEmbeddedContent_given_more_than_one_element_after_embedded_marker_then_exception_is_thrown()
        throws Exception
    {
        ContentPath.from( "myspace:/parent/__embedded/one/two" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPathToEmbeddedContent_given_no_content_before_embedded_marker_then_exception_is_thrown()
        throws Exception
    {
        ContentPath.from( "myspace:/__embedded/one/two" );
    }
}
