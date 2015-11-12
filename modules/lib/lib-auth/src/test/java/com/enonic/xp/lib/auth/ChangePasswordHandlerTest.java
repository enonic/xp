package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.mockito.Matchers.eq;

public class ChangePasswordHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testFunction()
    {
        runFunction( "/site/test/changePassword-test.js", "changePassword" );

        Mockito.verify( this.securityService ).setPassword( eq( PrincipalKey.from( "user:myUserStore:userId" ) ), eq( "test-password" ) );
    }
}
