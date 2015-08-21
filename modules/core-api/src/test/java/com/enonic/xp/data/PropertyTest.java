package com.enonic.xp.data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;


public class PropertyTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                PropertyTree tree = new PropertyTree();
                return tree.addString( "myString", "myValue" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                PropertyTree tree = new PropertyTree();
                return new Object[]{tree.addString( "myString", "otherValue" ), tree.addString( "otherString", "myValue" ),
                    tree.addXml( "otherType", "myValue" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                PropertyTree tree = new PropertyTree();
                return tree.addString( "myString", "myValue" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                PropertyTree tree = new PropertyTree();
                return tree.addString( "myString", "myValue" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void countAncestors()
    {
        PropertyTree tree = new PropertyTree();

        PropertySet aSet = tree.newSet();
        Property aProperty = tree.addSet( "a", aSet );

        PropertySet bSet = aSet.newSet();
        Property bProperty = aSet.addSet( "b", bSet );

        PropertySet cSet = bSet.newSet();
        Property cProperty = bSet.addSet( "c", cSet );
        Property cSubProperty = cSet.addString( "myProp", "value" );

        assertEquals( 0, aProperty.countAncestors() );
        assertEquals( 1, bProperty.countAncestors() );
        assertEquals( 2, cProperty.countAncestors() );
        assertEquals( 3, cSubProperty.countAncestors() );
    }

    @Test
    public void property_copy()
    {
        PropertyTree sourceTree = new PropertyTree();
        sourceTree.setString( "outerSet.innerSet.myString", "myValue" );
        System.out.println( sourceTree );

        PropertyTree destinationTree = new PropertyTree();
        PropertySet destiSet = destinationTree.addSet( "destiSet" );
        sourceTree.getProperty( "outerSet.innerSet" ).copyTo( destiSet );

        System.out.println( destinationTree.toString() );

        // Verify the property from the source and destination tree are equals
        assertEquals( sourceTree.getProperty( "outerSet.innerSet" ), destinationTree.getProperty( "destiSet.innerSet" ) );
        assertEquals( sourceTree.getProperty( "outerSet.innerSet.myString" ), destinationTree.getProperty( "destiSet.innerSet.myString" ) );
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void check_exception_is_thrown_when_name_is_null()
    {
        thrown.expect( NullPointerException.class );
        Property.checkName( null );
    }

    @Test
    public void check_exception_is_thrown_when_name_is_blank()
    {
        thrown.expect( IllegalArgumentException.class );
        Property.checkName( "" );
    }

    @Test
    public void check_exception_is_thrown_when_name_contains_dot()
    {
        thrown.expect( IllegalArgumentException.class );
        Property.checkName( "." );
    }

    @Test
    public void check_exception_is_thrown_when_name_contains_brackets()
    {
        thrown.expect( IllegalArgumentException.class );
        Property.checkName( "[]" );
    }

}
