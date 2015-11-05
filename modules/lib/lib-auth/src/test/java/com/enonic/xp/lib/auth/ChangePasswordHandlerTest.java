package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class ChangePasswordHandlerTest
    extends ScriptTestSupport
{

    private SecurityService securityService;

    private SimpleSession session;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final AuthenticationInfo authInfo = HandlerTestHelper.createAuthenticationInfo();
        this.session.setAttribute( authInfo );

        runTestFunction( "/test/changePassword-test.js", "changePassword" );
    }
}
