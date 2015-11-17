package com.enonic.xp.security.auth;

import org.junit.Test;

import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;

public class VerifiedUsernameAuthTokenTest
{
    @Test
    public void userName()
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
        token.setUsername( "user" );

        assertNull( token.getUserStore() );
        assertEquals( "user", token.getUsername() );
    }

    @Test
    public void userNameWithUserStore()
    {
        final UsernamePasswordAuthToken token = new UsernamePasswordAuthToken();
        token.setUsername( "store\\user" );

        assertEquals( UserStoreKey.from( "store" ), token.getUserStore() );
        assertEquals( "user", token.getUsername() );
    }
}
