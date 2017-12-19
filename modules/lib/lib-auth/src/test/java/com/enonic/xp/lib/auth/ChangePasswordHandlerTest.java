package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Matchers.eq;

public class ChangePasswordHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    private SimpleSession session;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runScript( "/site/lib/xp/examples/auth/changePassword.js" );
        Mockito.verify( this.securityService ).setPassword( eq( authInfo.getUser().getKey() ), eq( "new-secret-password" ) );
    }

    @Test
    public void testChangePassword()
    {
        runFunction( "/site/test/changePassword-test.js", "changePassword" );

        Mockito.verify( this.securityService ).setPassword( eq( PrincipalKey.from( "user:myUserStore:userId" ) ),
                                                            eq( "test-password-without-spaces" ) );
    }
}
