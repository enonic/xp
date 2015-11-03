package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class CreateUserHandlerTest
    extends ScriptTestSupport
{

    private SimpleSession session;

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );

        this.session.setAttribute( HandlerTestHelper.createAuthenticationInfo() );
    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( HandlerTestHelper.getTestUser() );

        runTestFunction( "/test/createUser-test.js", "createUser" );
    }

    @Test
    public void testCreateUserNoEmail()
        throws Exception
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( HandlerTestHelper.getTestUserWithouEmail() );

        runTestFunction( "/test/createUser-test.js", "createUserNoEmail" );
    }

    @Test(expected = ResourceProblemException.class)
    public void testCreateUserWithMissingArg()
        throws Exception
    {
        runTestFunction( "/test/createUser-test.js", "createUserWithMissingArg" );
    }
}
