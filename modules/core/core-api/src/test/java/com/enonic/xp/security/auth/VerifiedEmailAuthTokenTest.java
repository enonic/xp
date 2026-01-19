package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerifiedEmailAuthTokenTest
{
    @Test
    void userName()
    {
        final VerifiedEmailAuthToken token = new VerifiedEmailAuthToken( IdProviderKey.system(), "user@domain.com" );
        assertEquals( IdProviderKey.system(), token.getIdProvider() );
        assertEquals( "user@domain.com", token.getEmail() );
    }
}
