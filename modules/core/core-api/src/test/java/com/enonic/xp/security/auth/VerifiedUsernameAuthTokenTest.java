package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerifiedUsernameAuthTokenTest
{
    @Test
    void userName()
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken( IdProviderKey.system(), "user" );
        assertEquals( IdProviderKey.system(), token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }

}
