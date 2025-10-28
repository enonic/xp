package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.testing.ScriptTestSupport;

class GetIdProviderConfigHandlerTest
    extends ScriptTestSupport
{
    @Test
    void testExample()
    {
        final IdProvider idProvider = TestDataFixtures.getTestIdProvider();
        this.portalRequest.setIdProvider( idProvider );

        runScript( "/lib/xp/examples/auth/getIdProviderConfig.js" );
    }


    @Test
    void noIdProviderConfig()
    {
        this.portalRequest.setIdProvider( null );
        runFunction( "/test/getIdProviderConfig-test.js", "noIdProvider" );
    }
}
