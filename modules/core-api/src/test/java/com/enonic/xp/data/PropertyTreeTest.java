package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

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

                return new Object[]{tree1, tree2, tree3, new Object()};
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
        copy.setString( PropertyPath.from( "mySet.myString" ), "2" );

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

    @Test
    public void getByValueType()
    {
        PropertySet set = new PropertySet( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) );
        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );

        PropertyTree tree = new PropertyTree( set );

        Set<Property> extractedSet = tree.getByValueType( ValueTypes.STRING );
        assertEquals( 2, extractedSet.size() );
    }

    @Test
    public void toMap()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );
        set1.addStrings( "strings", "a", "b" );

        PropertySet set2 = tree.addSet( "mySet2" );
        set1.addLongs( "longs", 1L, 2L );
        set1.addStrings( "strings", Arrays.asList( "a", "b" ) );

        Map<String, Object> map = tree.toMap();

        assertNotNull( map );
        assertEquals( 2, map.size() );
    }

    @Test
    public void countNames()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );

        assertEquals( 2, tree.countNames( "mySet" ) );
    }

    @Test
    public void setValues()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        List<Value> stringValueList = new ArrayList<>();
        stringValueList.add( new Value.String( "d" ) );

        List<Value> longValueList = new ArrayList<>();
        longValueList.add( new Value.Long( 1L ) );

        tree.setValues( "mySet.strings", stringValueList );
        tree.setValues( PropertyPath.from( "longs" ), longValueList );

        assertEquals( "d", tree.getString( PropertyPath.from( "mySet.strings" ) ) );
        Property p = tree.getProperty( PropertyPath.from( "longs" ) );
        assertEquals( new Long( 1 ), p.getLong() );
    }

    @Test
    public void removeProperties()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        assertNotNull( tree.getProperty( "mySet" ) );
        tree.removeProperties( "mySet" );
        assertNull( tree.getProperty( "mySet" ) );
    }

    @Test
    public void removeProperty()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        assertNotNull( tree.getProperty( "mySet" ) );
        assertEquals( "a", tree.getString( PropertyPath.from( "mySet.strings" ) ) );
        tree.removeProperty( PropertyPath.from( "mySet.strings" ) );
        assertEquals( "b", tree.getString( PropertyPath.from( "mySet.strings" ) ) );
        tree.removeProperty( "mySet" );
        assertNull( tree.getProperty( "mySet" ) );
    }

    @Test
    public void hasProperty()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        assertTrue( tree.hasProperty( "mySet" ) );
        assertTrue( tree.hasProperty( PropertyPath.from( "mySet.strings" ) ) );
        assertTrue( tree.hasProperty( "mySet", 0 ) );
        assertFalse( tree.hasProperty( "nonExistingSet" ) );
    }

    @Test
    public void getPropertySet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        PropertySet set1 = tree.addSet( "mySet1" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = tree.addSet( "mySet2" );
        set1.addLongs( "longs", 1L, 2L );

        assertEquals( set1, tree.getPropertySet( "mySet1" ) );
        assertEquals( set2, tree.getPropertySet( PropertyPath.from( "mySet2" ) ) );
    }

    @Test
    public void setSet()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        PropertySet set1 = tree.addSet( "mySet1" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = new PropertySet();
        set2.addLongs( "longs", 1L, 2L );

        PropertySet set3 = new PropertySet();
        set3.addLongs( "newlongs", 4L, 6L );

        tree.setSet( PropertyPath.from( "mySet1" ), set2 );
        assertEquals( set2, tree.getSet( "mySet1" ) );

        tree.setSet( "mySet1", 0, set3 );
        assertEquals( set3, tree.getSet( "mySet1" ) );
    }

    @Test
    public void setHtmlPart()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        String htmlData = "<p>This is the text of the test post</p>";
        tree.setHtmlPart( "post", htmlData );
        assertEquals( htmlData, tree.getProperty( "post" ).getString() );

        String htmlData2 = "Test html post with no markup";
        tree.setHtmlPart( PropertyPath.from( "post2" ), htmlData2 );
        assertEquals( htmlData2, tree.getProperty( "post2" ).getString() );
        assertEquals( 2, tree.getTotalSize() );

        String htmlData3 = "<div id='Test html'>";
        tree.setHtmlPart( "post", 0, htmlData3 );
        assertEquals( htmlData3, tree.getProperty( "post" ).getString() );
        assertEquals( 2, tree.getTotalSize() );
    }

    @Test
    public void setXml()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        String xmlData = "<xml>my test xml</xml>";
        tree.setXml( "myXML", xmlData );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( xmlData, tree.getProperty( "myXML" ).getString() );

        String xmlData2 = "it is xml";
        tree.setXml( PropertyPath.from( "myXML2" ), xmlData2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( xmlData2, tree.getProperty( "myXML2" ).getString() );

        String xmlData3 = "<a b='it is not xml'/>";
        tree.setXml( "myXML2", 0, xmlData3 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( xmlData3, tree.getProperty( "myXML2" ).getString() );
    }

    @Test
    public void setBinaryReference()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        BinaryReference binaryReference1 = BinaryReference.from( "ref1" );
        tree.setBinaryReference( "binaryRef1", binaryReference1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( binaryReference1, tree.getBinaryReference( "binaryRef1" ) );

        BinaryReference binaryReference2 = BinaryReference.from( "ref2" );
        tree.setBinaryReference( PropertyPath.from( "binaryRef2" ), binaryReference2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( binaryReference2, tree.getBinaryReference( PropertyPath.from( "binaryRef2" ) ) );

        BinaryReference binaryReference3 = BinaryReference.from( "ref3" );
        tree.setBinaryReference( "binaryRef3", 0, binaryReference3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( binaryReference3, tree.getBinaryReference( "binaryRef3", 0 ) );

        assertEquals( "ref3", tree.getBinaryReferences( "binaryRef3" ).iterator().next().toString() );
    }

    @Test
    public void setReference()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        Reference reference1 = Reference.from( "myRef1" );
        tree.setReference( "ref1", reference1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( reference1, tree.getReference( "ref1" ) );

        Reference reference2 = Reference.from( "myRef2" );
        tree.setReference( PropertyPath.from( "ref2" ), reference2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( reference2, tree.getReference( PropertyPath.from( "ref2" ) ) );

        Reference reference3 = Reference.from( "myRef3" );
        tree.setReference( "ref3", 0, reference3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( reference3, tree.getReference( "ref3", 0 ) );

        assertEquals( "myRef3", tree.getReferences( "ref3" ).iterator().next().toString() );
    }

    @Test
    public void setLink()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        Link link1 = Link.from( "enonic.com" );
        tree.setLink( "enonic", link1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( link1, tree.getLink( "enonic" ) );

        Link link2 = Link.from( "wiki.enonic.com" );
        tree.setLink( PropertyPath.from( "wiki" ), link2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( link2, tree.getLink( PropertyPath.from( "wiki" ) ) );

        Link link3 = Link.from( "youtrack.enonic.net" );
        tree.setLink( "youtrack", 0, link3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( link3, tree.getLink( "youtrack", 0 ) );

        assertEquals( "youtrack.enonic.net", tree.getLinks( "youtrack" ).iterator().next().toString() );
    }

    @Test
    public void setBoolean()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        tree.setBoolean( "boolean1", true );
        assertEquals( 1, tree.getTotalSize() );
        assertTrue( tree.getBoolean( "boolean1" ) );

        tree.setBoolean( PropertyPath.from( "boolean2" ), false );
        assertEquals( 2, tree.getTotalSize() );
        assertFalse( tree.getBoolean( PropertyPath.from( "boolean2" ) ) );

        tree.setBoolean( "boolean3", 0, true );
        assertEquals( 3, tree.getTotalSize() );
        assertTrue( tree.getBoolean( "boolean3", 0 ) );

        assertTrue( tree.getBooleans( "boolean1" ).iterator().next() );
    }


    @Test
    public void setLong()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        tree.setLong( "Long1", 1L );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( new Long( 1 ), tree.getLong( "Long1" ) );

        tree.setLong( PropertyPath.from( "Long2" ), 2L );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( new Long( 2 ), tree.getLong( PropertyPath.from( "Long2" ) ) );

        tree.setLong( "Long3", 0, 3L );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( new Long( 3 ), tree.getLong( "Long3", 0 ) );

        assertEquals( new Long( 3 ), tree.getLongs( "Long3" ).iterator().next() );
    }

    @Test
    public void setDouble()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        tree.setDouble( "Double1", 1.0 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( new Double( 1 ), tree.getDouble( "Double1" ) );

        tree.setDouble( PropertyPath.from( "Double2" ), 2.0 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( new Double( 2 ), tree.getDouble( PropertyPath.from( "Double2" ) ) );

        tree.setDouble( "Double3", 0, 3.0 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( new Double( 3 ), tree.getDouble( "Double3", 0 ) );

        assertEquals( new Double( 3 ), tree.getDoubles( "Double3" ).iterator().next() );
    }

    @Test
    public void setGeoPoint()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        GeoPoint geoPoint1 = GeoPoint.from( "60,60" );
        tree.setGeoPoint( "geo1", geoPoint1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( geoPoint1, tree.getGeoPoint( "geo1" ) );

        GeoPoint geo2 = GeoPoint.from( "90,90" );
        tree.setGeoPoint( PropertyPath.from( "geo2" ), geo2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( geo2, tree.getGeoPoint( PropertyPath.from( "geo2" ) ) );

        GeoPoint geo3 = GeoPoint.from( "-20,-35" );
        tree.setGeoPoint( "geo3", 0, geo3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( geo3, tree.getGeoPoint( "geo3", 0 ) );

        assertEquals( geo3, tree.getGeoPoints( "geo3" ).iterator().next() );
    }

    @Test
    public void setLocalDate()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        LocalDate localDate1 = LocalDate.now();
        tree.setLocalDate( "localDate1", localDate1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( localDate1, tree.getLocalDate( "localDate1" ) );

        LocalDate localDate2 = LocalDate.now();
        tree.setLocalDate( PropertyPath.from( "localDate2" ), localDate2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( localDate2, tree.getLocalDate( PropertyPath.from( "localDate2" ) ) );

        LocalDate localDate3 = LocalDate.now();
        tree.setLocalDate( "localDate3", 0, localDate3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( localDate3, tree.getLocalDate( "localDate3", 0 ) );

        assertEquals( localDate3, tree.getLocalDates( "localDate3" ).iterator().next() );
    }

    @Test
    public void setLocalDateTime()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        LocalDateTime localDateTime1 = LocalDateTime.now();
        tree.setLocalDateTime( "localDateTime1", localDateTime1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( localDateTime1, tree.getLocalDateTime( "localDateTime1" ) );

        LocalDateTime localDateTime2 = LocalDateTime.now();
        tree.setLocalDateTime( PropertyPath.from( "localDateTime2" ), localDateTime2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( localDateTime2, tree.getLocalDateTime( PropertyPath.from( "localDateTime2" ) ) );

        LocalDateTime localDateTime3 = LocalDateTime.now();
        tree.setLocalDateTime( "localDateTime3", 0, localDateTime3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( localDateTime3, tree.getLocalDateTime( "localDateTime3", 0 ) );

        assertEquals( localDateTime3, tree.getLocalDateTimes( "localDateTime3" ).iterator().next() );
    }

    @Test
    public void setLocalTime()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        LocalTime localTime1 = LocalTime.now();
        tree.setLocalTime( "localTime1", localTime1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( localTime1, tree.getLocalTime( "localTime1" ) );

        LocalTime localTime2 = LocalTime.now();
        tree.setLocalTime( PropertyPath.from( "localTime2" ), localTime2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( localTime2, tree.getLocalTime( PropertyPath.from( "localTime2" ) ) );

        LocalTime localTime3 = LocalTime.now();
        tree.setLocalTime( "localTime3", localTime3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( localTime3, tree.getLocalTime( "localTime3", 0 ) );

        assertEquals( localTime3, tree.getLocalTimes( "localTime3" ).iterator().next() );
    }

    @Test
    public void setInstant()
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        Instant instant1 = Instant.now();
        tree.setInstant( "instant1", instant1 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( instant1, tree.getInstant( "instant1" ) );

        Instant instant2 = Instant.now();
        tree.setInstant( PropertyPath.from( "instant2" ), instant2 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( instant2, tree.getInstant( PropertyPath.from( "instant2" ) ) );

        Instant instant3 = Instant.now();
        tree.setInstant( "instant3", instant3 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( instant3, tree.getInstant( "instant3", 0 ) );

        assertEquals( instant3, tree.getInstants( "instant3" ).iterator().next() );
    }




}

