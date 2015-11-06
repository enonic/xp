package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class CreateGroupHandlerTest
    extends ScriptTestSupport
{

    private SimpleSession session;

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );

        this.session.setAttribute( TestDataFixtures.createAuthenticationInfo() );
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        Mockito.when( securityService.createGroup( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestGroup() );

        runTestFunction( "/test/createGroup-test.js", "createGroup" );
    }

    @Test
    public void testCreateGroupWithUnAuthenticated()
        throws Exception
    {

        this.session.setAttribute( AuthenticationInfo.unAuthenticated() );

        runTestFunction( "/test/createGroup-test.js", "createGroupUnAuthenticated" );
    }
}
