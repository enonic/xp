package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VerifiedEmailAuthTokenTest
{
    @Test
    void userName()
    {
        final VerifiedEmailAuthToken token = new VerifiedEmailAuthToken();
        token.setEmail( "user@domain.com" );

        assertNull( token.getIdProvider() );
        assertEquals( "user@domain.com", token.getEmail() );
    }
}
