package com.enonic.wem.api.content.data;


import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static org.junit.Assert.*;

public class EntryPathTest
{

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return EntryPath.from( "mySet.myInput" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{EntryPath.from( "mySet" ), EntryPath.from( "myInput" ), EntryPath.from( "mySet.myOther" ),
                    EntryPath.from( "myOther.myInput" ), EntryPath.from( "mySet.myInput[0]" ), EntryPath.from( "mySet[0].myInput" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return EntryPath.from( "mySet.myInput" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return EntryPath.from( "mySet.myInput" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void equals_of_element()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new EntryPath.Element( "element" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new EntryPath.Element( "element[0]" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new EntryPath.Element( "element" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new EntryPath.Element( "element" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void from_parentPath_element()
    {
        EntryPath parentPath = EntryPath.from( "parent.path" );
        EntryPath path = EntryPath.from( parentPath, EntryPath.Element.from( "element" ) );
        assertEquals( "parent.path.element", path.toString() );
        assertSame( parentPath, path.getParent() );

        path = EntryPath.from( parentPath, "element" );
        assertEquals( "parent.path.element", path.toString() );
        assertSame( parentPath, path.getParent() );
    }

    @Test
    public void from_element_varargs()
    {
        EntryPath path = EntryPath.from( EntryPath.Element.from( "a" ), EntryPath.Element.from( "b" ), EntryPath.Element.from( "c" ) );
        assertEquals( "a.b.c", path.toString() );
        assertEquals( "a.b", path.getParent().toString() );
    }

    @Test
    public void tostring()
    {
        assertEquals( "car[0]", EntryPath.from( "car[0]" ).toString() );
        assertEquals( "car[0].model", EntryPath.from( "car[0].model" ).toString() );
    }

    @Test
    public void isRelative_path_not_starting_with_dot_is_relative()
    {
        assertEquals( true, EntryPath.from( "a" ).isRelative() );
        assertEquals( true, EntryPath.from( "a.b" ).isRelative() );
    }

    @Test
    public void isRelative_root_is_not_relative()
    {
        assertEquals( false, EntryPath.ROOT.isRelative() );
    }

    @Test
    public void isRelative_path_starting_with_dot_is_not_relative()
    {
        assertEquals( false, EntryPath.from( ".a" ).isRelative() );
        assertEquals( false, EntryPath.from( ".a.b" ).isRelative() );
    }

    @Test
    public void elementCount_returns_zero_when_path_is_root()
    {
        assertEquals( false, EntryPath.ROOT.isRelative() );
    }

    @Test
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car[0].model", EntryPath.from( EntryPath.from( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        EntryPath.Element element = EntryPath.from( "car[1]" ).iterator().next();
        assertEquals( 1, element.getIndex() );
        assertEquals( "car[1]", element.toString() );

        element = EntryPath.from( "car" ).iterator().next();
        assertEquals( false, element.hasIndex() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolvePathElementNames()
    {
        Iterator<String> elementNamesIt = EntryPath.from( "car[0]" ).resolvePathElementNames().iterator();
        assertEquals( "car", elementNamesIt.next() );

        elementNamesIt = EntryPath.from( "car[0].model" ).resolvePathElementNames().iterator();
        assertEquals( "car", elementNamesIt.next() );
        assertEquals( "model", elementNamesIt.next() );
    }

    @Test
    public void startsWith()
    {
        Assert.assertTrue( EntryPath.from( "car" ).startsWith( EntryPath.from( "car" ) ) );
        Assert.assertTrue( EntryPath.from( "car[0].model" ).startsWith( EntryPath.from( "car[0]" ) ) );
        Assert.assertTrue( EntryPath.from( "car[0].model" ).startsWith( EntryPath.from( "car[0].model" ) ) );
        Assert.assertTrue( EntryPath.from( "car[0].model.other" ).startsWith( EntryPath.from( "car[0].model" ) ) );

        Assert.assertFalse( EntryPath.from( "car" ).startsWith( EntryPath.from( "bicycle" ) ) );
        Assert.assertFalse( EntryPath.from( "car[0].model" ).startsWith( EntryPath.from( "bicycle[0].model" ) ) );
        Assert.assertFalse( EntryPath.from( "car[0]" ).startsWith( EntryPath.from( "car[0].model" ) ) );
        Assert.assertFalse( EntryPath.from( "car[0].model" ).startsWith( EntryPath.from( "car[0].year" ) ) );

        Assert.assertTrue( EntryPath.from( "car[0].model" ).startsWith( EntryPath.from( "car" ) ) );
        Assert.assertTrue( EntryPath.from( "car[1].model" ).startsWith( EntryPath.from( "car" ) ) );

        Assert.assertFalse( EntryPath.from( "car[1].model" ).startsWith( EntryPath.from( "car[0]" ) ) );
    }

    @Test
    public void getParent()
    {
        assertEquals( null, EntryPath.from( "orphan" ).getParent() );
        assertEquals( EntryPath.from( "parent" ), EntryPath.from( "parent.child" ).getParent() );
        assertEquals( EntryPath.from( "parent[0]" ), EntryPath.from( "parent[0].child" ).getParent() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "b.c", EntryPath.from( "a.b.c" ).asNewWithoutFirstPathElement().toString() );
    }
}
