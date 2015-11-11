package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetMembershipsHandlerTest
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
    public void testGetUserMemberships()
    {
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( group.getKey() );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( group ) );

        runFunction( "/site/test/getMemberships-test.js", "getUserMemberships" );
    }

    @Test
    public void testGetUserMembershipsWithRoleAndGroup()
    {
        final Role role = TestDataFixtures.getTestRole();
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( role.getKey(), group.getKey() );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( role, group ) );

        runFunction( "/site/test/getMemberships-test.js", "getUserMembershipsWithRoleAndGroup" );
    }

    @Test
    public void testGetNonExistingMembership()
    {
        Mockito.when( securityService.getPrincipals( Mockito.any() ) ).thenReturn( Principals.empty() );

        runFunction( "/site/test/getMemberships-test.js", "getNonExistingMemberships" );
    }
}
