package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.testing.ScriptTestSupport;

class GetCurrentIdProviderKeyHandlerTest
    extends ScriptTestSupport
{
    @Test
    void testExample()
    {
        final IdProvider idProvider = TestDataFixtures.newIdProvider();
        this.portalRequest.setIdProvider( idProvider );

        runScript( "/lib/xp/examples/portal/getIdProviderKey.js" );
    }

    @Test
    void noIdProviderConfig()
    {
        this.portalRequest.setRawRequest( Mockito.mock( HttpServletRequest.class ) );
        this.portalRequest.setIdProvider( null );
        runFunction( "/test/getIdProviderKey-test.js", "noIdProvider" );
    }
}
