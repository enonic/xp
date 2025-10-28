package com.enonic.xp.lib.auth;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

class GetPrincipalHandlerTest
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
    void testExamples()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );
        runScript( "/lib/xp/examples/auth/getPrincipal.js" );
    }

    @Test
    void testGetUserPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        runFunction( "/test/getPrincipal-test.js", "getUserPrincipal" );
    }

    @Test
    void testGetRolePrincipal()
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "role:roleId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestRole() ) );

        runFunction( "/test/getPrincipal-test.js", "getRolePrincipal" );
    }

    @Test
    void testGetGroupPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "group:myGroupStore:groupId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestGroup() ) );

        runFunction( "/test/getPrincipal-test.js", "getGroupPrincipal" );
    }

    @Test
    void testGetNonExistingPrincipal()
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myIdProvider:XXX" ) ) ).thenReturn( Optional.ofNullable( null ) );

        runFunction( "/test/getPrincipal-test.js", "getNonExistingPrincipal" );
    }

    @Test
    void testGetPrincipalWithoutKey()
    {
        runFunction( "/test/getPrincipal-test.js", "getPrincipalWithoutKey" );
    }
}
