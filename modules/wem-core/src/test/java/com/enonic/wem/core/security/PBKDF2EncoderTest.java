package com.enonic.wem.core.security;

import org.junit.Before;
import org.junit.Test;

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

        final String encodedPwd = encoder.encodePassword( "runar" );

        assertTrue( encoder.validate( "runar", encodedPwd ) );
        assertFalse( encoder.validate( "runa1", encodedPwd ) );
        assertFalse( encoder.validate( "runar ", encodedPwd ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_format()
        throws Exception
    {
        assertTrue( encoder.validate( "runar", this.encoder.getType() + ":fisk" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_type()
        throws Exception
    {
        assertTrue( encoder.validate( "runar", "fisk" + ":ost:balle" ) );
    }

}