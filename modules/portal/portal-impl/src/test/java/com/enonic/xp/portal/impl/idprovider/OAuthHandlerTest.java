package com.enonic.xp.portal.impl.idprovider;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The id provider's {@code oauthConsent} function is stubbed to echo the action it was asked
 * to render and to keep the context it received, so the tests assert on the data handed to the
 * page rather than on markup, which is the id provider's.
 */
class OAuthHandlerTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IDP, "alice" );

    private static final ObjectMapper JSON = new ObjectMapper();

    private final GrantService grantService = mock( GrantService.class );

    private final AccessTokenService accessTokenService = mock( AccessTokenService.class );

    private final SecurityService securityService = mock( SecurityService.class );

    private final VirtualHostService virtualHostService = mock( VirtualHostService.class );

    /** No flow restriction. */
    private static final VirtualHostIdProvider UNRESTRICTED = VirtualHostIdProvider.create().allFlows().build();

    private final IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );

    private Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private Map<String, Object> lastContext;

    private Map<String, Object> lastContextOfConfirm;

    private OAuthHandler handler;

    @BeforeEach
    void setUp()
        throws Exception
    {
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of() );
        when( idProviderControllerService.hasFunction( any(), eq( OAuthHandler.CONSENT_FUNCTION ) ) ).thenReturn( true );
        when( idProviderControllerService.execute( any() ) ).thenAnswer( i -> {
            final IdProviderControllerExecutionParams params = i.getArgument( 0 );
            @SuppressWarnings("unchecked") final Map<String, Object> context =
                (Map<String, Object>) params.getPortalRequest().getRawRequest().getAttribute( OAuthHandler.CONTEXT_ATTRIBUTE );
            lastContext = context;
            return PortalResponse.create()
                .status( HttpStatus.OK )
                .contentType( MediaType.HTML_UTF_8 )
                .body( "page:" + context.get( "action" ) )
                .build();
        } );
        when( securityService.getScopes( any() ) ).thenReturn( Scopes.empty() );
        handler = new OAuthHandler( grantService, accessTokenService, securityService, virtualHostService, idProviderControllerService,
                                          new InMemorySharedMapService(), new Clock()
        {
            @Override
            public ZoneOffset getZone()
            {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone( final java.time.ZoneId zone )
            {
                return this;
            }

            @Override
            public Instant instant()
            {
                return now;
            }
        } );

        when( grantService.create( any() ) ).thenAnswer( i -> {
            final CreateGrantParams params = i.getArgument( 0 );
            final Grant grant = Grant.create()
                .id( "grant-1" )
                .subject( params.getSubject() )
                .clientId( params.getClientId() )
                .scope( params.getScope() )
                .audience( params.getAudience() )
                .restriction( params.getRestriction() )
                .createdAt( now )
                .rotatedAt( now )
                .expiresAt( now.plus( Duration.ofDays( 90 ) ) )
                .build();
            return new GrantWithRefreshToken( grant, "refresh-1" );
        } );
        when( accessTokenService.getIssuer() ).thenReturn( "xp" );
        when( accessTokenService.issue( any() ) ).thenAnswer( i -> {
            final IssueAccessTokenParams params = i.getArgument( 0 );
            final AccessTokenClaims claims = AccessTokenClaims.create()
                .issuer( "xp" )
                .subject( params.getSubject() )
                .audience( params.getAudience() )
                .scope( params.getScope() )
                .clientId( params.getClientId() )
                .grantId( params.getGrantId() )
                .issuedAt( now )
                .expiresAt( now.plus( Duration.ofHours( 1 ) ) )
                .tokenId( "jti-1" )
                .build();
            return new AccessToken( "access-1", claims );
        } );
    }

    private static PortalRequest request( final HttpMethod method, final String endpoint, final Map<String, String> params )
    {
        final PortalRequest request = new PortalRequest();
        request.setMethod( method );
        request.setScheme( "https" );
        request.setHost( "admin.example.com" );
        request.setRawPath( "/_/idprovider/system/" + endpoint );
        request.setUrl( "https://admin.example.com/_/idprovider/system/" + endpoint + ( method == HttpMethod.GET ? "?x=1" : "" ) );
        params.forEach( ( k, v ) -> request.getParams().put( k, v ) );

        final Map<String, Object> attributes = new HashMap<>();
        final HttpServletRequest raw = mock( HttpServletRequest.class );
        doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( raw ).setAttribute( any(), any() );
        when( raw.getAttribute( any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        request.setRawRequest( raw );
        return request;
    }

    private static JsonNode body( final PortalResponse response )
        throws Exception
    {
        return JSON.readTree( (String) response.getBody() );
    }

    @SuppressWarnings("unchecked")
    private static List<String> scopeIds( final Map<String, Object> context )
    {
        return ( (List<Map<String, Object>>) context.get( "scope" ) ).stream().map( s -> (String) s.get( "id" ) ).toList();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> user( final Map<String, Object> context )
    {
        return (Map<String, Object>) context.get( "user" );
    }

    private <T> T asAlice( final Callable<T> call )
    {
        return asUser( ALICE, call );
    }

    private static <T> T asUser( final PrincipalKey key, final Callable<T> call )
    {
        return asUser( key, RoleRestriction.none(), call );
    }

    private static <T> T asUser( final PrincipalKey key, final RoleRestriction restriction, final Callable<T> call )
    {
        final User user = User.create().key( key ).login( key.getId() ).displayName( "Alice" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create()
            .user( user )
            .principals( restriction.apply( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN, PrincipalKey.ofRole( "cms.cm.app" ) ) ) )
            .restriction( restriction )
            .build();
        return ContextBuilder.create().authInfo( authInfo ).build().callWith( () -> {
            ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
            return call.call();
        } );
    }

    private static VirtualHostIdProvider withFlows( final String... flows )
    {
        return VirtualHostIdProvider.create().flows( Set.of( flows ) ).build();
    }

    /**
     * Gives the id provider these scopes, none required, and returns settings without flow restriction.
     */
    private VirtualHostIdProvider withScopes( final Map<String, PrincipalKeys> scopes )
    {
        final List<Scope> known = new ArrayList<>();
        scopes.forEach( ( id, roles ) -> known.add( Scope.create().idProvider( IDP ).id( id ).roles( roles ).build() ) );
        when( securityService.getScopes( IDP ) ).thenReturn( Scopes.from( known ) );
        return UNRESTRICTED;
    }

    private String approve( final String userCode, final String... keptScopes )
    {
        return approve( UNRESTRICTED, userCode, keptScopes );
    }

    private String approve( final VirtualHostIdProvider settings, final String userCode, final String... keptScopes )
    {
        return asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) ), IDP, "device", settings );
            lastContextOfConfirm = lastContext;
            final PortalRequest approval = request( HttpMethod.POST, "device",
                                                    Map.of( "user_code", userCode, "approve", "true", "_csrf",
                                                            (String) lastContext.get( "csrf" ) ) );
            for ( String scope : keptScopes )
            {
                approval.getParams().put( "scope", scope );
            }
            return (String) handler.handle( approval, IDP, "device", settings ).getBody();
        } );
    }

    @Test
    void full_device_flow()
        throws Exception
    {
        final PortalResponse codes =
            handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "my-cli" ) ), IDP, "device/code", UNRESTRICTED );
        assertThat( codes.getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( codes.getHeaders() ).containsEntry( "Cache-Control", "no-store" );
        final JsonNode issued = body( codes );
        final String deviceCode = issued.get( "device_code" ).asText();
        final String userCode = issued.get( "user_code" ).asText();
        assertThat( userCode ).matches( "[A-Z]{4}-[A-Z]{4}" );
        assertThat( issued.get( "verification_uri" ).asText() ).isEqualTo( "https://admin.example.com/_/idprovider/system/device" );
        assertThat( issued.get( "verification_uri_complete" ).asText() ).isEqualTo(
            "https://admin.example.com/_/idprovider/system/device?user_code=" + userCode );
        assertThat( issued.get( "expires_in" ).asInt() ).isEqualTo( 600 );
        assertThat( issued.get( "interval" ).asInt() ).isEqualTo( 5 );

        final PortalRequest poll = request( HttpMethod.POST, "token",
                                            Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", deviceCode,
                                                    "client_id", "my-cli" ) );
        final PortalResponse pending = handler.handle( poll, IDP, "token", UNRESTRICTED );
        assertThat( pending.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( pending ).get( "error" ).asText() ).isEqualTo( "authorization_pending" );
        assertThat( body( handler.handle( poll, IDP, "token", UNRESTRICTED ) ).get( "error" ).asText() ).isEqualTo( "slow_down" );

        final PortalRequest page = request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) );
        assertThat( handler.handle( page, IDP, "device", UNRESTRICTED ).getStatus() ).isEqualTo( HttpStatus.UNAUTHORIZED );
        verify( idProviderControllerService, Mockito.never() ).execute( any() );

        final String[] csrf = new String[1];
        asAlice( () -> {
            final PortalResponse form = handler.handle( page, IDP, "device", UNRESTRICTED );
            assertThat( form.getBody() ).isEqualTo( "page:confirm" );
            assertThat( lastContext ).containsEntry( "idProvider", "system" ).containsEntry( "userCode", userCode )
                .containsEntry( "clientId", "my-cli" ).containsEntry( "audience", List.of( "xp" ) );
            assertThat( scopeIds( lastContext ) ).isEmpty();
            assertThat( lastContext ).as( "the page can say the token gets the user's full rights" ).containsEntry( "unrestricted", true );
            assertThat( user( lastContext ) ).containsEntry( "key", ALICE.toString() ).containsEntry( "displayName", "Alice" );
            csrf[0] = (String) lastContext.get( "csrf" );
            assertThat( csrf[0] ).isNotBlank();

            assertThatThrownBy( () -> handler.handle(
                request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "true" ) ), IDP, "device", UNRESTRICTED ) )
                .isInstanceOf( WebException.class );

            final PortalRequest approval =
                request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "true", "_csrf", csrf[0] ) );
            approval.getParams().put( "scope", "admin" );
            final PortalResponse done = handler.handle( approval, IDP, "device", UNRESTRICTED );
            assertThat( done.getBody() ).isEqualTo( "page:done" );
            assertThat( lastContext ).containsEntry( "approved", true );
            return null;
        } );

        now = now.plusSeconds( 10 );
        final PortalResponse tokens = handler.handle( poll, IDP, "token", UNRESTRICTED );
        assertThat( tokens.getStatus() ).isEqualTo( HttpStatus.OK );
        final JsonNode tokenBody = body( tokens );
        assertThat( tokenBody.get( "access_token" ).asText() ).isEqualTo( "access-1" );
        assertThat( tokenBody.get( "token_type" ).asText() ).isEqualTo( "Bearer" );
        assertThat( tokenBody.get( "expires_in" ).asLong() ).isEqualTo( 3600 );
        assertThat( tokenBody.get( "refresh_token" ).asText() ).isEqualTo( "refresh-1" );
        assertThat( tokenBody.has( "scope" ) ).as( "nothing was asked for, nothing is granted" ).isFalse();

        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getSubject() ).isEqualTo( ALICE );
        assertThat( grant.getValue().getClientId() ).isEqualTo( "my-cli" );
        assertThat( grant.getValue().getScope() ).isEmpty();
        assertThat( grant.getValue().getRestriction() ).as( "no scopes: unrestricted" ).isEqualTo( RoleRestriction.none() );
        assertThat( grant.getValue().getAudience() ).as( "no resource asked for: the token is for XP itself" ).containsExactly( "xp" );

        final ArgumentCaptor<IssueAccessTokenParams> token = ArgumentCaptor.forClass( IssueAccessTokenParams.class );
        verify( accessTokenService ).issue( token.capture() );
        assertThat( token.getValue().getGrantId() ).isEqualTo( "grant-1" );
        assertThat( token.getValue().getSubject() ).isEqualTo( ALICE );

        assertThat( body( handler.handle( poll, IDP, "token", UNRESTRICTED ) ).get( "error" ).asText() ).isEqualTo( "expired_token" );
    }

    @Test
    void a_request_authenticated_by_an_access_token_cannot_approve()
        throws Exception
    {
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                      "device/code", UNRESTRICTED ) );
        final String userCode = issued.get( "user_code" ).asText();
        final User alice = User.create().key( ALICE ).login( "alice" ).displayName( "Alice" ).build();
        final AuthenticationInfo viaBearer = AuthenticationInfo.create()
            .user( alice )
            .principals( RoleKeys.ADMIN_LOGIN )
            .accessToken( AccessTokenInfo.create().issuer( "xp" ).build() )
            .build();
        ContextBuilder.create().authInfo( viaBearer ).build().callWith( () -> {
            ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
            for ( HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.POST} )
            {
                final PortalRequest viaToken = request( method, "device", Map.of( "user_code", userCode, "approve", "true" ) );
                assertThatThrownBy( () -> handler.handle( viaToken, IDP, "device", UNRESTRICTED ) ).as( method.toString() )
                    .isInstanceOf( WebException.class )
                    .hasMessageContaining( "access token" );
            }
            return null;
        } );
        verify( grantService, never() ).create( any() );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token", Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                                     issued.get( "device_code" ).asText(), "client_id",
                                                                                     "cli" ) ), IDP, "token", UNRESTRICTED ) ).get( "error" )
                        .asText() ).as( "still pending" ).isEqualTo( "authorization_pending" );
    }

    @Test
    void deny()
        throws Exception
    {
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                      "device/code", UNRESTRICTED ) );
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", issued.get( "user_code" ).asText() ) ), IDP, "device",
                            UNRESTRICTED );
            final PortalResponse done = handler.handle( request( HttpMethod.POST, "device",
                                                                 Map.of( "user_code", issued.get( "user_code" ).asText(), "approve",
                                                                         "false", "_csrf", (String) lastContext.get( "csrf" ) ) ), IDP,
                                                        "device", UNRESTRICTED );
            assertThat( done.getBody() ).isEqualTo( "page:done" );
            assertThat( lastContext ).containsEntry( "approved", false );
            return null;
        } );
        final PortalResponse polled = handler.handle( request( HttpMethod.POST, "token",
                                                               Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                       issued.get( "device_code" ).asText(), "client_id", "cli" ) ), IDP,
                                                      "token", UNRESTRICTED );
        assertThat( body( polled ).get( "error" ).asText() ).isEqualTo( "access_denied" );
        verifyNoInteractions( grantService );
    }

    @Test
    void the_choice_is_the_forms_not_the_links()
        throws Exception
    {
        final VirtualHostIdProvider settings = withScopes( Map.of( "content:read", PrincipalKeys.from( "role:cms.cm.app" ) ) );
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read" ) ), IDP,
                                                      "device/code", settings ) );
        final String userCode = issued.get( "user_code" ).asText();
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) ), IDP, "device", settings );
            final String csrf = (String) lastContext.get( "csrf" );
            for ( String query : new String[]{"approve=true", "user_code=" + userCode + "&scope=content:read", "scope"} )
            {
                // The merged parameter map puts the query first, so the form's own values would lose.
                final PortalRequest viaLink = request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "false", "_csrf", csrf ) );
                when( viaLink.getRawRequest().getQueryString() ).thenReturn( query );
                assertThatThrownBy( () -> handler.handle( viaLink, IDP, "device", settings ) ).as( query )
                    .isInstanceOf( WebException.class )
                    .hasMessageContaining( "form fields" );
            }
            final PortalRequest fromLink = request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "false", "_csrf", csrf ) );
            when( fromLink.getRawRequest().getQueryString() ).thenReturn( "user_code=" + userCode );
            assertThat( handler.handle( fromLink, IDP, "device", settings ).getBody() ).as( "the code in the link is fine" ).isEqualTo( "page:done" );
            assertThat( lastContext ).containsEntry( "approved", false );
            return null;
        } );
        verifyNoInteractions( grantService );
    }

    @Test
    void verification_page_without_or_with_bad_code()
    {
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", UNRESTRICTED );
            assertThat( lastContext ).containsEntry( "action", "enter" ).doesNotContainKey( "error" );
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", "ZZZZ-ZZZZ" ) ), IDP, "device", UNRESTRICTED );
            assertThat( lastContext ).containsEntry( "action", "enter" );
            assertThat( lastContext ).as( "an error code, worded by the page" ).containsEntry( "error", "invalid_code" );
            return null;
        } );
    }

    @Test
    void user_of_another_id_provider_cannot_approve()
    {
        asUser( PrincipalKey.ofUser( IdProviderKey.from( "other" ), "bob" ), () -> {
            final PortalResponse response = handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", UNRESTRICTED );
            assertThat( response.getBody() ).isEqualTo( "page:wrongAccount" );
            assertThat( user( lastContext ) ).containsEntry( "key", "user:other:bob" );
            return null;
        } );
    }

    @Test
    void id_provider_without_the_page_does_not_support_device_login()
        throws Exception
    {
        when( idProviderControllerService.hasFunction( any(), any() ) ).thenReturn( false );

        assertThatThrownBy( () -> handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                  "device/code", UNRESTRICTED ) ).isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.NOT_FOUND );
        asAlice( () -> {
            assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", UNRESTRICTED ) )
                .isInstanceOf( WebException.class );
            return null;
        } );
        assertThatThrownBy( () -> handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", "x" ) ), IDP,
            "token", UNRESTRICTED ) ).isInstanceOf( WebException.class );

        when( grantService.redeem( any() ) ).thenReturn( Optional.empty() );
        final PortalResponse refresh = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad", "client_id", "cli" ) ), IDP,
            "token", UNRESTRICTED );
        assertThat( body( refresh ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );
        verify( idProviderControllerService, Mockito.never() ).execute( any() );
    }

    @Test
    void page_function_is_called_with_the_id_provider_and_the_request()
        throws Exception
    {
        asAlice( () -> {
            final PortalRequest page = request( HttpMethod.GET, "device", Map.of() );
            handler.handle( page, IDP, "device", UNRESTRICTED );
            final ArgumentCaptor<IdProviderControllerExecutionParams> params =
                ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
            verify( idProviderControllerService ).execute( params.capture() );
            assertThat( params.getValue().getFunctionName() ).isEqualTo( "oauthConsent" );
            assertThat( params.getValue().getIdProviderKey() ).isEqualTo( IDP );
            assertThat( params.getValue().getPortalRequest() ).isSameAs( page );
            return null;
        } );
    }

    @Test
    void client_id_mismatch_on_redeem()
        throws Exception
    {
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                      "device/code", UNRESTRICTED ) );
        approve( issued.get( "user_code" ).asText() );
        final PortalResponse polled = handler.handle( request( HttpMethod.POST, "token",
                                                               Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                       issued.get( "device_code" ).asText(), "client_id", "other" ) ),
                                                      IDP, "token", UNRESTRICTED );
        assertThat( body( polled ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );
        verify( grantService, never() ).create( any() );

        final PortalResponse retried = handler.handle( request( HttpMethod.POST, "token",
                                                                Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                        issued.get( "device_code" ).asText(), "client_id", "cli" ) ),
                                                       IDP, "token", UNRESTRICTED );
        assertThat( retried.getStatus() ).as( "the wrong client's attempt did not consume the approval" ).isEqualTo( HttpStatus.OK );
        assertThat( body( retried ).get( "access_token" ).asText() ).isEqualTo( "access-1" );
    }

    @Test
    void client_id_is_required_on_every_request_and_any_value_is_accepted()
        throws Exception
    {
        final String odd = "any string the client calls itself";
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", " " + odd + " " ) ), IDP,
                                                      "device/code", UNRESTRICTED ) );
        approve( issued.get( "user_code" ).asText() );

        for ( Map<String, String> params : List.of( Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                            issued.get( "device_code" ).asText() ),
                                                    Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                            issued.get( "device_code" ).asText(), "client_id", " " ),
                                                    Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1" ),
                                                    Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "" ),
                                                    Map.of( "client_id", "" ) ) )
        {
            final String endpoint = params.containsKey( "grant_type" ) ? "token" : "device/code";
            final PortalResponse refused = handler.handle( request( HttpMethod.POST, endpoint, params ), IDP, endpoint, UNRESTRICTED );
            assertThat( refused.getStatus() ).as( "%s", params ).isEqualTo( HttpStatus.BAD_REQUEST );
            assertThat( body( refused ).get( "error" ).asText() ).as( "%s", params ).isEqualTo( "invalid_request" );
            assertThat( body( refused ).get( "error_description" ).asText() ).as( "%s", params ).isEqualTo( "Missing client_id" );
        }
        verify( grantService, never() ).redeem( any() );
        verify( grantService, never() ).create( any() );

        final PortalResponse polled = handler.handle( request( HttpMethod.POST, "token",
                                                               Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                       issued.get( "device_code" ).asText(), "client_id", odd ) ), IDP,
                                                      "token", UNRESTRICTED );
        assertThat( polled.getStatus() ).as( "the refused polls left the approval for the client" ).isEqualTo( HttpStatus.OK );
        final ArgumentCaptor<CreateGrantParams> created = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( created.capture() );
        assertThat( created.getValue().getClientId() ).as( "trimmed, otherwise as the client said it" ).isEqualTo( odd );
    }

    @Test
    void refresh()
        throws Exception
    {
        final Grant grant = Grant.create()
            .id( "grant-1" )
            .subject( ALICE )
            .clientId( "cli" )
            .scope( Set.of( "content:read" ) )
            .audience( Set.of( "api" ) )
            .restriction( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) )
            .createdAt( now )
            .rotatedAt( now )
            .expiresAt( now.plusSeconds( 3600 ) )
            .build();
        when( grantService.redeem( argThat( p -> p != null && "refresh-1".equals( p.getRefreshToken() ) && IDP.equals( p.getIdProvider() ) &&
            "cli".equals( p.getClientId() ) ) ) ).thenReturn( Optional.of( new GrantWithRefreshToken( grant, "refresh-2" ) ) );

        final PortalResponse ok = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "cli" ) ),
            IDP, "token", UNRESTRICTED );
        assertThat( ok.getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( body( ok ).get( "refresh_token" ).asText() ).isEqualTo( "refresh-2" );
        assertThat( body( ok ).get( "access_token" ).asText() ).isEqualTo( "access-1" );

        final ArgumentCaptor<IssueAccessTokenParams> token = ArgumentCaptor.forClass( IssueAccessTokenParams.class );
        verify( accessTokenService ).issue( token.capture() );
        assertThat( token.getValue().getAudience() ).containsExactly( "api" );
        assertThat( token.getValue().getScope() ).containsExactly( "content:read" );
        assertThat( token.getValue().getGrantId() ).isEqualTo( "grant-1" );

        final PortalResponse bad = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad", "client_id", "cli" ) ), IDP,
            "token", UNRESTRICTED );
        assertThat( bad.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( bad ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );

        final PortalResponse mismatch = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "x" ) ),
            IDP, "token", UNRESTRICTED );
        assertThat( body( mismatch ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );

        final PortalResponse otherIdp = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "cli" ) ),
            IdProviderKey.from( "other" ), "token", UNRESTRICTED );
        assertThat( body( otherIdp ).get( "error" ).asText() ).as( "a grant of another id provider's user" ).isEqualTo( "invalid_grant" );
    }

    @Test
    void device_code_grant_needs_the_device_flow_but_refresh_does_not()
        throws Exception
    {
        final PortalResponse refused = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", "x" ) ), IDP,
            "token", withFlows( "native" ) );
        assertThat( body( refused ).get( "error" ).asText() ).isEqualTo( "unauthorized_client" );

        when( grantService.redeem( any() ) ).thenReturn( Optional.empty() );
        final PortalResponse refresh = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad", "client_id", "cli" ) ), IDP,
            "token", withFlows( "native" ) );
        assertThat( body( refresh ).get( "error" ).asText() ).as( "reaches the grant service" ).isEqualTo( "invalid_grant" );
    }

    @Test
    void without_scopes_no_scope_may_be_asked_for()
        throws Exception
    {
        final PortalResponse refused = handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read" ) ), IDP, "device/code",
            UNRESTRICTED );
        assertThat( refused.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( refused ).get( "error" ).asText() ).isEqualTo( "invalid_scope" );
        assertThat( body( refused ).get( "error_description" ).asText() ).contains( "has no scopes" );
    }

    @Test
    void known_scopes_are_required_and_restrict_the_grant()
        throws Exception
    {
        final VirtualHostIdProvider settings = withScopes(
            Map.of( "content:read", PrincipalKeys.from( "role:cms.cm.app", "role:cms.project.site.viewer" ), "profile",
                    PrincipalKeys.empty() ) );

        final PortalResponse omitted =
            handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP, "device/code", settings );
        assertThat( omitted.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( omitted ).get( "error" ).asText() ).isEqualTo( "invalid_scope" );
        assertThat( body( omitted ).get( "error_description" ).asText() ).contains( "content:read" ).contains( "profile" );

        final PortalResponse refused = handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read content:write" ) ), IDP,
            "device/code", settings );
        assertThat( refused.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( refused ).get( "error" ).asText() ).isEqualTo( "invalid_scope" );
        assertThat( body( refused ).get( "error_description" ).asText() ).startsWith( "Unknown scope [content:write]" );

        final JsonNode issued = body( handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read profile" ) ), IDP, "device/code",
            settings ) );
        approve( settings, issued.get( "user_code" ).asText(), "content:read", "profile", "admin" );
        assertThat( lastContextOfConfirm ).containsEntry( "unrestricted", false );
        @SuppressWarnings("unchecked") final List<Map<String, Object>> shown = (List<Map<String, Object>>) lastContextOfConfirm.get( "scope" );
        assertThat( shown ).hasSize( 2 );
        assertThat( shown.getFirst() ).containsEntry( "id", "content:read" )
            .containsEntry( "title", "content:read" )
            .doesNotContainKey( "description" )
            .containsEntry( "roles", List.of( "role:cms.cm.app", "role:cms.project.site.viewer" ) )
            .containsEntry( "required", false );
        assertThat( shown.get( 1 ) ).containsEntry( "id", "profile" ).containsEntry( "roles", List.of() );

        handler.handle( request( HttpMethod.POST, "token",
                                 Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", issued.get( "device_code" ).asText(),
                                         "client_id", "cli" ) ), IDP, "token", UNRESTRICTED );
        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getRestriction() ).isEqualTo(
            RoleRestriction.to( PrincipalKey.ofRole( "cms.cm.app" ), PrincipalKey.ofRole( "cms.project.site.viewer" ) ) );
        assertThat( grant.getValue().getScope() ).containsExactly( "content:read", "profile" );
    }

    @Test
    void a_restricted_sign_in_grants_no_more_than_its_own_roles()
        throws Exception
    {
        final VirtualHostIdProvider settings =
            withScopes( Map.of( "content:read", PrincipalKeys.from( "role:cms.cm.app", "role:cms.project.site.viewer" ) ) );
        final JsonNode issued = body( handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read" ) ), IDP, "device/code",
            settings ) );
        final String userCode = issued.get( "user_code" ).asText();
        asUser( ALICE, RoleRestriction.to( PrincipalKey.ofRole( "cms.cm.app" ) ), () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) ), IDP, "device", settings );
            return handler.handle( request( HttpMethod.POST, "device",
                                            Map.of( "user_code", userCode, "approve", "true", "scope", "content:read", "_csrf",
                                                    (String) lastContext.get( "csrf" ) ) ), IDP, "device", settings );
        } );
        handler.handle( request( HttpMethod.POST, "token",
                                 Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", issued.get( "device_code" ).asText(),
                                         "client_id", "cli" ) ), IDP, "token", settings );
        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getScope() ).containsExactly( "content:read" );
        assertThat( grant.getValue().getRestriction() ).as( "the viewer role the sign-in lacks is not granted" )
            .isEqualTo( RoleRestriction.to( PrincipalKey.ofRole( "cms.cm.app" ) ) );
    }

    @Test
    void a_restricted_sign_in_without_scopes_grants_its_own_roles_not_everything()
        throws Exception
    {
        final JsonNode issued =
            body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP, "device/code", UNRESTRICTED ) );
        final String userCode = issued.get( "user_code" ).asText();
        asUser( ALICE, RoleRestriction.to( RoleKeys.ADMIN_LOGIN ), () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) ), IDP, "device", UNRESTRICTED );
            return handler.handle( request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "true", "_csrf",
                                                                               (String) lastContext.get( "csrf" ) ) ), IDP, "device",
                                   UNRESTRICTED );
        } );
        handler.handle( request( HttpMethod.POST, "token",
                                 Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", issued.get( "device_code" ).asText(),
                                         "client_id", "cli" ) ), IDP, "token", UNRESTRICTED );
        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getRestriction() ).isEqualTo( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) );
    }

    @Test
    void required_scopes_cannot_be_dropped()
        throws Exception
    {
        when( securityService.getScopes( IDP ) ).thenReturn(
            Scopes.from( Scope.create().idProvider( IDP ).id( "content:read" ).roles( PrincipalKeys.from( "role:cms.cm.app" ) ).required( true ).build(),
                     Scope.create().idProvider( IDP ).id( "content:write" ).roles( PrincipalKeys.from( "role:cms.project.site.editor" ) ).build() ) );
        final VirtualHostIdProvider settings = UNRESTRICTED;
        final JsonNode issued = body( handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:write content:read" ) ), IDP,
            "device/code", settings ) );
        approve( settings, issued.get( "user_code" ).asText() );
        @SuppressWarnings("unchecked") final List<Map<String, Object>> shown = (List<Map<String, Object>>) lastContextOfConfirm.get( "scope" );
        assertThat( shown ).extracting( s -> s.get( "id" ), s -> s.get( "required" ) )
            .containsExactly( org.assertj.core.groups.Tuple.tuple( "content:write", false ),
                              org.assertj.core.groups.Tuple.tuple( "content:read", true ) );

        handler.handle( request( HttpMethod.POST, "token",
                                 Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", issued.get( "device_code" ).asText(),
                                         "client_id", "cli" ) ), IDP, "token", UNRESTRICTED );
        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getScope() ).containsExactly( "content:read" );
        assertThat( grant.getValue().getRestriction() ).isEqualTo( RoleRestriction.to( PrincipalKey.ofRole( "cms.cm.app" ) ) );
    }

    @Test
    void dropping_every_scope_leaves_no_roles()
        throws Exception
    {
        final VirtualHostIdProvider settings = withScopes( Map.of( "content:read", PrincipalKeys.from( "role:cms.cm.app" ) ) );
        final JsonNode issued = body( handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read" ) ), IDP, "device/code",
            settings ) );
        approve( settings, issued.get( "user_code" ).asText() );
        handler.handle( request( HttpMethod.POST, "token",
                                 Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", issued.get( "device_code" ).asText(),
                                         "client_id", "cli" ) ), IDP, "token", UNRESTRICTED );
        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getScope() ).isEmpty();
        assertThat( grant.getValue().getRestriction() ).as( "restricted, to nothing" ).isEqualTo( RoleRestriction.to() );
    }

    @Test
    void a_request_for_scopes_is_denied_once_the_id_provider_has_none()
        throws Exception
    {
        final VirtualHostIdProvider settings = withScopes( Map.of( "content:read", PrincipalKeys.from( "role:cms.cm.app" ) ) );
        final JsonNode issued = body( handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli", "scope", "content:read" ) ), IDP, "device/code",
            settings ) );
        when( securityService.getScopes( IDP ) ).thenReturn( Scopes.empty() );

        approve( settings, issued.get( "user_code" ).asText(), "content:read" );

        assertThat( lastContext ).as( "the page showed a choice that no longer exists" ).containsEntry( "approved", false );
        final JsonNode token = body( handler.handle( request( HttpMethod.POST, "token",
                                                              Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code",
                                                                      issued.get( "device_code" ).asText(), "client_id", "cli" ) ), IDP, "token",
                                                     UNRESTRICTED ) );
        assertThat( token.get( "error" ).asText() ).isEqualTo( "access_denied" );
        verify( grantService, never() ).create( any() );
    }

    @Test
    void audiences_are_what_the_vhosts_serve()
        throws Exception
    {
        final VirtualHost api = mock( VirtualHost.class );
        when( api.getAudience() ).thenReturn( Set.of( "api" ) );
        final VirtualHost mgmt = mock( VirtualHost.class );
        when( mgmt.getAudience() ).thenReturn( Set.of( "management" ) );
        final VirtualHost plain = mock( VirtualHost.class );
        when( plain.getAudience() ).thenReturn( Set.of() );
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( api, mgmt, plain ) );

        final PortalRequest unknown = request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) );
        unknown.getParams().put( "resource", "api" );
        unknown.getParams().put( "resource", "other" );
        final PortalResponse refused = handler.handle( unknown, IDP, "device/code", UNRESTRICTED );
        assertThat( body( refused ).get( "error" ).asText() ).isEqualTo( "invalid_target" );
        assertThat( body( refused ).get( "error_description" ).asText() ).contains( "other" ).doesNotContain( "api" );

        final PortalRequest one = request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) );
        one.getParams().put( "resource", "api" );
        final JsonNode issuedOne = body( handler.handle( one, IDP, "device/code", UNRESTRICTED ) );
        final JsonNode issuedAll = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                         "device/code", UNRESTRICTED ) );
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", issuedOne.get( "user_code" ).asText() ) ), IDP,
                            "device", UNRESTRICTED );
            assertThat( lastContext ).containsEntry( "audience", List.of( "api" ) );
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", issuedAll.get( "user_code" ).asText() ) ), IDP,
                            "device", UNRESTRICTED );
            @SuppressWarnings("unchecked") final List<String> all = (List<String>) lastContext.get( "audience" );
            assertThat( all ).as( "no resource: XP itself and everything served" ).containsExactlyInAnyOrder( "xp", "api", "management" );
            return null;
        } );
    }

    @Test
    void protocol_errors()
        throws Exception
    {
        assertThat( body( handler.handle( request( HttpMethod.POST, "device/code", Map.of() ), IDP, "device/code", UNRESTRICTED ) ).get( "error" )
                        .asText() ).isEqualTo( "invalid_request" );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token", Map.of( "grant_type", "password" ) ), IDP, "token", UNRESTRICTED ) )
                        .get( "error" )
                        .asText() ).isEqualTo( "unsupported_grant_type" );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token", Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT ) ),
                                          IDP, "token", UNRESTRICTED ) ).get( "error" ).asText() ).isEqualTo( "invalid_request" );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token",
                                                   Map.of( "grant_type", OAuthHandler.DEVICE_CODE_GRANT, "device_code", "nope",
                                                           "client_id", "cli" ) ), IDP, "token", UNRESTRICTED ) ).get( "error" ).asText() )
            .isEqualTo( "expired_token" );

        assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "device/code", Map.of() ), IDP, "device/code", UNRESTRICTED ) )
            .isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.METHOD_NOT_ALLOWED );
        assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "token", Map.of() ), IDP, "token", UNRESTRICTED ) )
            .isInstanceOf( WebException.class );
        assertThatThrownBy( () -> handler.handle( request( HttpMethod.DELETE, "device", Map.of() ), IDP, "device", UNRESTRICTED ) )
            .isInstanceOf( WebException.class );
    }
}
