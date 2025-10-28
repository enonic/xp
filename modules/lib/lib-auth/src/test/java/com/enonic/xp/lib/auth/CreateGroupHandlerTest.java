package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

class CreateGroupHandlerTest
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
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );
        runScript( "/lib/xp/examples/auth/createGroup.js" );
    }

    @Test
    void testCreateGroup()
    {
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );

        runFunction( "/test/createGroup-test.js", "createGroup" );
    }
}
