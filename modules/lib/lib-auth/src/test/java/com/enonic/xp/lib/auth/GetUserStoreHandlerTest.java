package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.security.UserStore;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetUserStoreHandlerTest
    extends ScriptTestSupport
{
    @Test
    public void testExample()
    {
        final UserStore userStore = TestDataFixtures.getTestUserStore();
        this.portalRequest.setUserStore( userStore );

        runScript( "/site/lib/xp/examples/portal/getUserStore.js" );
    }


    @Test
    public void noAuthConfig()
    {
        this.portalRequest.setUserStore( null );
        runFunction( "/site/test/getUserStore-test.js", "noUserStore" );
    }
}
