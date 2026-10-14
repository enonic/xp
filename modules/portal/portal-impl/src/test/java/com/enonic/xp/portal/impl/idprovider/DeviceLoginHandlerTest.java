package com.enonic.xp.portal.impl.idprovider;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
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
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The id provider's {@code deviceVerification} function is stubbed to echo the action it was asked
 * to render and to keep the context it received, so the tests assert on the data handed to the
 * page rather than on markup, which is the id provider's.
 */
class DeviceLoginHandlerTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IDP, "alice" );

    private static final ObjectMapper JSON = new ObjectMapper();

    private final GrantService grantService = mock( GrantService.class );

    private final AccessTokenService accessTokenService = mock( AccessTokenService.class );

    private final IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );

    private Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private Map<String, Object> lastContext;

    private DeviceLoginHandler handler;

    @BeforeEach
    void setUp()
        throws Exception
    {
        when( idProviderControllerService.hasFunction( any(), eq( DeviceLoginHandler.VERIFICATION_FUNCTION ) ) ).thenReturn( true );
        when( idProviderControllerService.execute( any() ) ).thenAnswer( i -> {
            final IdProviderControllerExecutionParams params = i.getArgument( 0 );
            @SuppressWarnings("unchecked") final Map<String, Object> context =
                (Map<String, Object>) params.getPortalRequest().getRawRequest().getAttribute( DeviceLoginHandler.CONTEXT_ATTRIBUTE );
            lastContext = context;
            return PortalResponse.create()
                .status( HttpStatus.OK )
                .contentType( MediaType.HTML_UTF_8 )
                .body( "page:" + context.get( "action" ) )
                .build();
        } );
        handler = new DeviceLoginHandler( grantService, accessTokenService, idProviderControllerService, new InMemorySharedMapService(),
                                          new Clock()
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
                .sid( "sid-1" )
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
        when( accessTokenService.issue( any() ) ).thenAnswer( i -> {
            final IssueAccessTokenParams params = i.getArgument( 0 );
            final AccessTokenClaims claims = AccessTokenClaims.create()
                .issuer( "xp" )
                .subject( params.getSubject() )
                .audience( params.getAudience() )
                .scope( params.getScope() )
                .restriction( params.getRestriction() )
                .clientId( params.getClientId() )
                .grantId( params.getGrantId() )
                .sid( params.getSid() )
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
        final User user = User.create().key( key ).login( key.getId() ).displayName( "Alice" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        return ContextBuilder.create().authInfo( authInfo ).build().callWith( () -> {
            ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
            return call.call();
        } );
    }

    private String approve( final String userCode, final String... keptScopes )
    {
        return asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) ), IDP, "device", Set.of() );
            final PortalRequest approval = request( HttpMethod.POST, "device",
                                                    Map.of( "user_code", userCode, "approve", "true", "_csrf",
                                                            (String) lastContext.get( "csrf" ) ) );
            for ( String scope : keptScopes )
            {
                approval.getParams().put( "scope", scope );
            }
            return (String) handler.handle( approval, IDP, "device", Set.of() ).getBody();
        } );
    }

    @Test
    void full_device_flow()
        throws Exception
    {
        final PortalResponse codes = handler.handle(
            request( HttpMethod.POST, "device/code", Map.of( "client_id", "my-cli", "scope", "content:read offline_access" ) ), IDP,
            "device/code", Set.of() );
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
                                            Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code", deviceCode,
                                                    "client_id", "my-cli" ) );
        final PortalResponse pending = handler.handle( poll, IDP, "token", Set.of() );
        assertThat( pending.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( pending ).get( "error" ).asText() ).isEqualTo( "authorization_pending" );
        assertThat( body( handler.handle( poll, IDP, "token", Set.of() ) ).get( "error" ).asText() ).isEqualTo( "slow_down" );

        final PortalRequest page = request( HttpMethod.GET, "device", Map.of( "user_code", userCode ) );
        assertThat( handler.handle( page, IDP, "device", Set.of() ).getStatus() ).isEqualTo( HttpStatus.UNAUTHORIZED );
        verify( idProviderControllerService, Mockito.never() ).execute( any() );

        final String[] csrf = new String[1];
        asAlice( () -> {
            final PortalResponse form = handler.handle( page, IDP, "device", Set.of() );
            assertThat( form.getBody() ).isEqualTo( "page:confirm" );
            assertThat( lastContext ).containsEntry( "idProvider", "system" ).containsEntry( "userCode", userCode )
                .containsEntry( "clientId", "my-cli" ).containsEntry( "audience", List.of() );
            assertThat( scopeIds( lastContext ) ).containsExactly( "content:read", "offline_access" );
            assertThat( user( lastContext ) ).containsEntry( "key", ALICE.toString() ).containsEntry( "displayName", "Alice" );
            csrf[0] = (String) lastContext.get( "csrf" );
            assertThat( csrf[0] ).isNotBlank();

            assertThatThrownBy( () -> handler.handle(
                request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "true" ) ), IDP, "device", Set.of() ) )
                .isInstanceOf( WebException.class );

            final PortalRequest approval =
                request( HttpMethod.POST, "device", Map.of( "user_code", userCode, "approve", "true", "_csrf", csrf[0] ) );
            final PortalResponse done = handler.handle( approval, IDP, "device", Set.of() );
            assertThat( done.getBody() ).isEqualTo( "page:done" );
            assertThat( lastContext ).containsEntry( "approved", true );
            return null;
        } );

        now = now.plusSeconds( 10 );
        final PortalResponse tokens = handler.handle( poll, IDP, "token", Set.of() );
        assertThat( tokens.getStatus() ).isEqualTo( HttpStatus.OK );
        final JsonNode tokenBody = body( tokens );
        assertThat( tokenBody.get( "access_token" ).asText() ).isEqualTo( "access-1" );
        assertThat( tokenBody.get( "token_type" ).asText() ).isEqualTo( "Bearer" );
        assertThat( tokenBody.get( "expires_in" ).asLong() ).isEqualTo( 3600 );
        assertThat( tokenBody.get( "refresh_token" ).asText() ).isEqualTo( "refresh-1" );
        assertThat( tokenBody.get( "scope" ).asText() ).isEqualTo( "content:read offline_access" );

        final ArgumentCaptor<CreateGrantParams> grant = ArgumentCaptor.forClass( CreateGrantParams.class );
        verify( grantService ).create( grant.capture() );
        assertThat( grant.getValue().getSubject() ).isEqualTo( ALICE );
        assertThat( grant.getValue().getClientId() ).isEqualTo( "my-cli" );
        assertThat( grant.getValue().getScope() ).containsExactly( "content:read", "offline_access" );
        assertThat( grant.getValue().getRestriction() ).isEqualTo( PrincipalRestriction.none() );

        final ArgumentCaptor<IssueAccessTokenParams> token = ArgumentCaptor.forClass( IssueAccessTokenParams.class );
        verify( accessTokenService ).issue( token.capture() );
        assertThat( token.getValue().getGrantId() ).isEqualTo( "grant-1" );
        assertThat( token.getValue().getSid() ).isEqualTo( "sid-1" );
        assertThat( token.getValue().getSubject() ).isEqualTo( ALICE );

        assertThat( body( handler.handle( poll, IDP, "token", Set.of() ) ).get( "error" ).asText() ).isEqualTo( "expired_token" );
    }

    @Test
    void deny()
        throws Exception
    {
        final JsonNode issued = body( handler.handle( request( HttpMethod.POST, "device/code", Map.of( "client_id", "cli" ) ), IDP,
                                                      "device/code", Set.of() ) );
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", issued.get( "user_code" ).asText() ) ), IDP, "device",
                            Set.of() );
            final PortalResponse done = handler.handle( request( HttpMethod.POST, "device",
                                                                 Map.of( "user_code", issued.get( "user_code" ).asText(), "approve",
                                                                         "false", "_csrf", (String) lastContext.get( "csrf" ) ) ), IDP,
                                                        "device", Set.of() );
            assertThat( done.getBody() ).isEqualTo( "page:done" );
            assertThat( lastContext ).containsEntry( "approved", false );
            return null;
        } );
        final PortalResponse polled = handler.handle( request( HttpMethod.POST, "token",
                                                               Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code",
                                                                       issued.get( "device_code" ).asText() ) ), IDP, "token", Set.of() );
        assertThat( body( polled ).get( "error" ).asText() ).isEqualTo( "access_denied" );
        verifyNoInteractions( grantService );
    }

    @Test
    void verification_page_without_or_with_bad_code()
    {
        asAlice( () -> {
            handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", Set.of() );
            assertThat( lastContext ).containsEntry( "action", "enter" ).doesNotContainKey( "error" );
            handler.handle( request( HttpMethod.GET, "device", Map.of( "user_code", "NOPE-NOPE" ) ), IDP, "device", Set.of() );
            assertThat( lastContext ).containsEntry( "action", "enter" );
            assertThat( (String) lastContext.get( "error" ) ).contains( "NOPE-NOPE" ).contains( "invalid or has expired" );
            return null;
        } );
    }

    @Test
    void user_of_another_id_provider_cannot_approve()
    {
        asUser( PrincipalKey.ofUser( IdProviderKey.from( "other" ), "bob" ), () -> {
            final PortalResponse response = handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", Set.of() );
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
                                                  "device/code", Set.of() ) ).isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.NOT_FOUND );
        asAlice( () -> {
            assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "device", Map.of() ), IDP, "device", Set.of() ) )
                .isInstanceOf( WebException.class );
            return null;
        } );
        assertThatThrownBy( () -> handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code", "x" ) ), IDP,
            "token", Set.of() ) ).isInstanceOf( WebException.class );

        when( grantService.redeem( "bad" ) ).thenReturn( Optional.empty() );
        final PortalResponse refresh = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad" ) ), IDP, "token", Set.of() );
        assertThat( body( refresh ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );
        verify( idProviderControllerService, Mockito.never() ).execute( any() );
    }

    @Test
    void page_function_is_called_with_the_id_provider_and_the_request()
        throws Exception
    {
        asAlice( () -> {
            final PortalRequest page = request( HttpMethod.GET, "device", Map.of() );
            handler.handle( page, IDP, "device", Set.of() );
            final ArgumentCaptor<IdProviderControllerExecutionParams> params =
                ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
            verify( idProviderControllerService ).execute( params.capture() );
            assertThat( params.getValue().getFunctionName() ).isEqualTo( "deviceVerification" );
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
                                                      "device/code", Set.of() ) );
        approve( issued.get( "user_code" ).asText() );
        final PortalResponse polled = handler.handle( request( HttpMethod.POST, "token",
                                                               Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code",
                                                                       issued.get( "device_code" ).asText(), "client_id", "other" ) ),
                                                      IDP, "token", Set.of() );
        assertThat( body( polled ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );
    }

    @Test
    void refresh()
        throws Exception
    {
        final Grant grant = Grant.create()
            .id( "grant-1" )
            .subject( ALICE )
            .sid( "sid-1" )
            .clientId( "cli" )
            .scope( Set.of( "content:read" ) )
            .audience( Set.of( "api" ) )
            .restriction( PrincipalRestriction.to( RoleKeys.ADMIN_LOGIN ) )
            .createdAt( now )
            .rotatedAt( now )
            .expiresAt( now.plusSeconds( 3600 ) )
            .build();
        when( grantService.redeem( "refresh-1" ) ).thenReturn( Optional.of( new GrantWithRefreshToken( grant, "refresh-2" ) ) );
        when( grantService.redeem( "bad" ) ).thenReturn( Optional.empty() );

        final PortalResponse ok = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "cli" ) ),
            IDP, "token", Set.of() );
        assertThat( ok.getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( body( ok ).get( "refresh_token" ).asText() ).isEqualTo( "refresh-2" );
        assertThat( body( ok ).get( "access_token" ).asText() ).isEqualTo( "access-1" );

        final ArgumentCaptor<IssueAccessTokenParams> token = ArgumentCaptor.forClass( IssueAccessTokenParams.class );
        verify( accessTokenService ).issue( token.capture() );
        assertThat( token.getValue().getRestriction() ).isEqualTo( PrincipalRestriction.to( RoleKeys.ADMIN_LOGIN ) );
        assertThat( token.getValue().getAudience() ).containsExactly( "api" );
        assertThat( token.getValue().getGrantId() ).isEqualTo( "grant-1" );
        assertThat( token.getValue().getSid() ).isEqualTo( "sid-1" );

        final PortalResponse bad = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad" ) ), IDP, "token", Set.of() );
        assertThat( bad.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( body( bad ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );

        final PortalResponse mismatch = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1", "client_id", "x" ) ),
            IDP, "token", Set.of() );
        assertThat( body( mismatch ).get( "error" ).asText() ).isEqualTo( "invalid_grant" );

        final PortalResponse otherIdp = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "refresh-1" ) ),
            IdProviderKey.from( "other" ), "token", Set.of() );
        assertThat( body( otherIdp ).get( "error" ).asText() ).as( "a grant of another id provider's user" ).isEqualTo( "invalid_grant" );
    }

    @Test
    void device_code_grant_needs_the_device_flow_but_refresh_does_not()
        throws Exception
    {
        final PortalResponse refused = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code", "x" ) ), IDP,
            "token", Set.of( "native" ) );
        assertThat( body( refused ).get( "error" ).asText() ).isEqualTo( "unauthorized_client" );

        when( grantService.redeem( "bad" ) ).thenReturn( Optional.empty() );
        final PortalResponse refresh = handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", "refresh_token", "refresh_token", "bad" ) ), IDP, "token",
            Set.of( "native" ) );
        assertThat( body( refresh ).get( "error" ).asText() ).as( "reaches the grant service" ).isEqualTo( "invalid_grant" );
    }

    @Test
    void protocol_errors()
        throws Exception
    {
        assertThat( body( handler.handle( request( HttpMethod.POST, "device/code", Map.of() ), IDP, "device/code", Set.of() ) ).get( "error" )
                        .asText() ).isEqualTo( "invalid_request" );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token", Map.of( "grant_type", "password" ) ), IDP, "token", Set.of() ) )
                        .get( "error" )
                        .asText() ).isEqualTo( "unsupported_grant_type" );
        assertThat( body( handler.handle( request( HttpMethod.POST, "token", Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT ) ),
                                          IDP, "token", Set.of() ) ).get( "error" ).asText() ).isEqualTo( "invalid_request" );
        assertThat( body( handler.handle(
            request( HttpMethod.POST, "token", Map.of( "grant_type", DeviceLoginHandler.DEVICE_CODE_GRANT, "device_code", "nope" ) ), IDP,
            "token", Set.of() ) ).get( "error" ).asText() ).isEqualTo( "expired_token" );

        assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "device/code", Map.of() ), IDP, "device/code", Set.of() ) )
            .isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.METHOD_NOT_ALLOWED );
        assertThatThrownBy( () -> handler.handle( request( HttpMethod.GET, "token", Map.of() ), IDP, "token", Set.of() ) )
            .isInstanceOf( WebException.class );
        assertThatThrownBy( () -> handler.handle( request( HttpMethod.DELETE, "device", Map.of() ), IDP, "device", Set.of() ) )
            .isInstanceOf( WebException.class );
    }
}
