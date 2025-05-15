package com.enonic.xp.session;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SessionKeyTest
{
    @Test
    public void testFrom()
    {
        final SessionKey key1 = SessionKey.from( "1" );
        final SessionKey key2 = SessionKey.from( "2" );

        assertNotEquals( key1, key2 );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( SessionKey.class ).withNonnullFields( "value" ).verify();
    }
}
