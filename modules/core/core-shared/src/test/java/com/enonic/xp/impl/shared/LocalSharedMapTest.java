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
}
