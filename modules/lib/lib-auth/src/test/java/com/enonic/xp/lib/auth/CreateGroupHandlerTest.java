package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class CreateGroupHandlerTest
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
    public void testCreateGroup()
        throws Exception
    {
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );

        runTestFunction( "/test/createGroup-test.js", "createGroup" );
    }

    @Test
    public void testCreateGroupWithUnAuthenticated()
        throws Exception
    {

        runTestFunction( "/test/createGroup-test.js", "createGroupUnAuthenticated" );
    }
}
