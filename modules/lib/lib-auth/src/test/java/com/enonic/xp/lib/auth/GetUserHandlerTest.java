package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetUserHandlerTest
    extends ScriptTestSupport
{
    private SimpleSession session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runScript( "/lib/xp/examples/auth/getUser.js" );
    }

    @Test
    public void testGetUserAuthenticated()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/test/getUser-test.js", "getUserAuthenticated" );
    }

    @Test
    public void testGetUserNotAuthenticated()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        this.session.setAttribute( authInfo );

        runFunction( "/test/getUser-test.js", "getUserNotAuthenticated" );
    }
}
