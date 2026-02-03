package com.enonic.xp.core.impl.security;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PHCParserTest
{
    @Test
    void parse_basic()
    {
        final String phcString = "$pbkdf2-sha512$i=210000,l=64$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertEquals( "pbkdf2-sha512", data.id() );
        assertNull( data.version() );
        assertEquals( "210000", data.params().get( "i" ) );
        assertEquals( "64", data.params().get( "l" ) );
        assertArrayEquals( Base64.getDecoder().decode( "c2FsdA" ), data.salt() );
        assertArrayEquals( Base64.getDecoder().decode( "aGFzaA" ), data.hash() );
    }

    @Test
    void parse_with_version()
    {
        final String phcString = "$pbkdf2-sha512$v=1$i=210000,l=64$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertEquals( "pbkdf2-sha512", data.id() );
        assertEquals( 1, data.version() );
        assertEquals( "210000", data.params().get( "i" ) );
        assertEquals( "64", data.params().get( "l" ) );
    }

    @Test
    void parse_no_params()
    {
        final String phcString = "$argon2id$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertEquals( "argon2id", data.id() );
        assertNull( data.version() );
        assertTrue( data.params().isEmpty() );
    }

    @Test
    void parse_multiple_params()
    {
        final String phcString = "$argon2id$m=65536,t=3,p=4$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertEquals( "65536", data.params().get( "m" ) );
        assertEquals( "3", data.params().get( "t" ) );
        assertEquals( "4", data.params().get( "p" ) );
    }

    @Test
    void paramInt()
    {
        final String phcString = "$pbkdf2-sha512$i=210000,l=64$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertEquals( 210000, data.paramInt( "i" ) );
        assertEquals( 64, data.paramInt( "l" ) );
    }

    @Test
    void paramInt_missing()
    {
        final String phcString = "$pbkdf2-sha512$i=210000$c2FsdA$aGFzaA";

        final PHCParser.PHCData data = PHCParser.parse( phcString );

        assertThrows( NullPointerException.class, () -> data.paramInt( "l" ) );
    }

    @Test
    void parse_invalid_format_no_dollar()
    {
        assertThrows( IllegalArgumentException.class, () -> PHCParser.parse( "invalid" ) );
    }

    @Test
    void parse_argon2_policy()
    {
        final String phcString = "$argon2id$v=19$m=65536,t=3,p=1,slen=16,hlen=32$c2FsdA$aGFzaA";
        final PHCParser.PHCData data = PHCParser.parse( phcString );
        assertEquals( 19, data.version() );
        assertEquals( "65536", data.params().get( "m" ) );
        assertEquals( "3", data.params().get( "t" ) );
        assertEquals( "1", data.params().get( "p" ) );
        assertEquals( "16", data.params().get( "slen" ) );
        assertEquals( "32", data.params().get( "hlen" ) );
    }

    @Test
    void parse_pbkdf2_policy()
    {
        final String phcString = "$pbkdf2-sha512$i=210000,l=64,slen=16";
        final PHCParser.PHCData data = PHCParser.parse( phcString );
        assertEquals( "pbkdf2-sha512", data.id() );
        assertEquals( 210000, data.paramInt( "i" ) );
        assertEquals( 64, data.paramInt( "l" ) );
        assertEquals( 16, data.paramInt( "slen" ) );
    }
}
