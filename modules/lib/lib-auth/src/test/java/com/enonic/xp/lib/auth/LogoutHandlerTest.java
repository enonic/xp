package com.enonic.xp.lib.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class LogoutHandlerTest
    extends OldScriptTestSupport
{
    private SimpleSession session;

    @Before
    public void setup()
    {
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testLogout()
        throws Exception
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();
        this.session.setAttribute( authInfo );

        Assert.assertTrue( ContextAccessor.current().getAuthInfo().isAuthenticated() );

        runTestFunction( "/test/logout-test.js", "logout" );

        Assert.assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }

    @Test
    public void testLogoutWithoutSession()
        throws Exception
    {
        Assert.assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );

        runTestFunction( "/test/logout-test.js", "alreadyLoggedOut" );

        Assert.assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }
}
