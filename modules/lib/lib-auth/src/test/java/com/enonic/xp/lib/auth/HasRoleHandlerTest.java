package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class HasRoleHandlerTest
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
    public void testHasRoleById()
        throws Exception
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runTestFunction( "/test/hasRole-test.js", "hasRole" );
    }

    @Test
    public void testHasRoleByKey()
        throws Exception
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( PrincipalKey.ofRole( "my-role" ) ).build();

        this.session.setAttribute( authInfo );

        runTestFunction( "/test/hasRole-test.js", "hasRoleByKey" );
    }

    @Test
    public void testDoesNotHaveRole()
        throws Exception
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runTestFunction( "/test/hasRole-test.js", "doesNotHaveRole" );
    }
}
