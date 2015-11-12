package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class CreateUserHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testCreateUser()
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUser() );

        runFunction( "/site/test/createUser-test.js", "createUser" );
    }

    @Test
    public void testCreateUserNoEmail()
    {
        Mockito.when( securityService.createUser( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUserWithoutEmail() );

        runFunction( "/site/test/createUser-test.js", "createUserNoEmail" );
    }

    @Test(expected = ResourceProblemException.class)
    public void testCreateUserWithMissingArg()
    {
        runFunction( "/site/test/createUser-test.js", "createUserWithMissingArg" );
    }
}
