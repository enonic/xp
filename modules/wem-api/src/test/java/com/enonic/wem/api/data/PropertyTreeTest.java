package com.enonic.wem.api.data;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;
import com.enonic.wem.api.util.Link;
import com.enonic.wem.api.util.Reference;

import static org.junit.Assert.*;

public class PropertyTreeTest
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
                tree.addString( "myString", "myValue" );
                PropertySet set = tree.addSet( "mySet" );
                set.addString( "myString", "myValue" );
                return tree;
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                PropertyTree tree1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                tree1.addString( "myString", "myValue" );
                PropertySet set1 = tree1.addSet( "mySet" );
                set1.addString( "myString", "otherValue" );

                PropertyTree tree2 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                tree2.addString( "myString", "otherValue" );
                PropertySet set2 = tree2.addSet( "mySet" );
                set2.addString( "myString", "myValue" );

                PropertyTree tree3 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                tree3.addString( "myString", "myValue" );
                PropertySet set3 = tree3.addSet( "myOtherSet" );
                set3.addString( "myString", "myValue" );

                return new Object[]{tree1, tree2, tree3};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                tree.addString( "myString", "myValue" );
                PropertySet set = tree.addSet( "mySet" );
                set.addString( "myString", "myValue" );
                return tree;
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {

                PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
                tree.addString( "myString", "myValue" );
                PropertySet set = tree.addSet( "mySet" );
                set.addString( "myString", "myValue" );
                return tree;
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void copy()
    {
        PropertyTree original = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        original.setString( "myString", "a" );
        original.setString( "mySet.myString", "1" );

        assertEquals( "a", original.getString( "myString" ) );
        assertEquals( "1", original.getString( "mySet.myString" ) );

        PropertyTree copy = original.copy();
        copy.setString( "myString", "b" );
        copy.setString( "mySet.myString", "2" );

        assertEquals( "b", copy.getString( "myString" ) );
        assertEquals( "2", copy.getString( "mySet.myString" ) );

        assertEquals( "a", original.getString( "myString" ) );
        assertEquals( "1", original.getString( "mySet.myString" ) );
    }

    @Test
    public void given_set_with_no_properties_when_addProperty_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        Value myValue = Value.newString( "myValue" );
        Property property = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", property.getName() );
        assertEquals( 0, property.getIndex() );
        assertSame( myValue, property.getValue() );
    }

    @Test
    public void given_set_with_existing_property_when_addProperty_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addProperty( "existing", Value.newString( "existing" ) );
        Value myValue = Value.newString( "myValue" );
        Property myProp = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", myProp.getName() );
        assertEquals( 0, myProp.getIndex() );
        assertSame( myValue, myProp.getValue() );
    }

    @Test
    public void given_set_with_existing_property_when_addProperty_with_same_name_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addProperty( "myProp", Value.newString( "existing" ) );
        Value myValue = Value.newString( "myValue" );
        Property myProp = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", myProp.getName() );
        assertEquals( 1, myProp.getIndex() );
        assertSame( myValue, myProp.getValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_set_with_existing_property_when_addProperty_with_same_name_but_different_ValueType_then_exception_is_thrown()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addProperty( "myProp", Value.newString( "otherType" ) );
        tree.addProperty( "myProp", Value.newBoolean( true ) );
    }

    @Test
    public void given_added_Property_when_getProperty_with_id_of_added_Property_then_same_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        Value myValue = Value.newString( "myValue" );
        Property my1Prop = tree.addProperty( "my1Prop", myValue );
        Property my2Prop = tree.addProperty( "my2Prop", myValue );
        Property my2PropSecond = tree.addProperty( "my2Prop", myValue );

        assertSame( my1Prop, tree.getProperty( my1Prop.getId() ) );
        assertSame( my2Prop, tree.getProperty( my2Prop.getId() ) );
        assertSame( my2PropSecond, tree.getProperty( my2PropSecond.getId() ) );
    }

    @Test
    public void given_Property_with_PropertySet_when_getPropertySet_then_same_PropertySet_is_returned()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set = tree.newSet();
        tree.addProperty( "myProp", Value.newData( set ) );

        assertSame( set, tree.getSet( PropertyPath.from( "myProp" ) ) );
    }

    @Test
    public void given_ifNotNull_is_true_adding_property_with_null_then_tree_is_still_empty()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        assertNull( tree.ifNotNull().addString( "myNull", null ) );
        assertEquals( 0, tree.getTotalSize() );
    }

    @Test
    public void given_ifNotNull_is_false_adding_property_with_null_then_tree_is_not_empty()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        assertNotNull( tree.addString( "myNull", null ) );
        assertEquals( 1, tree.getTotalSize() );
    }

    @Test
    public void newSet()
    {
        PropertyTree sourceTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        sourceTree.addString( "myProp", "myString" );

        PropertyTree newTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set = newTree.newSet( sourceTree );
        newTree.addSet( "mySet", set );

        assertEquals( "myString", newTree.getString( "mySet.myProp" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void adding_root_PropertySet_must_throw_IllegalArgumentException()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addSet( "myProp", tree.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_root_PropertySet_must_throw_IllegalArgumentException()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.setSet( "myProp", tree.getRoot() );
    }

    @Test
    public void setting_property_should_not_add_new_in_propertyByIdMap()
        throws Exception
    {
        PropertyTree tree = new PropertyTree();
        tree.setString( "myProperty", "a" );
        tree.setString( "myProperty", "b" );

        assertEquals( 1, tree.getTotalSize() );
    }

    @Test
    public void tostring_propertyArray_of_type__String()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addString( "myProp", "a" );
        tree.addString( "myProp", "b" );
        tree.addString( "myProp", "c" );

        String expected = "";
        expected += "[\n";
        expected += "  myProp: [a, b, c]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    public void tostring_propertyArray_of_type__Long()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addLong( "myProp", 1L );
        tree.addLong( "myProp", 2L );
        tree.addLong( "myProp", 3L );

        String expected = "";
        expected += "[\n";
        expected += "  myProp: [1, 2, 3]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    public void tostring_propertyArray_of_type__Reference()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addReference( "myProp", Reference.from( "test-1" ) );
        tree.addReference( "myProp", Reference.from( "test-2" ) );
        tree.addReference( "myProp", Reference.from( "test-3" ) );

        String expected = "";
        expected += "[\n";
        expected += "  myProp: [test-1, test-2, test-3]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    public void tostring_propertyArray_of_type_Link()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        tree.addLink( "myLink", Link.from( "/root/me" ) );
        tree.addLink( "myLink", Link.from( "./child" ) );
        tree.addLink( "myLink", Link.from( "child/image" ) );

        String expected = "";
        expected += "[\n";
        expected += "  myLink: [/root/me, ./child, child/image]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    @Ignore
    public void tostring_single_property_of_type_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );
        set1.addStrings( "strings", "a", "b" );

        String expected = "";
        expected += "[\n";
        expected += "  mySet: [\n";
        expected += "    [\n";
        expected += "      longs: [1, 2],\n";
        expected += "      strings: [\"a\", \"b\"]\n";
        expected += "    ]\n";
        expected += "  ]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    @Ignore
    public void tostring_propertyArray_of_type_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );
        set1.addStrings( "strings", "a", "b" );

        PropertySet set2 = tree.addSet( "mySet" );
        set2.addLongs( "longs", 1L, 2L );
        set2.addStrings( "strings", "a", "b" );

        String expected = "";
        expected += "[\n";
        expected += "  mySet: [\n";
        expected += "    [\n";
        expected += "      longs: [1, 2],\n";
        expected += "      strings: [\"a\", \"b\"]\n";
        expected += "    ],\n";
        expected += "    [\n";
        expected += "      longs: [1, 2],\n";
        expected += "      strings: [\"a\", \"b\"]\n";
        expected += "    ]\n";
        expected += "  ]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    @Ignore
    public void tostring_PropertySet_within_PropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addLongs( "a", 1L, 1L, 1L );
        set1.addLongs( "b", 2L, 2L, 2L );
        PropertySet subSet = set1.addSet( "subSet" );
        subSet.addLongs( "c", 3L, 3L, 3L );
        subSet.addLongs( "d", 4L, 4L, 4L );

        String expected = "";
        expected += "[\n";
        expected += "  mySet: [\n";
        expected += "    [\n";
        expected += "      a: [1, 1, 1],\n";
        expected += "      b: [2, 2, 2],\n";
        expected += "      subSet: [\n";
        expected += "        [\n";
        expected += "          c: [3, 3, 3],\n";
        expected += "          d: [4, 4, 4]\n";
        expected += "        ]\n";
        expected += "      ]\n";
        expected += "    ]\n";
        expected += "  ]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }
}
