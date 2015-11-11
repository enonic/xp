package com.enonic.xp.lib.auth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class GetMembershipsHandlerTest
    extends OldScriptTestSupport
{

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testGetUserMemberships()
        throws Exception
    {
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( group.getKey() );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( group ) );

        runTestFunction( "/test/getMemberships-test.js", "getUserMemberships" );
    }

    @Test
    public void testGetUserMembershipsWithRoleAndGroup()
        throws Exception
    {
        final Role role = TestDataFixtures.getTestRole();
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( role.getKey(), group.getKey() );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( role, group ) );

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
