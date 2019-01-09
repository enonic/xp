package com.enonic.xp.lib.portal.current;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetCurrentUserStoreKeyHandlerTest
    extends ScriptTestSupport
{
    @Test
    public void testExample()
    {
        final IdProvider idProvider = TestDataFixtures.newIdProvider();
        this.portalRequest.setIdProvider( idProvider );

        runScript( "/site/lib/xp/examples/portal/getUserStoreKey.js" );
    }

    @Test
    public void noIdProviderConfig()
    {
        this.portalRequest.setRawRequest( Mockito.mock( HttpServletRequest.class ) );
        this.portalRequest.setIdProvider( null );
        runFunction( "/site/test/getUserStoreKey-test.js", "noUserStore" );
    }
}
