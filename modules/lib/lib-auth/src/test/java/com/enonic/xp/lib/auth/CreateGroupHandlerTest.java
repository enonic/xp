package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class CreateGroupHandlerTest
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
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );
        runScript( "/site/lib/xp/examples/auth/createGroup.js" );
    }

    @Test
    public void testCreateGroup()
    {
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );

        runFunction( "/site/test/createGroup-test.js", "createGroup" );
    }

    @Test
    public void testCreateGroupWithUnAuthenticated()
    {
        runFunction( "/site/test/createGroup-test.js", "createGroupUnAuthenticated" );
    }
}
