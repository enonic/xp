package com.enonic.wem.api.content.data;


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
                return new EntryPath( "mySet.myInput" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new EntryPath( "mySet" ), new EntryPath( "myInput" ), new EntryPath( "mySet.myOther" ),
                    new EntryPath( "myOther.myInput" ), new EntryPath( "mySet.myInput[0]" ), new EntryPath( "mySet[0].myInput" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new EntryPath( "mySet.myInput" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new EntryPath( "mySet.myInput" );
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
    public void tostring()
    {
        assertEquals( "car[0]", new EntryPath( "car[0]" ).toString() );
        assertEquals( "car[0].model", new EntryPath( "car[0].model" ).toString() );
    }

    @Test
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car[0].model", new EntryPath( new EntryPath( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        EntryPath.Element element = new EntryPath( "car[1]" ).iterator().next();
        assertEquals( 1, element.getIndex() );
        assertEquals( "car[1]", element.toString() );

        element = new EntryPath( "car" ).iterator().next();
        assertEquals( false, element.hasIndex() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolveFormItemPath()
    {
        assertEquals( "car", new EntryPath( "car[0]" ).resolveFormItemPath().toString() );
        assertEquals( "car.model", new EntryPath( "car[0].model" ).resolveFormItemPath().toString() );
    }

    @Test
    public void startsWith()
    {
        Assert.assertTrue( new EntryPath( "car" ).startsWith( new EntryPath( "car" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0]" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].model" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model.other" ).startsWith( new EntryPath( "car[0].model" ) ) );

        Assert.assertFalse( new EntryPath( "car" ).startsWith( new EntryPath( "bicycle" ) ) );
        Assert.assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "bicycle[0].model" ) ) );
        Assert.assertFalse( new EntryPath( "car[0]" ).startsWith( new EntryPath( "car[0].model" ) ) );
        Assert.assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].year" ) ) );

        Assert.assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car" ) ) );
        Assert.assertTrue( new EntryPath( "car[1].model" ).startsWith( new EntryPath( "car" ) ) );

        Assert.assertFalse( new EntryPath( "car[1].model" ).startsWith( new EntryPath( "car[0]" ) ) );
    }

    @Test
    public void asNewWithIndexAtPath()
    {
        assertEquals( "set", new EntryPath( "set" ).asNewWithIndexAtPath( 0, new EntryPath( "nonExisting" ) ).toString() );

        assertEquals( "set[0]", new EntryPath( "set" ).asNewWithIndexAtPath( 0, new EntryPath( "set" ) ).toString() );
        assertEquals( "set[0].input", new EntryPath( "set.input" ).asNewWithIndexAtPath( 0, new EntryPath( "set" ) ).toString() );
        assertEquals( "anotherSet.set[0].input",
                      new EntryPath( "anotherSet.set.input" ).asNewWithIndexAtPath( 0, new EntryPath( "anotherSet.set" ) ).toString() );
        assertEquals( "anotherSet.set.input[0]", new EntryPath( "anotherSet.set.input" ).asNewWithIndexAtPath( 0, new EntryPath(
            "anotherSet.set.input" ) ).toString() );

    }

    @Test
    public void asNewWithoutIndexAtLastPathElement()
    {
        assertEquals( new EntryPath( "parent.child" ), new EntryPath( "parent.child[1]" ).asNewWithoutIndexAtLastPathElement() );
        assertEquals( new EntryPath( "element" ), new EntryPath( "element[1]" ).asNewWithoutIndexAtLastPathElement() );
    }

    @Test
    public void getParent()
    {
        assertEquals( new EntryPath( "parent" ), new EntryPath( "parent.child" ).getParent() );
        assertEquals( new EntryPath( "parent[0]" ), new EntryPath( "parent[0].child" ).getParent() );
    }
}
