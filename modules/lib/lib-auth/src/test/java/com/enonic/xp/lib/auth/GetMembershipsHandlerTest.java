package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetMembershipsHandlerTest
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
    public void testGetUserMemberships()
        throws Exception
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( "user:myUserStore:userId" );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( TestDataFixtures.getTestUser() ) );

        runTestFunction( "/test/getMemberships-test.js", "getUserMemberships" );
    }

    @Test
    public void testGetUserMembershipsWithRoleAndGroup()
        throws Exception
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( "user:myUserStore:userId" );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn(
            Principals.from( TestDataFixtures.getTestUser(), TestDataFixtures.getTestRole(), TestDataFixtures.getTestGroup() ) );

        runTestFunction( "/test/getMemberships-test.js", "getUserMembershipsWithRoleAndGroup" );
    }

    @Test
    public void testGetNonExistingMembership()
        throws Exception
    {

        Mockito.when( securityService.getPrincipals( Mockito.any() ) ).thenReturn( Principals.empty() );

        runTestFunction( "/test/getMemberships-test.js", "getNonExistingMemberships" );
    }
}
