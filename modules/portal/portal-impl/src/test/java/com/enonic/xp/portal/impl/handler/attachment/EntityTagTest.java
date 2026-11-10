package com.enonic.xp.portal.impl.handler.attachment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityTagTest
{
    private final EntityTag tag = new EntityTag( "abc123" );

    @Test
    void headerValueIsQuoted()
    {
        assertEquals( "\"abc123\"", tag.toHeaderValue() );
    }

    @Test
    void ifNoneMatch()
    {
        assertTrue( tag.matchesIfNoneMatch( "\"abc123\"" ) );
        assertTrue( tag.matchesIfNoneMatch( "W/\"abc123\"" ) );
        assertTrue( tag.matchesIfNoneMatch( "\"x\", \"abc123\"" ) );
        assertTrue( tag.matchesIfNoneMatch( " \"x\" ,W/\"abc123\" " ) );
        assertFalse( tag.matchesIfNoneMatch( "\"x\"" ) );
        assertFalse( tag.matchesIfNoneMatch( "*" ) );
        assertFalse( tag.matchesIfNoneMatch( "abc123" ) );
        assertFalse( tag.matchesIfNoneMatch( "" ) );
    }

    @Test
    void ifRange()
    {
        assertTrue( tag.matchesIfRange( "\"abc123\"" ) );
        assertTrue( tag.matchesIfRange( " \"abc123\" " ) );
        assertFalse( tag.matchesIfRange( "W/\"abc123\"" ) );
        assertFalse( tag.matchesIfRange( "*" ) );
        assertFalse( tag.matchesIfRange( "\"x\", \"abc123\"" ) );
        assertFalse( tag.matchesIfRange( "Wed, 21 Oct 2015 07:28:00 GMT" ) );
    }
}
