package com.enonic.xp.lib.context;

import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.testing.ScriptRunnerSupport;

public class ContextScriptTest
    extends ScriptRunnerSupport
{
    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        final SecurityService securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, securityService );

        final User user = User.create().
            login( PrincipalKey.ofSuperUser().getId() ).
            displayName( "Super User" ).
            key( PrincipalKey.ofSuperUser() ).
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
