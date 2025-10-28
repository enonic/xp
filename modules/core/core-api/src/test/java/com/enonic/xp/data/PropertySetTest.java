package com.enonic.xp.data;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertySetTest
{
    @Test
    void setProperty_given_unsuccessive_index_then_IndexOutOfBoundsException_is_thrown()
    {
        PropertySet set = new PropertyTree().newSet();

        // exercise & verify
        assertThrows(IndexOutOfBoundsException.class, () -> set.setProperty( "myProp", 1, ValueFactory.newString( "myValue" ) ));
    }

    @Test
    void setString_creates_Property()
    {
        PropertySet set = new PropertyTree().newSet();

        // exercise
        Property property = set.setString( "myProp", 0, "myValue" );

        // verify
        assertEquals( "myProp", property.getName() );
        assertEquals( 0, property.getIndex() );
        assertEquals( "myValue", property.getValue().asString() );
    }

    @Test
    void setPropertySet()
    {
        final PropertyTree tree = new PropertyTree();
        PropertySet set = tree.newSet();

        final PropertySet propertySet = tree.newSet();
        propertySet.setString( "myStrProp", "myValue");
        set.setProperty( "myProp", ValueFactory.newPropertySet( propertySet ) );

        assertEquals( "myValue", set.getValue("myProp.myStrProp").asString() );
        assertEquals( "myValue", set.getSet( "myProp" ).getProperty().getSet().getValue( "myStrProp" ).asString() );

        set.setProperty( "myProp", ValueFactory.newPropertySet( tree.newSet() ) );

        assertNull( set.getSet( "myProp" ).getProperty().getSet().getValue( "myStrProp" ) );
    }

    @Test
    void setString_getProperty()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet a = tree.addSet( "mySet" );
        PropertySet b = tree.addSet( "mySet" );
        a.setString( "myProp", 0, "myValue" );

        Property propertyA = a.getProperty();
        Property propertyB = b.getProperty();

        assertEquals( "mySet", propertyA.getName() );
        assertEquals( 0, propertyA.getIndex() );
        assertTrue( propertyA.getValue().isPropertySet() );
        assertEquals( "myValue", propertyA.getValue().asData().getString( "myProp" ) );

        assertEquals( "mySet", propertyB.getName() );
        assertEquals( 1, propertyB.getIndex() );
        assertTrue( propertyB.getValue().isPropertySet() );
        assertNull( propertyB.getValue().asData().getString( "myProp" ) );
    }

    @Test
    void getString()
    {
        PropertySet set = new PropertyTree().newSet();
        set.setString( "myProp", 0, "myValue" );

        // exercise & verify
        assertEquals( "myValue", set.getString( "myProp", 0 ) );
    }

    @Test
    void addLongs()
    {
        PropertySet set = new PropertyTree().newSet();
        Property[] properties = set.addLongs( "longs", 1L, 2L, 3L );

        assertEquals( Long.valueOf( 1L ), properties[0].getLong() );
        assertEquals( Long.valueOf( 2L ), properties[1].getLong() );
        assertEquals( Long.valueOf( 3L ), properties[2].getLong() );
    }

    @Test
    void removeProperties()
    {
        final PropertyTree tree = new PropertyTree();
        PropertySet set = tree.newSet();
        set.addLongs( "longs", 1L, 2L, 3L );
        set.removeProperties( "longs" );

        assertEquals( 0, set.countProperties( "longs" ) );
        assertEquals( 0, tree.getTotalSize() );
    }

    @Test
    void setting_with_same_index_twice_overwrites()
    {
        PropertySet set = new PropertyTree().newSet();
        set.setString( "a", "1" );
        set.setString( "a", "2" );

        assertEquals( "2", set.getString( "a" ) );

        set.setString( "b[0]", "1" );
        set.setString( "b[0]", "2" );

        assertEquals( "2", set.getString( "b", 0 ) );

        set.setString( "set[0].c[0]", "1" );
        set.setString( "set[0].c[0]", "2" );

        assertEquals( "2", set.getString( "set[0].c[0]" ) );

        set.setString( "set[1].d[0]", "1" );
        set.setString( "set[1].d[0]", "2" );
        set.setString( "set[1].d", "3" );

        assertEquals( "3", set.getString( "set[1].d[0]" ) );

        set.setString( "set[1].d[1]", "1" );
        set.setString( "set[1].d[1]", "2" );

        assertEquals( "2", set.getString( "set[1].d[1]" ) );
    }

    @Test
    void when_copy_then_values_within_copied_set_equals()
    {
        PropertyTree sourceTree = new PropertyTree();
        PropertySet setSource = sourceTree.addSet( "setSource" );
        setSource.addStrings( "a", "1", "2" );

        PropertyTree destinationTree = new PropertyTree();
        PropertySet copy = setSource.copy( destinationTree );
        destinationTree.addSet( "setCopy", copy );

        assertEquals( sourceTree.getProperty( "setSource.a[0]" ).getValue(), destinationTree.getProperty( "setCopy.a[0]" ).getValue() );
        assertEquals( sourceTree.getProperty( "setSource.a[1]" ).getValue(), destinationTree.getProperty( "setCopy.a[1]" ).getValue() );
    }

    @Test
    void attaching_detached_PropertySet()
    {
        PropertySet set = new PropertySet(null , 0);
        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );
        PropertySet innerSet = new PropertySet(null, 0);
        Property innerStringProperty = innerSet.addString( "myInnerString", "a" );
        Property innerSetProperty = set.addSet( "innerSet", innerSet );

        PropertyTree tree = new PropertyTree();
        tree.addSet( "mySet", set );

        assertNotNull( tree.getProperty( aProperty.getPath() ) );
        assertNotNull( tree.getProperty( bProperty.getPath() ) );
        assertNotNull( tree.getProperty( innerSetProperty.getPath() ) );
        assertNotNull( tree.getProperty( innerStringProperty.getPath() ) );

        assertSame( aProperty, tree.getProperty( aProperty.getPath() ) );
        assertSame( bProperty, tree.getProperty( bProperty.getPath() ) );
        assertSame( innerSetProperty, tree.getProperty( innerSetProperty.getPath() ) );
        assertSame( innerStringProperty, tree.getProperty( innerStringProperty.getPath() ) );
    }

    @Test
    void toMap()
    {
        PropertySet set = new PropertyTree().newSet();

        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );
        Property cProperty = set.addString( "mySpecialString", "b" );
        Map<String, Object> map = set.toMap();
        assertEquals( 2, map.size() );
        assertEquals( "b", ( (List<String>) map.get( "myString" ) ).get( 1 ) );
    }

    @Test
    void replace_value_with_different_type()
    {
        PropertySet set = new PropertyTree().newSet();

        // exercise
        Property property1 = set.setString( "myProp", 0, "myValue" );
        set.removeProperty( property1.getPath() );
        Property property2 = set.setLong( "myProp", 0, 42L );

        Property addedProperty = set.getProperty( "myProp", 0 );

        // verify
        assertNotNull( addedProperty );
        assertEquals( "myProp", addedProperty.getName() );
        assertEquals( 0, addedProperty.getIndex() );
        assertSame( ValueTypes.LONG, addedProperty.getValue().getType() );
        assertEquals( 42L, addedProperty.getValue().asLong().longValue() );
    }
}
