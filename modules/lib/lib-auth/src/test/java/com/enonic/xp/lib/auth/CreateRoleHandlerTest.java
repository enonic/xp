package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class CreateRoleHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        Mockito.when( securityService.createRole( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestRole() );
        runScript( "/site/lib/xp/examples/auth/createRole.js" );
    }

    @Test
    public void testCreateRole()
    {
        Mockito.when( securityService.createRole( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestRole() );

        runFunction( "/site/test/createRole-test.js", "createRole" );
    }

    @Test
    public void testCreateRoleWithUnAuthenticated()
    {
        runFunction( "/site/test/createRole-test.js", "createRoleUnAuthenticated" );
    }
}
