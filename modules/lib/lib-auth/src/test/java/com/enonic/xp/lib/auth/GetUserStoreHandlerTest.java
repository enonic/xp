package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetUserStoreHandlerTest
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
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn(
            TestDataFixtures.getTestUserStore() );
        runScript( "/site/lib/xp/examples/auth/getUserStore.js" );
    }

    @Test
    public void testGetUserStore()
    {
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn(
            TestDataFixtures.getTestUserStore() );

        runFunction( "/site/test/getUserStore-test.js", "getUserStore" );
    }

    @Test
    public void testNonExistingUserStore()
    {
        Mockito.when( securityService.getUserStore( UserStoreKey.from( "myUserStore" ) ) ).thenReturn( null );

        runFunction( "/site/test/getUserStore-test.js", "getNonExistingUserStore" );
    }
}
