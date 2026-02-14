package com.enonic.xp.impl.shared;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalSharedMapTest
{
    @Test
    void ttl()
    {
        LocalSharedMap.clock = Clock.fixed( Instant.ofEpochSecond( 0 ), ZoneOffset.UTC );
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();
        map.set( "key", "value", 1 );

        assertEquals( "value", map.get( "key" ) );
        LocalSharedMap.clock = Clock.fixed( Instant.ofEpochSecond( 1 ), ZoneOffset.UTC );
        assertNull( map.get( "key" ) );

        map.set( "key", "value", 1 );
        assertEquals( "value", map.get( "key" ) );

        LocalSharedMap.clock = Clock.fixed( Instant.ofEpochSecond( 2 ), ZoneOffset.UTC );
        assertNull( map.get( "key" ) );
        assertEquals( 1, map.cleanUp() );
    }

    @Test
    void enormous_ttl()
    {
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();
        map.set( "key", "value", Integer.MAX_VALUE );

        assertEquals( "value", map.get( "key" ) );
    }

    @Test
    void set_then_get_then_delete()
    {
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();
        map.set( "key", "value" );

        assertEquals( "value", map.get( "key" ) );

        map.delete( "key" );

        assertNull( map.get( "key" ) );
    }

    @Test
    void set_null()
    {
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();
        map.set( "key", "value" );

        assertEquals( "value", map.get( "key" ) );

        map.set( "key", null );

        assertNull( map.get( "key" ) );
    }

    @Test
    void modify()
    {
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();

        map.modify( "key", v -> v + "0" );
        assertEquals( "null0", map.get( "key" ) );

        map.modify( "key", v -> v + "1" );
        assertEquals( "null01", map.get( "key" ) );

        map.modify( "key", v -> null );
        assertNull( map.get( "key" ) );
    }

    @Test
    void removeAll()
    {
        final LocalSharedMap<String, Integer> map = new LocalSharedMap<>();
        map.set( "key1", 1 );
        map.set( "key2", 10 );
        map.set( "key3", 3 );
        map.set( "key4", 20 );

        map.removeAll( entry -> entry.getValue() > 5 );

        assertEquals( 1, map.get( "key1" ) );
        assertNull( map.get( "key2" ) );
        assertEquals( 3, map.get( "key3" ) );
        assertNull( map.get( "key4" ) );
    }

    @Test
    void removeAll_byKey()
    {
        final LocalSharedMap<String, String> map = new LocalSharedMap<>();
        map.set( "apple", "red" );
        map.set( "banana", "yellow" );
        map.set( "avocado", "green" );

        map.removeAll( entry -> entry.getKey().startsWith( "a" ) );

        assertNull( map.get( "apple" ) );
        assertEquals( "yellow", map.get( "banana" ) );
        assertNull( map.get( "avocado" ) );
    }

    @Test
    void removeAll_withExpiredEntries()
    {
        LocalSharedMap.clock = Clock.fixed( Instant.ofEpochSecond( 0 ), ZoneOffset.UTC );
        final LocalSharedMap<String, Integer> map = new LocalSharedMap<>();
        map.set( "key1", 1, 1 ); // Will expire at second 1
        map.set( "key2", 10 );
        map.set( "key3", 3 );

        // Move time forward so key1 expires
        LocalSharedMap.clock = Clock.fixed( Instant.ofEpochSecond( 2 ), ZoneOffset.UTC );

        // Remove all entries with value > 5 - key1 is expired so it should not be considered
        map.removeAll( entry -> entry.getValue() > 5 );

        // key1 expired, so should be null
        assertNull( map.get( "key1" ) );
        // key2 matched predicate and was removed
        assertNull( map.get( "key2" ) );
        // key3 did not match predicate
        assertEquals( 3, map.get( "key3" ) );

        // Expired entries are cleaned up by removeAll, so cleanUp should return 0
        assertEquals( 0, map.cleanUp() );
    }
}
