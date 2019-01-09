package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetPrincipalHandlerTest
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
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );
        runScript( "/site/lib/xp/examples/auth/getPrincipal.js" );
    }

    @Test
    public void testGetUserPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        runFunction( "/site/test/getPrincipal-test.js", "getUserPrincipal" );
    }

    @Test
    public void testGetRolePrincipal()
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "role:roleId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestRole() ) );

        runFunction( "/site/test/getPrincipal-test.js", "getRolePrincipal" );
    }

    @Test
    public void testGetGroupPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "group:myGroupStore:groupId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestGroup() ) );

        runFunction( "/site/test/getPrincipal-test.js", "getGroupPrincipal" );
    }

    @Test
    public void testGetNonExistingPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:XXX" ) ) ).thenReturn( Optional.ofNullable( null ) );

        runFunction( "/site/test/getPrincipal-test.js", "getNonExistingPrincipal" );
    }
}
