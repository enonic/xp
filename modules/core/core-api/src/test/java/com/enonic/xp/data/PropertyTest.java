package com.enonic.xp.data;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PropertyTest
{
    @Test
    void equals()
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
    void property_copy()
    {
        PropertyTree sourceTree = new PropertyTree();
        sourceTree.setString( "outerSet.innerSet.myString", "myValue" );
        System.out.println( sourceTree );

        PropertyTree destinationTree = new PropertyTree();
        PropertySet destiSet = destinationTree.addSet( "destiSet" );
        sourceTree.getProperty( "outerSet.innerSet" ).copyTo( destiSet );

        System.out.println( destinationTree );

        // Verify the property from the source and destination tree are equals
        assertEquals( sourceTree.getProperty( "outerSet.innerSet" ), destinationTree.getProperty( "destiSet.innerSet" ) );
        assertEquals( sourceTree.getProperty( "outerSet.innerSet.myString" ), destinationTree.getProperty( "destiSet.innerSet.myString" ) );
    }

    @Test
    void property_copy_indexed()
    {
        PropertyTree sourceTree = new PropertyTree();
        sourceTree.addSet( "outerSet" );
        sourceTree.addSet( "outerSet" );

        sourceTree.setString( "outerSet[1].innerSet.myString", "myValue" );

        PropertyTree destinationTree = new PropertyTree();
        PropertySet destiSet = destinationTree.addSet( "destiSet" );

        sourceTree.getProperty( "outerSet[1]" ).copyTo( destiSet );

        assertEquals( sourceTree.getProperty( "outerSet[1].innerSet" ), destinationTree.getProperty( "destiSet.outerSet.innerSet" ) );
        assertEquals( sourceTree.getProperty( "outerSet[1].innerSet.myString" ), destinationTree.getProperty( "destiSet.outerSet.innerSet.myString" ) );
    }

    @Test
    void check_exception_is_thrown_when_name_is_null()
    {
        assertThrows( NullPointerException.class, () -> Property.checkName( null ) );
    }

    @Test
    void check_exception_is_thrown_when_name_is_blank()
    {
        assertThrows( IllegalArgumentException.class, () -> Property.checkName( "" ) );
    }

    @Test
    void check_exception_is_thrown_when_name_contains_dot()
    {
        assertThrows( IllegalArgumentException.class, () -> Property.checkName( "." ) );
    }

    @Test
    void check_exception_is_thrown_when_name_contains_brackets()
    {
        assertThrows( IllegalArgumentException.class, () -> Property.checkName( "[]" ) );
    }

}
