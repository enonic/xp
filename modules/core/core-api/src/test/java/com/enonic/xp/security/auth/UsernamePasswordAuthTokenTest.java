package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernamePasswordAuthTokenTest
{
    @Test
    void userName()
    {
        final UsernamePasswordAuthToken token = new UsernamePasswordAuthToken( IdProviderKey.system(), "user" );
        assertEquals( IdProviderKey.system(), token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }

}
