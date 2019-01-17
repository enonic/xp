package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetIdProviderConfigHandlerTest
    extends ScriptTestSupport
{
    @Test
    public void testExample()
    {
        final IdProvider idProvider = TestDataFixtures.getTestIdProvider();
        this.portalRequest.setIdProvider( idProvider );

        runScript( "/site/lib/xp/examples/auth/getIdProviderConfig.js" );
    }


    @Test
    public void noIdProviderConfig()
    {
        this.portalRequest.setIdProvider( null );
        runFunction( "/site/test/getIdProviderConfig-test.js", "noIdProvider" );
    }
}
