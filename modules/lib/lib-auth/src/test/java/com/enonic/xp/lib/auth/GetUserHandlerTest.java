package com.enonic.xp.lib.auth;

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

    @Override
    public void initialize()
    {
        super.initialize();
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testGetUserAuthenticated()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/site/test/getUser-test.js", "getUserAuthenticated" );
    }

    @Test
    public void testGetUserNotAuthenticated()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        this.session.setAttribute( authInfo );

        runFunction( "/site/test/getUser-test.js", "getUserNotAuthenticated" );
    }
}
