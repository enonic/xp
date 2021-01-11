package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.eq;

public class ChangePasswordHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    private Session session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SessionMock();
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runScript( "/lib/xp/examples/auth/changePassword.js" );
        Mockito.verify( this.securityService ).setPassword( eq( authInfo.getUser().getKey() ), eq( "new-secret-password" ) );
    }

    @Test
    public void testChangePassword()
    {
        runFunction( "/test/changePassword-test.js", "changePassword" );

        Mockito.verify( this.securityService ).setPassword( eq( PrincipalKey.from( "user:myIdProvider:userId" ) ),
                                                            eq( "test-password-without-spaces" ) );
    }
}
