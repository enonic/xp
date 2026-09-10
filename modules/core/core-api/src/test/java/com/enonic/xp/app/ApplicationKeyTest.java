package com.enonic.xp.app;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationKeyTest
{
    @Test
    void testCreate()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }

    @Test
    void testEquals()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
        assertEquals( ApplicationKey.from( "myapplication" ).hashCode(), applicationKey.hashCode() );
    }

    @Test
    void testFromApplicationKey()
    {
        assertEquals( "myapplication", ApplicationKey.from( "myapplication" ).getName() );
    }

    @Test
    void testParseApplicationVersion()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ApplicationKey.class ).withNonnullFields( "name" ).verify();
    }

    @Test
    void reservedApplicationKeys()
    {
        assertTrue( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( ApplicationKey.from( "server" ) ) );
        assertTrue( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( ApplicationKey.from( "admin" ) ) );
        assertTrue( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( ApplicationKey.from( "xp" ) ) );
        assertTrue( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( ApplicationKey.from( "enonic" ) ) );
        assertTrue( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( ApplicationKey.from( "cms" ) ) );

        assertSame( ApplicationKey.SERVER, ApplicationKey.from( "server" ) );
        assertSame( ApplicationKey.ADMIN, ApplicationKey.from( "admin" ) );
        assertSame( ApplicationKey.XP, ApplicationKey.from( "xp" ) );
        assertSame( ApplicationKey.ENONIC, ApplicationKey.from( "enonic" ) );
        assertSame( ApplicationKey.CMS, ApplicationKey.from( "cms" ) );
    }
}
