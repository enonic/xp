package com.enonic.xp.core.impl.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PBKDF2EncoderTest
{
    private PBKDF2Encoder encoder;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.encoder = new PBKDF2Encoder();
    }

    @Test
    public void encode_validate()
        throws Exception
    {
        final String encodedPwd = encoder.encodePassword( "fiskepudding" );

        assertTrue( encoder.validate( "fiskepudding", encodedPwd ) );
        assertFalse( encoder.validate( "Fiskepudding", encodedPwd ) );
        assertFalse( encoder.validate( "fiskepudding1", encodedPwd ) );
        assertFalse( encoder.validate( "fiskepudding ", encodedPwd ) );
    }

    @Test
    public void unknown_format()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( encoder.validate( "fiskepudding", this.encoder.getType() + ":fisk" ) ));
    }

    @Test
    public void unknown_type()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( encoder.validate( "fiskepudding", "fisk" + ":ost:bolle" ) ));
    }
}
