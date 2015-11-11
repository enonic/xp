package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class CreateUserHandlerTest
    extends OldScriptTestSupport
{

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUser() );

        runTestFunction( "/test/createUser-test.js", "createUser" );
    }

    @Test
    public void testCreateUserNoEmail()
        throws Exception
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUserWithoutEmail() );

        runTestFunction( "/test/createUser-test.js", "createUserNoEmail" );
    }

    @Test(expected = ResourceProblemException.class)
    public void testCreateUserWithMissingArg()
        throws Exception
    {
        runTestFunction( "/test/createUser-test.js", "createUserWithMissingArg" );
    }
}
