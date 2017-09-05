package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetProfileHandlerTest
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
        Mockito.when( securityService.getUser( Mockito.any() ) ).
            thenReturn( Optional.of( TestDataFixtures.getTestUser() ) );

        runScript( "/site/lib/xp/examples/auth/getProfile.js" );
    }

    @Test
    public void testNoProfile()
    {
        Mockito.when( securityService.getUser( Mockito.any() ) ).
            thenReturn( Optional.of( TestDataFixtures.getTestUser2() ) );

        runFunction( "/site/test/getProfile-test.js", "noProfile" );
    }
}
