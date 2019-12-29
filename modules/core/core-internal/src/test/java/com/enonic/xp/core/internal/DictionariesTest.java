package com.enonic.xp.core.internal;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DictionariesTest
{
    @Test
    void ofEmpty()
    {
        final Dictionary<String, String> dictionary = Dictionaries.of();

        assertAll( () -> assertTrue( dictionary.isEmpty() ), () -> assertEquals( 0, dictionary.size() ),
                   () -> assertNull( dictionary.get( "a" ) ),
                   () -> assertEquals( Collections.emptyList(), Collections.list( dictionary.keys() ) ),
                   () -> assertEquals( Collections.emptyList(), Collections.list( dictionary.elements() ) ) );
    }

    @Test
    void ofSingle()
    {
        final Dictionary<String, String> dictionary = Dictionaries.of( "a", "b" );

        assertAll( () -> assertFalse( dictionary.isEmpty() ), () -> assertEquals( 1, dictionary.size() ),
                   () -> assertEquals( "b", dictionary.get( "a" ) ), () -> assertNull( dictionary.get( "b" ) ),
                   () -> assertEquals( Collections.singletonList( "a" ), Collections.list( dictionary.keys() ) ),
                   () -> assertEquals( Collections.singletonList( "b" ), Collections.list( dictionary.elements() ) ) );
    }

    @Test
    void copyOf()
    {
        final Dictionary<String, String> dictionary = Dictionaries.copyOf( Map.of( "a", "b", "c", "d" ) );

        assertAll( () -> assertFalse( dictionary.isEmpty() ), () -> assertEquals( 2, dictionary.size() ),
                   () -> assertEquals( "b", dictionary.get( "a" ) ), () -> assertNull( dictionary.get( "b" ) ) );
    }

    @Test
    void unmodifiable()
    {
        final HashMap<String, String> origin = new HashMap<>( Map.of( "a", "b" ) );
        final Dictionary<String, String> dictionary = Dictionaries.copyOf( origin );

        assertAll( () -> assertThrows( UnsupportedOperationException.class, () -> dictionary.put( "c", "d" ) ),
                   () -> assertThrows( UnsupportedOperationException.class, () -> dictionary.remove( "a" ) ) );
    }

    @Test
    void defensiveCopy()
    {
        final HashMap<String, String> origin = new HashMap<>();
        final Dictionary<String, String> dictionary = Dictionaries.copyOf( origin );

        origin.put( "a", "b" );

        assertEquals( "b", origin.get( "a" ) );
        assertNull( dictionary.get( "a" ) );
    }
}