package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class LoginHandlerTest
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
        final AccessTokenService accessTokenService = Mockito.mock( AccessTokenService.class );
        Mockito.when( accessTokenService.getIssuer() ).thenReturn( "xp" );
        addService( AccessTokenService.class, accessTokenService );
        Mockito.when( this.securityService.getScopes( Mockito.any() ) ).thenReturn( Scopes.empty() );

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @AfterEach
    void removeNoSessionAuthInfo()
    {
        ContextAccessor.current().getLocalScope().removeAttribute( AuthenticationInfo.class );
    }

    @Test
    void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runScript( "/lib/xp/examples/auth/login.js" );
    }

    @Test
    void testLoginSuccess()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginSuccess" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testLoginWithSkipAuth()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithSkipAuth" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testLoginWithRoles()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithRoles" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer", "role:system.admin" ) ), token.getValue().getRestriction() );
        assertEquals( IdProviderKey.from( "enonic" ), token.getValue().getIdProvider() );
        assertEquals( authInfo, ContextAccessor.current().getAuthInfo() );
        assertNull( ContextAccessor.current().getLocalScope().getSession().getAttribute( AuthenticationInfo.class ) );
    }

    @Test
    void testLoginWithGroupInRolesIsRefused()
    {
        final RuntimeException e = assertThrows( RuntimeException.class, () -> runFunction( "/test/login-test.js", "loginWithGroupInRoles" ) );
        assertTrue( e.getMessage().contains( "group:system:editors" ), e.getMessage() );
        verifyNoInteractions( this.securityService );
    }

    @Test
    void testLoginWithEmptyRolesRestrictsToNothing()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithEmptyRoles" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.to(), token.getValue().getRestriction() );
        assertEquals( authInfo, ContextAccessor.current().getAuthInfo() );
    }

    @Test
    void testLoginWithoutRolesLeavesNoRestriction()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithSkipAuth" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.none(), token.getValue().getRestriction() );
    }

    @Test
    void testLoginWithActor()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithActor" );

        final AuthenticationInfo loggedIn = ContextAccessor.current().getAuthInfo();
        assertEquals( TestDataFixtures.getTestUser().getKey(), loggedIn.getUser().getKey() );
        assertEquals( PrincipalKey.from( "user:enonic:automation" ), loggedIn.getActor() );
    }

    @Test
    void testLoginWithAccessTokenDerivesRolesFromTheScopes()
    {
        serveVirtualHost( Set.of( "xp-api" ), withScopes() );
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithAccessToken" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer", "role:cms.project.site.editor" ) ),
                      token.getValue().getRestriction() );

        final AuthenticationInfo loggedIn = ContextAccessor.current().getAuthInfo();
        assertEquals( TestDataFixtures.getTestUser().getKey(), loggedIn.getUser().getKey() );
        assertEquals( AccessTokenInfo.create()
                          .issuer( "https://issuer.example" )
                          .clientId( "cli" )
                          .scope( Set.of( "content:read", "profile", "unknown" ) )
                          .audience( Set.of( "xp-api" ) )
                          .build(), loggedIn.getAccessToken() );
        assertNull( loggedIn.getAccessToken().getGrantId() );
    }

    @Test
    void testLoginWithAccessTokenAndRolesNarrowsFurther()
    {
        serveVirtualHost( Set.of( "xp-api" ), withScopes() );
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).build();
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithAccessTokenAndRoles" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer" ) ), token.getValue().getRestriction() );
    }

    @Test
    void testLoginWithAccessTokenWithoutScopesLeavesNoRestriction()
    {
        serveVirtualHost( Set.of( "xp-api" ), null );
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).build();
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithAccessToken" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.none(), token.getValue().getRestriction() );
        assertEquals( "https://issuer.example", ContextAccessor.current().getAuthInfo().getAccessToken().getIssuer() );
    }

    @Test
    void testLoginWithAccessTokenForAnotherAudienceIsRefused()
    {
        serveVirtualHost( Set.of( "xp-api" ), withScopes() );

        runFunction( "/test/login-test.js", "loginWithAccessTokenForAnotherAudience" );

        verifyNoInteractions( this.securityService );
        assertNull( ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class ) );
    }

    @Test
    void testLoginWithAccessTokenNamingAnAudienceOutsideAVirtualHostIsRefused()
    {
        runFunction( "/test/login-test.js", "loginWithAccessTokenForAnotherAudience" );

        verifyNoInteractions( this.securityService );
    }

    @Test
    void testLoginWithAccessTokenForXpItselfOutsideAVirtualHost()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).build();
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithAccessTokenForXp" );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( token.capture() );
        assertEquals( RoleRestriction.none(), token.getValue().getRestriction() );
        assertEquals( Set.of( "xp" ), ContextAccessor.current().getAuthInfo().getAccessToken().getAudience() );
    }

    @Test
    void testLoginWithAccessTokenIsRefusedWhereItsAudienceIsNotDeclared()
    {
        runFunction( "/test/login-test.js", "loginWithAccessTokenWithoutAudience" );
        verifyNoInteractions( this.securityService );

        serveVirtualHost( Set.of(), withScopes() );
        runFunction( "/test/login-test.js", "loginWithAccessTokenWithoutAudience" );
        runFunction( "/test/login-test.js", "loginWithAccessTokenForAnotherAudience" );
        verifyNoInteractions( this.securityService );
    }

    @Test
    void testLoginWithAccessTokenRequiresAnIssuer()
    {
        final RuntimeException e =
            assertThrows( RuntimeException.class, () -> runFunction( "/test/login-test.js", "loginWithAccessTokenWithoutIssuer" ) );
        assertTrue( e.getMessage().contains( "issuer" ), e.getMessage() );
        verifyNoInteractions( this.securityService );
    }

    /**
     * Gives the enonic id provider two scopes and returns vhost settings enabling it.
     */
    private VirtualHostIdProvider withScopes()
    {
        final IdProviderKey enonic = IdProviderKey.from( "enonic" );
        Mockito.when( this.securityService.getScopes( enonic ) ).thenReturn( Scopes.from( Scope.create()
                                                                                .idProvider( enonic )
                                                                                .id( "content:read" )
                                                                                .roles( PrincipalKeys.from( "role:cms.project.site.viewer",
                                                                                                            "role:cms.project.site.editor" ) )
                                                                                .build(),
                                                                            Scope.create().idProvider( enonic ).id( "profile" ).required( true ).build() ) );
        return VirtualHostIdProvider.create().build();
    }

    private void serveVirtualHost( final Set<String> audience, final VirtualHostIdProvider enonic )
    {
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getAudience() ).thenReturn( audience );
        Mockito.when( virtualHost.acceptsAudience( Mockito.any(), Mockito.any() ) ).thenCallRealMethod();
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( enonic == null ? Map.of() : Map.of( IdProviderKey.from( "enonic" ), enonic ) );
        this.portalRequest.setVirtualHost( virtualHost );
    }

    @Test
    void testLoginWithScopeNONE()
    {
        ContextAccessor.current().getLocalScope().setSession( null );

        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runFunction( "/test/login-test.js", "loginWithScopeNONE" );

        assertNull( ContextAccessor.current().getLocalScope().getSession() );
        assertNull( ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class ) );
    }

    @Test
    void testLoginSuccessNoSession()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginSuccessNoSession" );

        final AuthenticationInfo localScopeAuth = ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class );
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, localScopeAuth );
        assertNull( sessionAuthInfo );
    }

    @Test
    void testLoginNoIdProviders()
    {
        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runFunction( "/test/login-test.js", "loginNoIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testInvalidLogin()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertNull( sessionAuthInfo );
    }

    @Test
    void testLoginUnspecifiedIdProvider()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getSystemTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        final EmailPasswordAuthToken expectedAuthToken = new EmailPasswordAuthToken( IdProviderKey.system(), "user1@enonic.com", "pwd123" );

        Mockito.when( this.securityService.authenticate( Mockito.argThat( new AuthTokenMatcher( expectedAuthToken ) ) ) )
            .thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginUnspecifiedIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testSessionInvalidatedOnLogin()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.system() ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        final SessionMock session = Mockito.spy( new SessionMock() );
        ContextAccessor.current().getLocalScope().setSession( session );

        runScript( "/lib/xp/examples/auth/login.js" );

        verify( session, times( 4 ) ).invalidate();
    }

    private static class AuthTokenMatcher
        implements ArgumentMatcher<AuthenticationToken>
    {
        EmailPasswordAuthToken thisObject;

        AuthTokenMatcher( EmailPasswordAuthToken thisObject )
        {
            this.thisObject = thisObject;
        }

        @Override
        public boolean matches( AuthenticationToken argument )
        {
            return argument instanceof final EmailPasswordAuthToken authToken &&
                this.thisObject.getIdProvider().equals( authToken.getIdProvider() ) &&
                this.thisObject.getEmail().equals( authToken.getEmail() ) &&
                this.thisObject.getPassword().equals( authToken.getPassword() );

        }
    }
}
