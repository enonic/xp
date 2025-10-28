package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

class CreateRoleHandlerTest
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
    void testExamples()
    {
        Mockito.when( securityService.createRole( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestRole() );
        runScript( "/lib/xp/examples/auth/createRole.js" );
    }

    @Test
    void testCreateRole()
    {
        Mockito.when( securityService.createRole( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestRole() );

        runFunction( "/test/createRole-test.js", "createRole" );
    }
}
