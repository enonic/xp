package com.enonic.xp.portal.impl.idprovider;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.VerifiedUserKeyAuthToken;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.AccessTokenVerification;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.VerifyAccessTokenParams;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccessTokenFilterTest
{
    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IdProviderKey.system(), "alice" );

    private final AccessTokenService accessTokenService = mock( AccessTokenService.class );

    private final GrantService grantService = mock( GrantService.class );

    private final SecurityService securityService = mock( SecurityService.class );

    private final HttpServletRequest request = mock( HttpServletRequest.class );

    private final HttpServletResponse response = mock( HttpServletResponse.class );

    private final FilterChain chain = mock( FilterChain.class );

    private final Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private AccessTokenFilter filter;

    private final AccessTokenClaims claims = AccessTokenClaims.create()
        .issuer( "xp" )
        .subject( ALICE )
        .audience( Set.of( "api" ) )
        .scope( Set.of( "content:read" ) )
        .clientId( "my-cli" )
        .grantId( "grant-1" )
        .issuedAt( now )
        .expiresAt( now.plusSeconds( 3600 ) )
        .tokenId( "jti" )
        .build();

    @BeforeEach
    void setUp()
    {
        filter = new AccessTokenFilter( accessTokenService, grantService, securityService );
        when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer xp-token" );
        when( accessTokenService.verify( token( "xp-token" ) ) ).thenReturn( new AccessTokenVerification.Verified( claims ) );
        when( grantService.get( ALICE, "grant-1" ) ).thenReturn( Optional.of( grant( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) ) ) );
        final User user = User.create().key( ALICE ).login( "alice" ).displayName( "Alice" ).build();
        when( securityService.authenticate( any() ) ).thenReturn(
            AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build() );
        vhost( Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ), Set.of( "api" ) );
    }

    private Grant grant( final RoleRestriction restriction )
    {
        return Grant.create()
            .id( "grant-1" )
            .subject( ALICE )
            .clientId( "my-cli" )
            .scope( Set.of( "content:read" ) )
            .audience( Set.of( "api" ) )
            .restriction( restriction )
            .createdAt( now )
            .rotatedAt( now )
            .expiresAt( now.plusSeconds( 3600 ) )
            .build();
    }

    private void vhost( final Map<IdProviderKey, Set<String>> idProviders, final Set<String> audience )
    {
        final VirtualHost virtualHost = mock( VirtualHost.class );
        final Map<IdProviderKey, VirtualHostIdProvider> mapped = new LinkedHashMap<>();
        idProviders.forEach( ( key, flows ) -> mapped.put( key, VirtualHostIdProvider.create().flows( flows ).build() ) );
        when( virtualHost.getIdProviders() ).thenReturn( mapped );
        when( virtualHost.getAudience() ).thenReturn( audience );
        when( virtualHost.acceptsAudience( any(), any() ) ).thenCallRealMethod();
        when( virtualHost.getName() ).thenReturn( "test" );
        when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }

    private void run()
        throws Exception
    {
        ContextBuilder.create().build().callWith( () -> {
            filter.doHandle( request, response, chain );
            return null;
        } );
    }

    @Test
    void valid_token_authenticates_the_request()
        throws Exception
    {
        final AuthenticationInfo[] seen = new AuthenticationInfo[1];
        org.mockito.Mockito.doAnswer( i -> {
            seen[0] = ContextAccessor.current().getAuthInfo();
            return null;
        } ).when( chain ).doFilter( any(), any() );

        run();

        verify( chain ).doFilter( request, response );
        assertThat( seen[0].isAuthenticated() ).isTrue();
        assertThat( seen[0].getUser().getKey() ).isEqualTo( ALICE );
        verify( request ).setAttribute( AccessTokenFilter.CLAIMS_ATTRIBUTE, claims );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( securityService ).authenticate( token.capture() );
        assertThat( token.getValue() ).isInstanceOf( VerifiedUserKeyAuthToken.class );
        assertThat( ( (VerifiedUserKeyAuthToken) token.getValue() ).getUserKey() ).isEqualTo( ALICE );
        assertThat( token.getValue().getRestriction() ).isEqualTo( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) );
        verify( response, never() ).sendError( anyInt() );
    }

    @Test
    void token_actor_becomes_the_authentication_actor()
        throws Exception
    {
        final PrincipalKey service = PrincipalKey.from( "user:system:automation" );
        when( accessTokenService.verify( token( "xp-token" ) ) ).thenReturn( new AccessTokenVerification.Verified( AccessTokenClaims.create()
                                                                                       .issuer( "xp" )
                                                                                       .subject( ALICE )
                                                                                       .audience( Set.of( "api" ) )
                                                                                       .clientId( "my-cli" )
                                                                                       .grantId( "grant-1" )
                                                                                       .actor( service )
                                                                                       .issuedAt( now )
                                                                                       .expiresAt( now.plusSeconds( 3600 ) )
                                                                                       .tokenId( "jti" )
                                                                                       .build() ) );
        final AuthenticationInfo[] seen = new AuthenticationInfo[1];
        org.mockito.Mockito.doAnswer( i -> {
            seen[0] = ContextAccessor.current().getAuthInfo();
            return null;
        } ).when( chain ).doFilter( any(), any() );

        run();

        assertThat( seen[0].getUser().getKey() ).isEqualTo( ALICE );
        assertThat( seen[0].getActor() ).isEqualTo( service );
        assertThat( seen[0].getPrincipals() ).as( "the actor is no principal" ).doesNotContain( service );
    }

    @Test
    void no_bearer_or_foreign_bearer_passes_through()
        throws Exception
    {
        when( request.getHeader( "Authorization" ) ).thenReturn( null );
        run();
        verify( chain ).doFilter( request, response );

        when( request.getHeader( "Authorization" ) ).thenReturn( "Basic abc" );
        run();

        when( request.getHeader( "Authorization" ) ).thenReturn( "Bearer upstream-jwt" );
        when( accessTokenService.verify( token( "upstream-jwt" ) ) ).thenReturn( new AccessTokenVerification.InvalidIssuer() );
        run();

        verify( chain, org.mockito.Mockito.times( 3 ) ).doFilter( request, response );
        verify( accessTokenService, never() ).verify( token( "xp-token" ) );
        verify( accessTokenService ).verify( token( "upstream-jwt" ) );
        verify( response, never() ).sendError( anyInt() );
    }

    @Test
    void already_authenticated_request_is_left_alone()
        throws Exception
    {
        final User user = User.create().key( ALICE ).login( "alice" ).build();
        ContextBuilder.create().authInfo( AuthenticationInfo.create().user( user ).build() ).build().callWith( () -> {
            filter.doHandle( request, response, chain );
            return null;
        } );
        verify( chain ).doFilter( request, response );
        verify( accessTokenService, never() ).verify( any() );
    }

    @Test
    void invalid_xp_token_is_rejected_not_passed_on()
        throws Exception
    {
        when( accessTokenService.verify( token( "xp-token" ) ) ).thenReturn( new AccessTokenVerification.Invalid() );
        run();
        verify( response ).setHeader( org.mockito.ArgumentMatchers.eq( "WWW-Authenticate" ), org.mockito.ArgumentMatchers.contains( "invalid_token" ) );
        verify( response ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        verify( chain, never() ).doFilter( any(), any() );
    }

    @Test
    void token_flow_and_audience_gate_acceptance()
        throws Exception
    {
        vhost( Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.LOGIN ) ), Set.of( "api" ) );
        run();
        verify( response ).sendError( HttpServletResponse.SC_UNAUTHORIZED );

        vhost( Map.of( IdProviderKey.from( "other" ), Set.of() ), Set.of( "api" ) );
        run();
        verify( response, org.mockito.Mockito.times( 2 ) ).sendError( HttpServletResponse.SC_UNAUTHORIZED );

        vhost( Map.of( IdProviderKey.system(), Set.of() ), Set.of( "admin" ) );
        run();
        verify( response, org.mockito.Mockito.times( 3 ) ).sendError( HttpServletResponse.SC_UNAUTHORIZED );

        verify( chain, never() ).doFilter( any(), any() );

        vhost( Map.of( IdProviderKey.system(), Set.of() ), Set.of( "admin", "api" ) );
        run();
        verify( chain ).doFilter( request, response );
    }

    @Test
    void a_token_for_xp_itself_is_accepted_only_on_a_vhost_declaring_no_audience()
    {
        final AccessTokenClaims forXp = AccessTokenClaims.create()
            .issuer( "xp" )
            .subject( ALICE )
            .audience( Set.of( "xp" ) )
            .clientId( "cli" )
            .issuedAt( now )
            .expiresAt( now.plusSeconds( 10 ) )
            .tokenId( "j" )
            .build();
        final VirtualHost served = vhostServing( Set.of( "api" ) );
        assertThat( AccessTokenFilter.refusal( forXp, served ) ).as( "a declared list is matched alone" ).isNotNull();

        final VirtualHost unnamed = vhostServing( Set.of() );
        assertThat( AccessTokenFilter.refusal( forXp, unnamed ) ).isNull();
        assertThat( AccessTokenFilter.refusal( claims, unnamed ) ).as( "a token for an audience needs a vhost serving it" )
            .isNotNull()
            .contains( "does not name this XP" );
        final AccessTokenClaims forBoth = AccessTokenClaims.create()
            .issuer( "xp" )
            .subject( ALICE )
            .audience( Set.of( "xp", "api" ) )
            .clientId( "cli" )
            .issuedAt( now )
            .expiresAt( now.plusSeconds( 10 ) )
            .tokenId( "j2" )
            .build();
        assertThat( AccessTokenFilter.refusal( forBoth, served ) ).as( "a token for everything is good on both" ).isNull();
        assertThat( AccessTokenFilter.refusal( forBoth, unnamed ) ).isNull();
    }

    private static VirtualHost vhostServing( final Set<String> audience )
    {
        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.system(), VirtualHostIdProvider.create().build() ) );
        when( virtualHost.getAudience() ).thenReturn( audience );
        when( virtualHost.acceptsAudience( any(), any() ) ).thenCallRealMethod();
        return virtualHost;
    }

    @Test
    void token_needs_a_live_grant_and_runs_with_its_restriction()
        throws Exception
    {
        when( grantService.get( ALICE, "grant-1" ) ).thenReturn( Optional.empty() );
        run();
        verify( response ).sendError( HttpServletResponse.SC_UNAUTHORIZED );

        when( accessTokenService.verify( token( "xp-token" ) ) ).thenReturn(
            new AccessTokenVerification.Verified( AccessTokenClaims.create()
                                                      .issuer( "xp" )
                                                      .subject( ALICE )
                                                      .audience( Set.of( "api" ) )
                                                      .clientId( "my-cli" )
                                                      .issuedAt( now )
                                                      .expiresAt( now.plusSeconds( 3600 ) )
                                                      .tokenId( "jti-no-grant" )
                                                      .build() ) );
        run();
        verify( response, times( 2 ) ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        verify( chain, never() ).doFilter( any(), any() );
        verify( securityService, never() ).authenticate( any() );

        when( accessTokenService.verify( token( "xp-token" ) ) ).thenReturn( new AccessTokenVerification.Verified( claims ) );
        when( grantService.get( ALICE, "grant-1" ) ).thenReturn( Optional.of( grant( RoleRestriction.none() ) ) );
        run();
        verify( chain ).doFilter( request, response );

        final ArgumentCaptor<AuthenticationToken> token = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( securityService ).authenticate( token.capture() );
        assertThat( token.getValue().getRestriction() ).as( "the grant's current restriction, not the token's" )
            .isEqualTo( RoleRestriction.none() );
    }

    @Test
    void user_gone_or_disabled()
        throws Exception
    {
        when( securityService.authenticate( any() ) ).thenReturn( AuthenticationInfo.unAuthenticated() );
        run();
        verify( response ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        verify( chain, never() ).doFilter( any(), any() );
    }

    private static VerifyAccessTokenParams token( final String token )
    {
        return argThat( params -> params != null && token.equals( params.getToken() ) );
    }
}
