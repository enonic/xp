package com.enonic.xp.data;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;


public class PropertyArrayTest
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
                final PropertyArray propertyArray = new PropertyArray( tree, tree.getRoot(), "myProp", ValueTypes.STRING );
                propertyArray.addValue( Value.newString( "myValue" ) );
                return propertyArray;
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                final PropertyArray propertyArray1 = new PropertyArray( tree, tree.getRoot(), "myProp", ValueTypes.STRING );
                propertyArray1.addValue( Value.newString( "otherValue" ) );

                final PropertyArray propertyArray2 = new PropertyArray( tree, tree.getRoot(), "myProp", ValueTypes.HTML_PART );
                propertyArray2.addValue( Value.newHtmlPart( "otherValue" ) );

                return new Object[]{propertyArray1, propertyArray2};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                final PropertyArray propertyArray = new PropertyArray( tree, tree.getRoot(), "myProp", ValueTypes.STRING );
                propertyArray.addValue( Value.newString( "myValue" ) );
                return propertyArray;
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                final PropertyArray propertyArray = new PropertyArray( tree, tree.getRoot(), "myProp", ValueTypes.STRING );
                propertyArray.addValue( Value.newString( "myValue" ) );
                return propertyArray;
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void parent()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addString( "myString", "a" );
        PropertyArray propertyArray = tree.getRoot().getPropertyArray( "myString" );

        // exercise
        assertSame( tree.getRoot(), propertyArray.getParent() );

        PropertySet mySet = tree.addSet( "mySet" );
        mySet.addString( "myString", "a" );

        // exercise
        assertSame( mySet, mySet.getPropertyArray( "myString" ).getParent() );

    }

    @Test
    public void countAncestors()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addString( "myString", "a" );
        PropertyArray propertyArray = tree.getRoot().getPropertyArray( "myString" );

        // exercise
        assertEquals( 0, propertyArray.countAncestors() );

        PropertySet mySet = tree.addSet( "mySet" );
        mySet.addString( "myString", "a" );
        propertyArray = mySet.getPropertyArray( "myString" );

        // exercise
        assertEquals( 1, propertyArray.countAncestors() );
    }

    @Test
    public void given_existing_Property_at_index_0_when_setting_another_value_without_specifying_index_then_existing_is_overwritten()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.setString( "myString", "a" );

        // exercise
        tree.setString( "myString", "b" );

        // verify
        PropertyArray array = tree.getRoot().getPropertyArray( "myString" );
        assertEquals( 1, array.size() );
        assertEquals( "b", array.get( 0 ).getString() );
    }
}
