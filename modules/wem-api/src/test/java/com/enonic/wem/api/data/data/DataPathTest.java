package com.enonic.wem.api.data.data;


import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static org.junit.Assert.*;

public class DataPathTest
{

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return DataPath.from( "mySet.myInput" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{DataPath.from( "mySet" ), DataPath.from( "myInput" ), DataPath.from( "mySet.myOther" ),
                    DataPath.from( "myOther.myInput" ), DataPath.from( "mySet.myInput[0]" ), DataPath.from( "mySet[0].myInput" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return DataPath.from( "mySet.myInput" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return DataPath.from( "mySet.myInput" );
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
                return new DataPath.Element( "element" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new DataPath.Element( "element[0]" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new DataPath.Element( "element" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new DataPath.Element( "element" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void from_parentPath_element()
    {
        DataPath parentPath = DataPath.from( "parent.path" );
        DataPath path = DataPath.from( parentPath, DataPath.Element.from( "element" ) );
        assertEquals( "parent.path.element", path.toString() );
        assertSame( parentPath, path.getParent() );

        path = DataPath.from( parentPath, "element" );
        assertEquals( "parent.path.element", path.toString() );
        assertSame( parentPath, path.getParent() );
    }

    @Test
    public void from_element_varargs()
    {
        DataPath path = DataPath.from( DataPath.Element.from( "a" ), DataPath.Element.from( "b" ), DataPath.Element.from( "c" ) );
        assertEquals( "a.b.c", path.toString() );
        assertEquals( "a.b", path.getParent().toString() );
    }

    @Test
    public void tostring()
    {
        assertEquals( "car[0]", DataPath.from( "car[0]" ).toString() );
        assertEquals( "car[0].model", DataPath.from( "car[0].model" ).toString() );
    }

    @Test
    public void isRelative_path_not_starting_with_dot_is_relative()
    {
        assertEquals( true, DataPath.from( "a" ).isRelative() );
        assertEquals( true, DataPath.from( "a.b" ).isRelative() );
    }

    @Test
    public void isRelative_root_is_not_relative()
    {
        assertEquals( false, DataPath.ROOT.isRelative() );
    }

    @Test
    public void isRelative_path_starting_with_dot_is_not_relative()
    {
        assertEquals( false, DataPath.from( ".a" ).isRelative() );
        assertEquals( false, DataPath.from( ".a.b" ).isRelative() );
    }

    @Test
    public void elementCount_returns_zero_when_path_is_root()
    {
        assertEquals( false, DataPath.ROOT.isRelative() );
    }

    @Test
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car[0].model", DataPath.from( DataPath.from( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        DataPath.Element element = DataPath.from( "car[1]" ).iterator().next();
        assertEquals( 1, element.getIndex() );
        assertEquals( "car[1]", element.toString() );

        element = DataPath.from( "car" ).iterator().next();
        assertEquals( false, element.hasIndex() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolvePathElementNames()
    {
        Iterator<String> elementNamesIt = DataPath.from( "car[0]" ).resolvePathElementNames().iterator();
        assertEquals( "car", elementNamesIt.next() );

        elementNamesIt = DataPath.from( "car[0].model" ).resolvePathElementNames().iterator();
        assertEquals( "car", elementNamesIt.next() );
        assertEquals( "model", elementNamesIt.next() );
    }

    @Test
    public void startsWith()
    {
        Assert.assertTrue( DataPath.from( "car" ).startsWith( DataPath.from( "car" ) ) );
        Assert.assertTrue( DataPath.from( "car[0].model" ).startsWith( DataPath.from( "car[0]" ) ) );
        Assert.assertTrue( DataPath.from( "car[0].model" ).startsWith( DataPath.from( "car[0].model" ) ) );
        Assert.assertTrue( DataPath.from( "car[0].model.other" ).startsWith( DataPath.from( "car[0].model" ) ) );

        Assert.assertFalse( DataPath.from( "car" ).startsWith( DataPath.from( "bicycle" ) ) );
        Assert.assertFalse( DataPath.from( "car[0].model" ).startsWith( DataPath.from( "bicycle[0].model" ) ) );
        Assert.assertFalse( DataPath.from( "car[0]" ).startsWith( DataPath.from( "car[0].model" ) ) );
        Assert.assertFalse( DataPath.from( "car[0].model" ).startsWith( DataPath.from( "car[0].year" ) ) );

        Assert.assertTrue( DataPath.from( "car[0].model" ).startsWith( DataPath.from( "car" ) ) );
        Assert.assertTrue( DataPath.from( "car[1].model" ).startsWith( DataPath.from( "car" ) ) );

        Assert.assertFalse( DataPath.from( "car[1].model" ).startsWith( DataPath.from( "car[0]" ) ) );
    }

    @Test
    public void getParent()
    {
        assertEquals( null, DataPath.from( "orphan" ).getParent() );
        assertEquals( DataPath.from( "parent" ), DataPath.from( "parent.child" ).getParent() );
        assertEquals( DataPath.from( "parent[0]" ), DataPath.from( "parent[0].child" ).getParent() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "b.c", DataPath.from( "a.b.c" ).asNewWithoutFirstPathElement().toString() );
    }
}
