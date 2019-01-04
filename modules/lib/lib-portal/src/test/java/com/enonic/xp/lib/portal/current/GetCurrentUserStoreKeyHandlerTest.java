package com.enonic.xp.lib.portal.current;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetCurrentUserStoreKeyHandlerTest
    extends ScriptTestSupport
{
    @Test
    public void testExample()
    {
        final UserStore userStore = TestDataFixtures.newUserStore();
        this.portalRequest.setUserStore( userStore );

        runScript( "/site/lib/xp/examples/portal/getUserStoreKey.js" );
    }

    @Test
    public void noIdProviderConfig()
    {
        this.portalRequest.setRawRequest( Mockito.mock( HttpServletRequest.class ) );
        this.portalRequest.setUserStore( null );
        runFunction( "/site/test/getUserStoreKey-test.js", "noUserStore" );
    }
}
