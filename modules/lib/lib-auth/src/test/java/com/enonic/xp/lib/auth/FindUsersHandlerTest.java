package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserQuery;
import com.enonic.xp.security.UserQueryResult;
import com.enonic.xp.testing.ScriptTestSupport;

class FindUsersHandlerTest
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

        final UserQueryResult result = UserQueryResult.create().
            addUser( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();

        Mockito.when( securityService.query( Mockito.any( UserQuery.class ) ) ).thenReturn( result );

        runScript( "/lib/xp/examples/auth/findUsers.js" );
    }
}
