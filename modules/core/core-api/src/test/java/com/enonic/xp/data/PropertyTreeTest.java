package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.support.SerializableUtils;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyTreeTest
{
    @Test
    void equalsContract() {
        final PropertySet expression1 = new PropertySet(null, 0);
        expression1.addString( "field", "myField" );

        final PropertySet expression2 = new PropertySet(null, 0);
        expression2.addString( "field", "anotherField" );

        EqualsVerifier.forClass( PropertyTree.class )
            .suppress( Warning.NONFINAL_FIELDS, Warning.TRANSIENT_FIELDS )
            .withNonnullFields( "root" )
            .withPrefabValues( PropertySet.class, expression1, expression2 )
            .verify();
    }

    @Test
    void copy()
    {
        PropertyTree original = new PropertyTree();
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
    void given_set_with_no_properties_when_addProperty_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree();
        Value myValue = ValueFactory.newString( "myValue" );
        Property property = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", property.getName() );
        assertEquals( 0, property.getIndex() );
        assertSame( myValue, property.getValue() );
    }

    @Test
    void given_set_with_existing_property_when_addProperty_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree();
        tree.addProperty( "existing", ValueFactory.newString( "existing" ) );
        Value myValue = ValueFactory.newString( "myValue" );
        Property myProp = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", myProp.getName() );
        assertEquals( 0, myProp.getIndex() );
        assertSame( myValue, myProp.getValue() );
    }

    @Test
    void given_set_with_existing_property_when_addProperty_with_same_name_then_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree();
        tree.addProperty( "myProp", ValueFactory.newString( "existing" ) );
        Value myValue = ValueFactory.newString( "myValue" );
        Property myProp = tree.addProperty( "myProp", myValue );

        assertEquals( "myProp", myProp.getName() );
        assertEquals( 1, myProp.getIndex() );
        assertSame( myValue, myProp.getValue() );
    }

    @Test
    void given_set_with_existing_property_when_addProperty_with_same_name_but_different_ValueType_then_exception_is_thrown()
    {
        PropertyTree tree = new PropertyTree();
        tree.addProperty( "myProp", ValueFactory.newString( "otherType" ) );

        assertThrows(IllegalArgumentException.class, () -> tree.addProperty( "myProp", ValueFactory.newBoolean( true ) ) );
    }

    @Test
    void given_added_Property_when_getProperty_with_id_of_added_Property_then_same_Property_is_returned()
    {
        PropertyTree tree = new PropertyTree();
        Value myValue = ValueFactory.newString( "myValue" );
        Property my1Prop = tree.addProperty( "my1Prop", myValue );
        Property my2Prop = tree.addProperty( "my2Prop", myValue );
        Property my2PropSecond = tree.addProperty( "my2Prop", myValue );

        assertSame( my1Prop, tree.getProperty( my1Prop.getPath() ) );
        assertSame( my2Prop, tree.getProperty( my2Prop.getPath() ) );
        assertSame( my2PropSecond, tree.getProperty( my2PropSecond.getPath() ) );
    }

    @Test
    void given_Property_with_PropertySet_when_getPropertySet_then_same_PropertySet_is_returned()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set = tree.newSet();
        tree.addProperty( "myProp", ValueFactory.newPropertySet( set ) );

        assertSame( set, tree.getSet( PropertyPath.from( "myProp" ) ) );
    }

    @Test
    void given_ifNotNull_is_true_adding_property_with_null_then_tree_is_still_empty()
    {
        PropertyTree tree = new PropertyTree();
        assertNull( tree.ifNotNull().addString( "myNull", null ) );
        assertEquals( 0, tree.getTotalSize() );
    }

    @Test
    void given_ifNotNull_is_false_adding_property_with_null_then_tree_is_not_empty()
    {
        PropertyTree tree = new PropertyTree();
        assertNotNull( tree.addString( "myNull", null ) );
        assertEquals( 1, tree.getTotalSize() );
    }

    @Test
    void newSet()
    {
        PropertyTree sourceTree = new PropertyTree();
        sourceTree.addString( "myProp", "myString" );

        PropertyTree newTree = new PropertyTree();
        PropertySet set = newTree.newSet();
        for ( final Property sourceProperty : sourceTree.getProperties() )
        {
            set.addProperty( sourceProperty.getName(), sourceProperty.getValue() );
        }

        newTree.addSet( "mySet", set );

        assertEquals( "myString", newTree.getString( "mySet.myProp" ) );
    }

    @Test
    void adding_root_PropertySet_must_throw_IllegalArgumentException()
    {
        PropertyTree tree = new PropertyTree();
        assertThrows(IllegalArgumentException.class, () -> tree.addSet( "myProp", tree.getRoot() ) );
    }

    @Test
    void setting_root_PropertySet_must_throw_IllegalArgumentException()
    {
        PropertyTree tree = new PropertyTree();
        assertThrows(IllegalArgumentException.class, () -> tree.setSet( "myProp", tree.getRoot() ) );
    }

    @Test
    void setting_property_should_not_add_new_in_propertyByIdMap()
    {
        PropertyTree tree = new PropertyTree();
        tree.setString( "myProperty", "a" );
        tree.setString( "myProperty", "b" );

        assertEquals( 1, tree.getTotalSize() );
    }

    @Test
    void tostring_propertyArray_of_type__String()
    {
        PropertyTree tree = new PropertyTree();
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
    void tostring_propertyArray_of_type__Long()
    {
        PropertyTree tree = new PropertyTree();
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
    void tostring_propertyArray_of_type__Reference()
    {
        PropertyTree tree = new PropertyTree();
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
    void tostring_propertyArray_of_type_Link()
    {
        PropertyTree tree = new PropertyTree();
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
    void tostring_single_property_of_type_PropertySet()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );
        set1.addStrings( "strings", "a", "b" );

        String expected = "";
        expected += "[\n";
        expected += "  mySet: [\n";
        expected += "    [\n";
        expected += "        longs: [1, 2],\n";
        expected += "        strings: [a, b]\n";
        expected += "    ]\n";
        expected += "  ]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    void tostring_propertyArray_of_type_PropertySet()
    {
        PropertyTree tree = new PropertyTree();
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
        expected += "        longs: [1, 2],\n";
        expected += "        strings: [a, b]\n";
        expected += "    ],\n";
        expected += "    [\n";
        expected += "        longs: [1, 2],\n";
        expected += "        strings: [a, b]\n";
        expected += "    ]\n";
        expected += "  ]\n";
        expected += "]";
        assertEquals( expected, tree.toString() );
    }

    @Test
    void tostring_PropertySet_within_PropertySet()
    {
        PropertyTree tree = new PropertyTree();
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
        expected += "        a: [1, 1, 1],\n";
        expected += "        b: [2, 2, 2],\n";
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
    void getByValueType()
    {
        PropertySet set = new PropertyTree().newSet();
        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );

        PropertyTree tree = new PropertyTree( set );

        final List<Property> stringProperties = tree.getProperties( ValueTypes.STRING );
        assertEquals( 2, stringProperties.size() );
    }

    @Test
    void toMap()
    {
        PropertyTree tree = new PropertyTree();
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
    void countNames()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = tree.addSet( "mySet" );
        set1.addLongs( "longs", 1L, 2L );

        assertEquals( 2, tree.countNames( "mySet" ) );
    }

    @Test
    void setValues()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        List<Value> stringValueList = new ArrayList<>();
        stringValueList.add( ValueFactory.newString( "d" ) );
        stringValueList.add( ValueFactory.newString( "f" ) );

        List<Value> longValueList = new ArrayList<>();
        longValueList.add( ValueFactory.newLong( 1L ) );

        tree.setValues( "mySet.strings", stringValueList );
        tree.setValues( PropertyPath.from( "longs" ), longValueList );

        assertEquals( "d", tree.getString( PropertyPath.from( "mySet.strings" ) ) );
        assertEquals( "f", tree.getString( PropertyPath.from( "mySet.strings[1]" ) ) );
        assertNull( tree.getString( PropertyPath.from( "mySet.strings[2]" ) ) );
        Property p = tree.getProperty( PropertyPath.from( "longs" ) );
        assertEquals( 1L, p.getLong() );
    }

    @Test
    void removeProperties()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        assertNotNull( tree.getProperty( "mySet" ) );
        tree.removeProperties( "mySet" );
        assertNull( tree.getProperty( "mySet" ) );
    }

    @Test
    void replaceStringWithLong()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addString( "strings", "a" );
        set1.removeProperty( "strings" );
        set1.setLong( "strings", 1L );
    }

    @Test
    void removeProperty()
    {
        PropertyTree tree = new PropertyTree();
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
    void hasProperty()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.addSet( "mySet" );
        set1.addStrings( "strings", "a", "b", "c" );

        assertTrue( tree.hasProperty( "mySet" ) );
        assertTrue( tree.hasProperty( PropertyPath.from( "mySet.strings" ) ) );
        assertTrue( tree.hasProperty( "mySet", 0 ) );
        assertFalse( tree.hasProperty( "nonExistingSet" ) );
    }

    @Test
    void getPropertySet()
    {
        PropertyTree tree = new PropertyTree();

        PropertySet set1 = tree.addSet( "mySet1" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = tree.addSet( "mySet2" );
        set1.addLongs( "longs", 1L, 2L );

        assertEquals( set1, tree.getPropertySet( "mySet1" ) );
        assertEquals( set2, tree.getPropertySet( PropertyPath.from( "mySet2" ) ) );
    }

    @Test
    void setSet()
    {
        PropertyTree tree = new PropertyTree();

        PropertySet set1 = tree.addSet( "mySet1" );
        set1.addStrings( "strings", "a", "b", "c" );

        PropertySet set2 = tree.newSet();
        set2.addLongs( "longs", 1L, 2L );

        PropertySet set3 = new PropertySet( null, 0);
        set3.addLongs( "newlongs", 4L, 6L );

        tree.setSet( PropertyPath.from( "mySet1" ), set2 );
        assertEquals( set2, tree.getSet( "mySet1" ) );

        tree.setSet( "mySet1", 0, set3 );
        assertEquals( set3, tree.getSet( "mySet1" ) );
    }

    @Test
    void setXml()
    {
        PropertyTree tree = new PropertyTree();

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
    void setBinaryReference()
    {
        PropertyTree tree = new PropertyTree();

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
    void setReference()
    {
        PropertyTree tree = new PropertyTree();

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
    void setLink()
    {
        PropertyTree tree = new PropertyTree();

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
    void setBoolean()
    {
        PropertyTree tree = new PropertyTree();

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
    void setLong()
    {
        PropertyTree tree = new PropertyTree();

        tree.setLong( "Long1", 1L );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( 1, tree.getLong( "Long1" ) );

        tree.setLong( PropertyPath.from( "Long2" ), 2L );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( 2, tree.getLong( PropertyPath.from( "Long2" ) ) );

        tree.setLong( "Long3", 0, 3L );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( 3, tree.getLong( "Long3", 0 ) );

        assertEquals( 3, tree.getLongs( "Long3" ).iterator().next() );
    }

    @Test
    void setDouble()
    {
        PropertyTree tree = new PropertyTree();

        tree.setDouble( "Double1", 1.0 );
        assertEquals( 1, tree.getTotalSize() );
        assertEquals( 1, tree.getDouble( "Double1" ) );

        tree.setDouble( PropertyPath.from( "Double2" ), 2.0 );
        assertEquals( 2, tree.getTotalSize() );
        assertEquals( 2, tree.getDouble( PropertyPath.from( "Double2" ) ) );

        tree.setDouble( "Double3", 0, 3.0 );
        assertEquals( 3, tree.getTotalSize() );
        assertEquals( 3, tree.getDouble( "Double3", 0 ) );

        assertEquals( 3, tree.getDoubles( "Double3" ).iterator().next() );
    }

    @Test
    void setGeoPoint()
    {
        PropertyTree tree = new PropertyTree();

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
    void setLocalDate()
    {
        PropertyTree tree = new PropertyTree();

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
    void setLocalDateTime()
    {
        PropertyTree tree = new PropertyTree();

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
    void setLocalTime()
    {
        PropertyTree tree = new PropertyTree();

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
    void setInstant()
    {
        PropertyTree tree = new PropertyTree();

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

    @Test
    void setSerialization()
    {
        PropertyTree source = createTreeWithAllTypes();

        final byte[] serializedObject = SerializableUtils.serialize( source );
        final PropertyTree deserializedObject = (PropertyTree) SerializableUtils.deserialize( serializedObject );

        assertEquals( source, deserializedObject );
    }

    private PropertyTree createTreeWithAllTypes()
    {
        final PropertyTree tree = new PropertyTree();
        tree.addString( "singleString", "a" );
        tree.addString( "nullString", null );
        tree.addXml( "singleXML", "<xml>Hello</xml>" );
        tree.addBoolean( "singleBoolean", true );
        tree.addLong( "singleLong", 1L );
        tree.addDouble( "singleDouble", 1.1 );
        tree.addGeoPoint( "singleGeoPoint", GeoPoint.from( "1.1,-2.2" ) );
        tree.addLocalDate( "singleLocalDate", LocalDate.of( 2006, 1, 8 ) );
        tree.addLocalDateTime( "singleLocalDateTime", LocalDateTime.of( 2006, 1, 8, 12, 0, 0 ) );
        tree.addLocalTime( "singleLocalTime", LocalTime.of( 12, 0, 0 ) );
        tree.addInstant( "singleInstant", Instant.parse( "2007-12-03T10:15:30.00Z" ) );
        tree.addReference( "reference", Reference.from( "my-node-id" ) );
        tree.addBinaryReference( "binaryRef", BinaryReference.from( "myImage" ) );
        tree.addLink( "link", Link.from( "/root/my-node" ) );
        PropertySet singleSet = tree.addSet( "singleSet" );
        singleSet.addLong( "long", 1L );
        PropertySet setWithinSet = singleSet.addSet( "setWithinSet" );
        setWithinSet.addLong( "long", 1L );

        tree.addStrings( "arrayString", "a", "b" );
        tree.addXmls( "arrayXML", "<xml>Hello</xml>", "<xml>World</xml>" );
        tree.addBooleans( "arrayBoolean", true, false );
        tree.addLongs( "arrayLong", 1L, 2L );
        tree.addDoubles( "arrayDouble", 1.1, 1.2 );
        tree.addLocalDates( "arrayLocalDates", LocalDate.of( 2006, 1, 8 ), LocalDate.of( 2015, 1, 31 ) );
        tree.addLocalDateTimes( "arrayLocalDateTimes", LocalDateTime.of( 2006, 1, 8, 12, 0, 0 ),
                                LocalDateTime.of( 2015, 1, 31, 12, 0, 0 ) );
        tree.addGeoPoints( "arrayGeoPoint", GeoPoint.from( "1.1,-2.2" ), GeoPoint.from( "-2.2,1.1" ) );
        tree.addReferences( "references", Reference.from( "my-node-id-1" ), Reference.from( "my-node-id-2" ) );
        tree.addLinks( "links", Link.from( "/root/my-node-1" ), Link.from( "/root/my-node-2" ) );
        tree.addBinaryReferences( "binaryReferences", BinaryReference.from( "image1" ), BinaryReference.from( "image2" ) );

        PropertySet arraySet1 = tree.addSet( "arraySet" );
        arraySet1.addString( "string", "a" );
        arraySet1.addLongs( "long", 1L, 2L );
        PropertySet arraySet2 = tree.addSet( "arraySet" );
        arraySet2.addStrings( "string", "b", "c" );
        arraySet2.addLong( "long", 2L );

        tree.addSet( "nullSet", null );

        return tree;
    }


    @Test
    void fromMap()
    {
        final HashMap<String, Object> map = new HashMap<>();
        map.put( "myString", "a" );
        map.put( "myDoable", 1.1 );
        map.put("myFloat", 1.1f);
        map.put( "myInt", 1 );
        map.put( "myLong", 1L );
        map.put( "myByte", (byte) 1 );
        map.put( "myShort", (short) 1 );
        map.put( "myBoolean", true );
        map.put( "myNull", null );
        map.put( "myList", List.of( "a", "b" ) );
        map.put( "myMap", Map.of( "a", "b" ) );
        map.put( "myMapMap", Map.of( "a", Map.of("k", "v") ) );
        map.put( "mySet", Set.of( "a", "b" ) );
        map.put( "myEmptyList", List.of() );
        map.put( "myEmptyMap", Map.of() );
        map.put( "myGeoPoint", GeoPoint.from( "0,1" ));
        map.put( "myInstant", Instant.parse( "2018-01-01T00:00:00Z" ) );
        map.put( "myDate", Date.from( Instant.parse( "2018-01-01T00:00:00Z" ) ) );
        map.put( "myLocalDate", LocalDate.parse( "2018-01-01" ) );
        map.put( "myLocalDateTime", LocalDate.parse( "2018-01-01" ).atStartOfDay() );
        map.put( "myLocalTime", LocalDate.parse( "2018-01-01" ).atStartOfDay().toLocalTime() );
        map.put( "myReference", Reference.from( "nodeId" ) );
        map.put( "myBinaryReference", BinaryReference.from( "binaryReference" ) );
        map.put( "myLink", Link.from( "/link" ) );
        map.put( "myObject", NodeId.from( "becomeString") );

        final PropertyTree result = PropertyTree.fromMap( map );

        assertEquals( "a", result.getString( "myString" ) );
        assertEquals( 1.1D, result.getDouble( "myDoable" ) );
        assertThat( result.getDouble( "myFloat" ) ).isEqualTo(1.1, withPrecision(0.001));
        assertEquals( 1L, result.getLong( "myInt" ) );
        assertEquals( 1L, result.getLong( "myLong" ) );
        assertEquals( 1L, result.getLong( "myByte" ) );
        assertEquals( 1L, result.getLong( "myShort" ) );
        assertTrue( result.getBoolean( "myBoolean" ) );
        assertNull( result.getString( "myNull" ) );
        assertEquals( "a", result.getString( "myList[0]" ) );
        assertEquals( "b", result.getString( "myList[1]" ) );
        assertEquals( "b", result.getString( "myMap.a" ) );
        assertEquals( "v", result.getString( "myMapMap.a.k" ) );
        assertThat( result.getStrings( "mySet" ) ).containsExactlyInAnyOrder( "a", "b" );
        assertThat( result.getStrings( "myEmptyList" ) ).isEmpty();
        assertThat( result.getSet( "myEmptyMap" ).getProperties() ).isEmpty();
        assertEquals( GeoPoint.from( "0,1" ), result.getGeoPoint( "myGeoPoint" ) );
        assertEquals( Instant.parse( "2018-01-01T00:00:00Z" ), result.getInstant( "myInstant" ) );
        assertEquals( Instant.parse( "2018-01-01T00:00:00Z" ) , result.getInstant( "myDate" ) );
        assertEquals( LocalDate.parse( "2018-01-01" ), result.getLocalDate( "myLocalDate" ) );
        assertEquals( LocalDate.parse( "2018-01-01" ).atStartOfDay(), result.getLocalDateTime( "myLocalDateTime" ) );
        assertEquals( LocalDate.parse( "2018-01-01" ).atStartOfDay().toLocalTime(), result.getLocalTime( "myLocalTime" ) );
        assertEquals( Reference.from( "nodeId" ), result.getReference( "myReference" ) );
        assertEquals( BinaryReference.from( "binaryReference" ), result.getBinaryReference( "myBinaryReference" ) );
        assertEquals( Link.from( "/link" ), result.getLink( "myLink" ) );
        assertEquals( "becomeString", result.getString( "myObject" ) );
    }

}

