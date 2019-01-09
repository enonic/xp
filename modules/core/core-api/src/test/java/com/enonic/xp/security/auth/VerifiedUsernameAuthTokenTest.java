package com.enonic.xp.security.auth;

import org.junit.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.Assert.*;

public class VerifiedUsernameAuthTokenTest
{
    @Test
    public void userName()
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
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
