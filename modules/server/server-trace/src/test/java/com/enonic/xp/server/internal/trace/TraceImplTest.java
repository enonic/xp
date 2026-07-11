package com.enonic.xp.server.internal.trace;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceImplTest
{
    private TraceImpl trace;

    private TraceLocation location;

    @BeforeEach
    void setup()
    {
        this.location = Mockito.mock( TraceLocation.class );
        this.trace = new TraceImpl( "name", "parentId", this.location );
    }

    @Test
    void testGetters()
    {
        assertNotNull( this.trace.getId() );
        assertEquals( "parentId", this.trace.getParentId() );
        assertEquals( "name", this.trace.getName() );
        assertEquals( Duration.ZERO, this.trace.getDuration() );
        assertSame( this.location, this.trace.getLocation() );
        assertNull( this.trace.getStartTime() );
        assertNull( this.trace.getEndTime() );
        assertTrue( this.trace.inProgress() );
    }

    @Test
    void testStartEnd()
    {
        this.trace.start();
        assertNotNull( this.trace.getStartTime() );
        assertNull( this.trace.getEndTime() );
        assertTrue( this.trace.getDuration().toMillis() >= 0 );
        assertTrue( this.trace.inProgress() );

        this.trace.end();
        assertNotNull( this.trace.getStartTime() );
        assertNotNull( this.trace.getEndTime() );
        assertTrue( this.trace.getDuration().toMillis() >= 0 );
        assertFalse( this.trace.inProgress() );
    }

    @Test
    void putNormalizesValues()
    {
        // pass-through types
        this.trace.put( "string", "value" );
        this.trace.put( "boolean", true );
        this.trace.put( "long", 42L );
        this.trace.put( "double", 1.5d );
        assertEquals( "value", this.trace.get( "string" ) );
        assertEquals( Boolean.TRUE, this.trace.get( "boolean" ) );
        assertEquals( 42L, this.trace.get( "long" ) );
        assertEquals( 1.5d, this.trace.get( "double" ) );

        // numeric widening
        this.trace.put( "int", 7 );
        this.trace.put( "float", 2.5f );
        assertEquals( 7L, this.trace.get( "int" ) );
        assertEquals( 2.5d, this.trace.get( "float" ) );

        // iterables become immutable lists of strings
        this.trace.put( "list", java.util.List.of( "a", 1, true ) );
        assertEquals( java.util.List.of( "a", "1", "true" ), this.trace.get( "list" ) );

        // arbitrary objects are converted eagerly to String
        this.trace.put( "object", new java.math.BigInteger( "9999999999999999999999" ) );
        assertEquals( "9999999999999999999999", this.trace.get( "object" ) );

        this.trace.put( "uri", java.net.URI.create( "repo:branch" ) );
        assertEquals( "repo:branch", this.trace.get( "uri" ) );
    }

    @Test
    void putNullValueRemoves()
    {
        this.trace.put( "key", "value" );
        assertEquals( "value", this.trace.get( "key" ) );

        // null values are treated as removals (the map must stay ConcurrentHashMap-safe without throwing NPE)
        this.trace.put( "key", null );
        assertNull( this.trace.get( "key" ) );
        assertFalse( this.trace.containsKey( "key" ) );

        this.trace.put( "absent", null );
        assertFalse( this.trace.containsKey( "absent" ) );
    }
}
