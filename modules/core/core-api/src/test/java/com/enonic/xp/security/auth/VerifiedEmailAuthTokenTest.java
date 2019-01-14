package com.enonic.xp.security.auth;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerifiedEmailAuthTokenTest
{
    @Test
    public void userName()
    {
        final VerifiedEmailAuthToken token = new VerifiedEmailAuthToken();
        token.setEmail( "user@domain.com" );

        assertNull( token.getIdProvider() );
        assertEquals( "user@domain.com", token.getEmail() );
    }
}
