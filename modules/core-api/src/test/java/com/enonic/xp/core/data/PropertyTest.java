package com.enonic.xp.core.data;

import org.junit.Test;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.support.AbstractEqualsTest;

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
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                return tree.addString( "myString", "myValue" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                return new Object[]{tree.addString( "myString", "otherValue" ), tree.addString( "otherString", "myValue" ),
                    tree.addHtmlPart( "otherType", "myValue" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                return tree.addString( "myString", "myValue" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                return tree.addString( "myString", "myValue" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void countAncestors()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

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
        PropertyTree sourceTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        sourceTree.setString( "outerSet.innerSet.myString", "myValue" );
        System.out.println( sourceTree );

        PropertyTree destinationTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet destiSet = destinationTree.addSet( "destiSet" );
        sourceTree.getProperty( "outerSet.innerSet" ).copyTo( destiSet );

        System.out.println( destinationTree.toString() );

        // Verify the property ids from the source and destination tree are the same
        assertSame( sourceTree.getProperty( "outerSet.innerSet" ).getId(), destinationTree.getProperty( "destiSet.innerSet" ).getId() );
        assertSame( sourceTree.getProperty( "outerSet.innerSet.myString" ).getId(),
                    destinationTree.getProperty( "destiSet.innerSet.myString" ).getId() );
    }
}
