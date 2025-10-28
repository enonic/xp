package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VerifiedUsernameAuthTokenTest
{
    @Test
    void userName()
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
        token.setUsername( "user" );

        assertNull( token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }

    @Test
    void userNameWithIdProvider()
    {
        final UsernamePasswordAuthToken token = new UsernamePasswordAuthToken();
        token.setUsername( "store\\user" );

        assertEquals( IdProviderKey.from( "store" ), token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }
}
