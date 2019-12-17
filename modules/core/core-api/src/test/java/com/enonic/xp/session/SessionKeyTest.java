package com.enonic.xp.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionKeyTest
{
    @Test
    public void testFrom()
    {
        final SessionKey key1 = SessionKey.from( "1" );
        final SessionKey key2 = SessionKey.from( "2" );

        assertEquals( key1, key1 );
        assertEquals( key2, key2 );
        assertTrue( !key1.equals( key2 ) );

        assertEquals( key1.hashCode(), key1.hashCode() );
        assertFalse( key1.hashCode() == key2.hashCode() );

    }

    @Test
    public void testGenerate()
    {
        final SessionKey key1 = SessionKey.generate();
        final SessionKey key2 = SessionKey.generate();

        assertEquals( key1, key1 );
        assertEquals( key2, key2 );
        assertTrue( !key1.equals( key2 ) );

        assertEquals( key1.hashCode(), key1.hashCode() );
        assertFalse( key1.hashCode() == key2.hashCode() );
    }
}
