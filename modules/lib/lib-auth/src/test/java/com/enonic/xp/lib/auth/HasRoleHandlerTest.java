package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class HasRoleHandlerTest
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
    public void testHasRoleById()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/site/test/hasRole-test.js", "hasRole" );
    }

    @Test
    public void testHasRoleByKey()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/site/test/hasRole-test.js", "hasRoleByKey" );
    }

    @Test
    public void testDoesNotHaveRole()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runFunction( "/site/test/hasRole-test.js", "doesNotHaveRole" );
    }
}
