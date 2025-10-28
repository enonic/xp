package com.enonic.xp.core.impl.security;

import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PBKDF2EncoderTest
{
    private PBKDF2Encoder encoder;

    @BeforeEach
    void setUp()
    {
        this.encoder = new PBKDF2Encoder( new SecureRandom() );
    }

    @Test
    void encode_validate()
    {
        final String encodedPwd = encoder.encodePassword( "fiskepudding" );

        assertTrue( encoder.validate( "fiskepudding", encodedPwd ) );
        assertFalse( encoder.validate( "Fiskepudding", encodedPwd ) );
        assertFalse( encoder.validate( "fiskepudding1", encodedPwd ) );
        assertFalse( encoder.validate( "fiskepudding ", encodedPwd ) );
    }

    @Test
    void unknown_format()
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( encoder.validate( "fiskepudding", this.encoder.getType() + ":fisk" ) ));
    }

    @Test
    void unknown_type()
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( encoder.validate( "fiskepudding", "fisk" + ":ost:bolle" ) ));
    }
}
