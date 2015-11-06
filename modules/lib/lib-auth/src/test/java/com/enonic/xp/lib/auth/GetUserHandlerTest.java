package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetUserHandlerTest
    extends ScriptTestSupport
{
    private SimpleSession session;

    @Before
    public void setup()
    {
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testGetUserAuthenticated()
        throws Exception
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runTestFunction( "/test/getUser-test.js", "getUserAuthenticated" );
    }

    @Test
    public void testGetUserNotAuthenticated()
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        this.session.setAttribute( authInfo );

        runTestFunction( "/test/getUser-test.js", "getUserNotAuthenticated" );
    }
}
