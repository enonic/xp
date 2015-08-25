package com.enonic.xp.data;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertySetTest
{
    @Test(expected = IndexOutOfBoundsException.class)
    public void setProperty_given_unsuccessive_index_then_IndexOutOfBoundsException_is_thrown()
    {
        PropertySet set = new PropertySet( new PropertyTree() );

        // exercise & verify
        set.setProperty( "myProp", 1, ValueFactory.newString( "myValue" ) );
    }

    @Test
    public void setString_creates_Property()
    {
        PropertySet set = new PropertySet( new PropertyTree() );

        // exercise
        Property property = set.setString( "myProp", 0, "myValue" );

        // verify
        assertEquals( "myProp", property.getName() );
        assertEquals( 0, property.getIndex() );
        assertEquals( "myValue", property.getValue().asString() );
    }

    @Test
    public void getString()
    {
        PropertySet set = new PropertySet( new PropertyTree() );
        set.setString( "myProp", 0, "myValue" );

        // exercise & verify
        assertEquals( "myValue", set.getString( "myProp", 0 ) );
    }

    @Test
    public void countAncestors()
    {
        PropertyTree tree = new PropertyTree();
        PropertySet a = tree.addSet( "a" );
        PropertySet b = a.addSet( "b" );
        PropertySet c = b.addSet( "c" );

        assertEquals( 0, a.countAncestors() );
        assertEquals( 1, b.countAncestors() );
        assertEquals( 2, c.countAncestors() );
    }

    @Test
    public void addLongs()
    {
        PropertySet set = new PropertySet( new PropertyTree() );
        Property[] properties = set.addLongs( "longs", 1L, 2L, 3L );

        assertEquals( Long.valueOf( 1L ), properties[0].getLong() );
        assertEquals( Long.valueOf( 2L ), properties[1].getLong() );
        assertEquals( Long.valueOf( 3L ), properties[2].getLong() );
    }

    @Test
    public void removeProperties()
    {
        final PropertyTree tree = new PropertyTree();
        PropertySet set = new PropertySet( tree );
        set.addLongs( "longs", 1L, 2L, 3L );
        set.removeProperties( "longs" );

        assertEquals( 0, set.countProperties( "longs" ) );
        assertEquals( 0, tree.getTotalSize() );
    }

    @Test
    public void setting_with_same_index_twice_overwrites()
    {
        PropertySet set = new PropertySet( new PropertyTree() );
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
    public void when_copy_then_values_within_copied_set_equals()
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
    public void creating_detached_PropertySet()
    {
        PropertySet set = new PropertySet();

        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );

        assertEquals( "a", aProperty.getString() );
        assertEquals( "b", bProperty.getString() );

        assertEquals( "a", set.getPropertyArray( "myString" ).get( 0 ).getString() );
        assertEquals( "b", set.getPropertyArray( "myString" ).get( 1 ).getString() );

    }

    @Test
    public void attaching_detached_PropertySet()
    {
        PropertySet set = new PropertySet();
        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );
        PropertySet innerSet = new PropertySet();
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
    public void toMap()
    {
        PropertySet set = new PropertySet();
        Property aProperty = set.addString( "myString", "a" );
        Property bProperty = set.addString( "myString", "b" );
        Property cProperty = set.addString( "mySpecialString", "b" );
        Map<String, Object> map = set.toMap();
        assertEquals( 2, map.size() );
        assertEquals( "b", ( (List<String>) map.get( "myString" ) ).get( 1 ) );
    }

    @Test
    public void replace_value_with_different_type()
    {
        PropertySet set = new PropertySet( new PropertyTree() );

        // exercise
        Property property1 = set.setString( "myProp", 0, "myValue" );
        set.removeProperty( property1.getPath() );
        Property property2 = set.setLong( "myProp", 0, 42l );

        Property addedProperty = set.getProperty( "myProp", 0 );

        // verify
        assertNotNull( addedProperty );
        assertEquals( "myProp", addedProperty.getName() );
        assertEquals( 0, addedProperty.getIndex() );
        assertEquals( ValueTypes.LONG, addedProperty.getValue().getType() );
        assertEquals( 42l, addedProperty.getValue().asLong().longValue() );
    }
}