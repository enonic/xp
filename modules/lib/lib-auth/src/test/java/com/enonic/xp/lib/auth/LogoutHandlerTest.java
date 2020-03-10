package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutHandlerTest
    extends ScriptTestSupport
{
    private SimpleSession session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().removeAttribute( AuthenticationInfo.class );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();
        this.session.setAttribute( authInfo );

        assertTrue( ContextAccessor.current().getAuthInfo().isAuthenticated() );

        runScript( "/lib/xp/examples/auth/logout.js" );

        assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }

    @Test
    public void testLogout()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();
        this.session.setAttribute( authInfo );

        assertTrue( ContextAccessor.current().getAuthInfo().isAuthenticated() );

        runFunction( "/test/logout-test.js", "logout" );

        assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }

    @Test
    public void testLogoutWithoutSession()
    {
        assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );

        runFunction( "/test/logout-test.js", "alreadyLoggedOut" );

        assertFalse( ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }
}
