package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetMembershipsHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        final Role role = TestDataFixtures.getTestRole();
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( role.getKey(), group.getKey() );

        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn( principalKeys );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( role, group ) );
        runScript( "/site/lib/xp/examples/auth/getMemberships.js" );
    }

    @Test
    public void testGetUserMemberships()
    {
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( group.getKey() );
        final PrincipalKey pKey = PrincipalKey.from( "user:myUserStore:userId" );

        Mockito.when( securityService.getMemberships( pKey ) ).thenReturn( principalKeys );
        Mockito.verify( securityService, Mockito.never() ).getAllMemberships( pKey );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( group ) );

        runFunction( "/site/test/getMemberships-test.js", "getUserMemberships" );
    }

    @Test
    public void testGetTransitiveUserMemberships()
    {
        final Group group = TestDataFixtures.getTestGroup();
        final PrincipalKeys principalKeys = PrincipalKeys.from( group.getKey() );
        final PrincipalKey pKey = PrincipalKey.from( "user:myUserStore:userId" );

        Mockito.when( securityService.getAllMemberships( pKey ) ).thenReturn( principalKeys );
        Mockito.verify( securityService, Mockito.never() ).getMemberships( pKey );

        Mockito.when( securityService.getPrincipals( principalKeys ) ).thenReturn( Principals.from( group ) );

        runFunction( "/site/test/getMemberships-test.js", "getTransitiveUserMemberships" );
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
