package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

class HasRoleHandlerTest
    extends ScriptTestSupport
{
    private Session session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.session = new SessionMock();
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    void testExamples()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals(
            PrincipalKey.ofRole( "system.admin.login" ) ).build();

        this.session.setAttribute( authInfo );

        runScript( "/lib/xp/examples/auth/hasRole.js" );
    }

    @Test
    void testHasRoleById()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/test/hasRole-test.js", "hasRole" );
    }

    @Test
    void testHasRoleByKey()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/test/hasRole-test.js", "hasRoleByKey" );
    }

    @Test
    void testDoesNotHaveRole()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/test/hasRole-test.js", "doesNotHaveRole" );
    }
}
