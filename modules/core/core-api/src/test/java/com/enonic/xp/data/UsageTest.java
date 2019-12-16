package com.enonic.xp.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class UsageTest
{
    @Test
    public void setting_property()
    {
        PropertyTree tree = new PropertyTree();

        tree.setString( "myProp", 0, "myString" );
        tree.setString( "myProp[1]", "myString" );
        tree.setProperty( "myProp", 2, ValueFactory.newString( "myString" ) );
        tree.setProperty( "myProp[3]", ValueFactory.newString( "myString" ) );

        assertEquals( 4, tree.getTotalSize() );
    }

    @Test
    public void adding_property()
    {
        PropertyTree tree = new PropertyTree();

        tree.addString( "myProp", "myString" );
        tree.addProperty( "myProp", ValueFactory.newString( "myString" ) );

        assertEquals( 2, tree.getTotalSize() );
    }

    @Test
    public void getting_value()
    {
        PropertyTree tree = new PropertyTree();
        tree.addString( "myProp", "a" );
        tree.addString( "myProp", "b" );

        assertEquals( "a", tree.getProperty( "myProp", 0 ).getString() );
        assertEquals( "a", tree.getValue( "myProp", 0 ).asString() );
        assertEquals( "a", tree.getString( "myProp", 0 ) );
        assertEquals( "b", tree.getString( "myProp[1]" ) );
        assertIterableEquals( List.of( "a", "b" ), tree.getStrings( "myProp" ) );
    }

    @Test
    public void getting_set()
    {
        PropertyTree tree = new PropertyTree();
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
        PropertyTree tree = new PropertyTree();
        PropertySet set1 = tree.newSet();
        set1.setString( "myProp", "myValue" );
        tree.addProperty( "mySet", ValueFactory.newPropertySet( set1 ) );
        PropertySet set2 = tree.newSet();
        set1.setString( "myProp", "myValue" );
        tree.addProperty( "mySet", ValueFactory.newPropertySet( set2 ) );

        assertIterableEquals( List.of( set1, set2 ), tree.getSets( "mySet" ) );
        assertSame( set1, tree.getSets( "mySet" ).iterator().next() );
    }

}
