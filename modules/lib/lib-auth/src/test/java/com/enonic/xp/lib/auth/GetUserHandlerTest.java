package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

class GetUserHandlerTest
    extends ScriptTestSupport
{
    private Session session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.session = new SessionMock();
        ContextAccessor.current().getLocalScope().removeAttribute( AuthenticationInfo.class );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    void testExamples()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runScript( "/lib/xp/examples/auth/getUser.js" );
    }

    @Test
    void testGetUserAuthenticated()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/test/getUser-test.js", "getUserAuthenticated" );
    }

    @Test
    void testGetUserNotAuthenticated()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        this.session.setAttribute( authInfo );

        runFunction( "/test/getUser-test.js", "getUserNotAuthenticated" );
    }
}
