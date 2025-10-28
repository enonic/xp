package com.enonic.xp.resource;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceKeysTest
{
    private static final String RESOURCE_URI_1 = "myapplication-1.0.0:";

    private static final String RESOURCE_URI_2 = "myapplication-1.0.0:/a/b.txt";

    private static final String RESOURCE_URI_3 = "myapplication-1.0.0:/a/c.txt";


    private static final ArrayList<ResourceKey> list = new ArrayList();

    @BeforeAll
    public static void initResourceKeys()
    {
        ResourceKeysTest.list.add( ResourceKey.from( RESOURCE_URI_1 ) );
        ResourceKeysTest.list.add( ResourceKey.from( RESOURCE_URI_2 ) );
        ResourceKeysTest.list.add( ResourceKey.from( RESOURCE_URI_3 ) );
    }

    @Test
    void empty()
    {
        ResourceKeys resourceKeys = ResourceKeys.empty();

        assertEquals( 0, resourceKeys.getSize() );
    }

    @Test
    void fromArray()
    {
        ResourceKeys resourceKeys =
            ResourceKeys.from( ResourceKeysTest.list.get( 0 ), ResourceKeysTest.list.get( 1 ), ResourceKeysTest.list.get( 2 ) );

        assertEquals( 3, resourceKeys.getSize() );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 0 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 1 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromIterable()
    {
        ResourceKeys resourceKeys = ResourceKeys.from( ResourceKeysTest.list );

        assertEquals( 3, resourceKeys.getSize() );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 0 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 1 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromCollection()
    {
        ResourceKeys resourceKeys = ResourceKeys.from( ResourceKeysTest.list );

        assertEquals( 3, resourceKeys.getSize() );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 0 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 1 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromStringArray()
    {
        ResourceKeys resourceKeys = ResourceKeys.from( RESOURCE_URI_1, RESOURCE_URI_2, RESOURCE_URI_3 );

        assertEquals( 3, resourceKeys.getSize() );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 0 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 1 ) ) );
        assertTrue( resourceKeys.contains( ResourceKeysTest.list.get( 2 ) ) );
    }
}
