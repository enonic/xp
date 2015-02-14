package com.enonic.xp.security.impl;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.security.impl.PBKDF2Encoder;

import static org.junit.Assert.*;

public class PBKDF2EncoderTest
{
    private PBKDF2Encoder encoder;

    @Before
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

    @Test(expected = IllegalArgumentException.class)
    public void unknown_format()
        throws Exception
    {
        assertTrue( encoder.validate( "fiskepudding", this.encoder.getType() + ":fisk" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_type()
        throws Exception
    {
        assertTrue( encoder.validate( "fiskepudding", "fisk" + ":ost:bolle" ) );
    }
}