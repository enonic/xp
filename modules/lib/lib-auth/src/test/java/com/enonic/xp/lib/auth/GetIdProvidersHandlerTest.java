package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

class GetIdProvidersHandlerTest
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
        Mockito.when( securityService.getIdProviders() ).thenReturn( IdProviders.from( TestDataFixtures.getTestIdProvider() ) );
        runScript( "/lib/xp/examples/auth/getIdProviders.js" );
    }

    @Test
    void testGetIdProviders()
    {
        Mockito.when( securityService.getIdProviders() ).thenReturn( IdProviders.from( TestDataFixtures.getTestIdProvider() ) );

        runFunction( "/test/getIdProviders-test.js", "getIdProviders" );
    }

    @Test
    void testGetIdProvidersEmpty()
    {
        Mockito.when( securityService.getIdProviders() ).thenReturn( IdProviders.empty() );

        runFunction( "/test/getIdProviders-test.js", "getIdProvidersEmpty" );
    }
}
