package com.enonic.xp.security.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UsernamePasswordAuthTokenTest
{
    @Test
    public void userName()
    {
        final UsernamePasswordAuthToken token = new UsernamePasswordAuthToken();
        token.setUsername( "user" );

        assertNull( token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }

    @Test
    public void userNameWithIdProvider()
    {
        final UsernamePasswordAuthToken token = new UsernamePasswordAuthToken();
        token.setUsername( "store\\user" );

        assertEquals( IdProviderKey.from( "store" ), token.getIdProvider() );
        assertEquals( "user", token.getUsername() );
    }
}
