package com.enonic.xp.data;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class UsageTest
{
    @Test
    public void setting_property()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );

        tree.setString( "myProp", 0, "myString" );
        tree.setString( "myProp[1]", "myString" );
        tree.setProperty( "myProp", 2, ValueFactory.newString( "myString" ) );
        tree.setProperty( "myProp[3]", ValueFactory.newString( "myString" ) );

        assertEquals( 4, tree.getTotalSize() );
    }

    @Test
    public void adding_property()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );

        tree.addString( "myProp", "myString" );
        tree.addProperty( "myProp", ValueFactory.newString( "myString" ) );

        assertEquals( 2, tree.getTotalSize() );
    }

    @Test
    public void getting_value()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );
        tree.addString( "myProp", "a" );
        tree.addString( "myProp", "b" );

        assertEquals( "a", tree.getProperty( "myProp", 0 ).getString() );
        assertEquals( "a", tree.getValue( "myProp", 0 ).asString() );
        assertEquals( "a", tree.getString( "myProp", 0 ) );
        assertEquals( "b", tree.getString( "myProp[1]" ) );
        assertEquals( Lists.newArrayList( "a", "b" ), tree.getStrings( "myProp" ) );
    }

    @Test
    public void getting_set()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );
        PropertySet set = tree.newSet();
        set.setString( "myProp", "myValue" );
        tree.addProperty( "mySet", ValueFactory.newPropertySet( set ) );

        assertSame( set, tree.getProperty( "mySet", 0 ).getSet() );
        assertSame( set, tree.getValue( "mySet", 0 ).asData() );
        assertSame( set, tree.getSet( "mySet", 0 ) );
        assertSame( set, tree.getSet( "mySet" ) );
        assertSame( "myValue", tree.getString( "mySet.myProp[0]" ) );
    }

    @Test
    public void getting_sets()
    {
        PropertyTree tree = new PropertyTree( new CounterPropertyIdProvider() );
        PropertySet set1 = tree.newSet();
        set1.setString( "myProp", "myValue" );
        tree.addProperty( "mySet", ValueFactory.newPropertySet( set1 ) );
        PropertySet set2 = tree.newSet();
        set1.setString( "myProp", "myValue" );
        tree.addProperty( "mySet", ValueFactory.newPropertySet( set2 ) );

        assertEquals( Lists.newArrayList( set1, set2 ), tree.getSets( "mySet" ) );
        assertSame( set1, tree.getSets( "mySet" ).iterator().next() );
    }

}