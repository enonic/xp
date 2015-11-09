package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetPrincipalHandlerTest
    extends ScriptTestSupport
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

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
    public void testGetUserPrincipal()
        throws Exception
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myUserStore:userId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestUser() ) );

        runTestFunction( "/test/getPrincipal-test.js", "getUserPrincipal" );
    }

    @Test
    public void testGetRolePrincipal()
        throws Exception
    {
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "role:roleId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestRole() ) );

        runTestFunction( "/test/getPrincipal-test.js", "getRolePrincipal" );
    }

    @Test
    public void testGetGroupPrincipal()
        throws Exception
    {
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "group:myGroupStore:groupId" ) ) ).thenReturn(
            Optional.of( TestDataFixtures.getTestGroup() ) );

        runTestFunction( "/test/getPrincipal-test.js", "getGroupPrincipal" );
    }

    @Test
    public void testGetNonExistingPrincipal()
        throws Exception
    {

        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "user:myUserStore:XXX" ) ) ).thenReturn( Optional.ofNullable( null ) );

        runTestFunction( "/test/getPrincipal-test.js", "getNonExistingPrincipal" );
    }
}
