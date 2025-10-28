package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

class MultiRepoConnectTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        final Context context = ContextBuilder.create().
            authInfo( AuthenticationInfo.create().
                user( User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build() ).
                principals( RoleKeys.ADMIN ).
                build() ).
            build();

        context.runWith( () -> runScript( "/lib/xp/examples/node/multiRepoConnect.js" ) );
    }
}
