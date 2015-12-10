package com.enonic.xp.lib.context;

import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.testing.script.ScriptTestSupport2;

public class ContextScriptTest
    extends ScriptTestSupport2
{
    @Override
    protected void initialize()
    {
        super.initialize();

        final SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final User user = User.create().
            login( "su" ).
            displayName( "Super User" ).
            key( PrincipalKey.ofUser( UserStoreKey.system(), "su" ) ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( user ).
            principals( RoleKeys.ADMIN, RoleKeys.EVERYONE ).
            build();
        Mockito.when( securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
    }

    @Override
    public String getScriptTestFile()
    {
        return "/site/test/context-test.js";
    }
}
