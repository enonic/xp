package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.security.token.RedeemRefreshTokenParams;
import com.enonic.xp.session.Session;
import com.enonic.xp.shared.SharedMapService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.vhost.IdProviderFlow;

/**
 * The device login endpoints (RFC 8628) under {@code /_/idprovider/<idp>/}, served by XP for any
 * id provider's users:
 * <ul>
 * <li>{@code POST device/code}: the client asks for a device code and a user code.</li>
 * <li>{@code GET device}, {@code POST device}: the verification page, where a signed-in user of the
 * id provider enters the code and approves or denies. Unauthenticated, it answers 401 and the
 * vhost's interactive login takes over.</li>
 * <li>{@code POST token}: the client polls with the device code and, once approved, gets an access
 * token and a refresh token; later it refreshes with {@code grant_type=refresh_token}.</li>
 * </ul>
 * Approval creates a {@link Grant}; the tokens are issued from it when the client polls, so
 * nothing secret is kept in the pending state.
 * <p>
 * Every client is a public client: XP has no client registry yet, so {@code client_id} is whatever
 * the client calls itself. It is still required on every request, and a device code or refresh
 * token is honoured only for the client id it was issued to.
 * <p>
 * XP serves no page of its own. The id provider renders the verification page in
 * {@code deviceVerification(req)} from {@code req.deviceLogin} (see {@link DeviceLoginContext}). An
 * id provider without that function does not support device login, and its device endpoints
 * answer 404. The posted choice is checked here against what was requested and what may be
 * granted.
 */
@Component(service = DeviceLoginHandler.class)
public class DeviceLoginHandler
{
    static final String DEVICE_CODE_GRANT = "urn:ietf:params:oauth:grant-type:device_code";

    static final String REFRESH_TOKEN_GRANT = "refresh_token";

    /**
     * The request attribute holding the {@link DeviceLoginContext} data for the verification page.
     */
    public static final String CONTEXT_ATTRIBUTE = "com.enonic.xp.portal.idprovider.device.context";

    static final String VERIFICATION_FUNCTION = "deviceVerification";

    private static final String CSRF_SESSION_ATTRIBUTE = "com.enonic.xp.portal.idprovider.device.csrf";

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final SecureRandom RANDOM = new SecureRandom();

    private final GrantService grantService;

    private final AccessTokenService accessTokenService;

    private final IdProviderControllerService idProviderControllerService;

    private final DeviceCodeStore store;

    @Activate
    public DeviceLoginHandler( @Reference final GrantService grantService, @Reference final AccessTokenService accessTokenService,
                               @Reference final IdProviderControllerService idProviderControllerService,
                               @Reference final SharedMapService sharedMapService )
    {
        this( grantService, accessTokenService, idProviderControllerService, sharedMapService, Clock.systemUTC() );
    }

    DeviceLoginHandler( final GrantService grantService, final AccessTokenService accessTokenService,
                        final IdProviderControllerService idProviderControllerService, final SharedMapService sharedMapService,
                        final Clock clock )
    {
        this.grantService = grantService;
        this.accessTokenService = accessTokenService;
        this.idProviderControllerService = idProviderControllerService;
        this.store = new DeviceCodeStore( sharedMapService, clock );
    }

    /**
     * Whether the id provider supports device login, that is, has the verification page function.
     */
    public boolean supports( final IdProviderKey idProvider )
    {
        return idProviderControllerService.hasFunction( idProvider, VERIFICATION_FUNCTION );
    }

    /**
     * @param flows the id provider's flows on the vhost; empty means no restriction
     */
    public PortalResponse handle( final PortalRequest request, final IdProviderKey idProvider, final String endpoint,
                                  final Set<String> flows )
    {
        final HttpMethod method = request.getMethod();
        return switch ( endpoint )
        {
            case DeviceLoginEndpoints.DEVICE_CODE -> requirePost( method, () -> {
                requireSupport( idProvider );
                return deviceAuthorization( request, idProvider );
            } );
            case DeviceLoginEndpoints.TOKEN -> requirePost( method, () -> token( request, idProvider, flows ) );
            case DeviceLoginEndpoints.DEVICE -> switch ( method )
            {
                case GET, HEAD ->
                {
                    requireSupport( idProvider );
                    yield verificationPage( request, idProvider );
                }
                case POST ->
                {
                    requireSupport( idProvider );
                    yield verificationSubmit( request, idProvider );
                }
                default -> throw methodNotAllowed( method );
            };
            default -> throw WebException.notFound( "Not a valid idprovider url pattern" );
        };
    }

    private void requireSupport( final IdProviderKey idProvider )
    {
        if ( !supports( idProvider ) )
        {
            throw WebException.notFound( String.format( "'%s' id provider has no device login page (%s)", idProvider, VERIFICATION_FUNCTION ) );
        }
    }

    private static PortalResponse requirePost( final HttpMethod method, final java.util.function.Supplier<PortalResponse> handler )
    {
        if ( method != HttpMethod.POST )
        {
            throw methodNotAllowed( method );
        }
        return handler.get();
    }

    private static WebException methodNotAllowed( final HttpMethod method )
    {
        return new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
    }

    private PortalResponse deviceAuthorization( final PortalRequest request, final IdProviderKey idProvider )
    {
        final String clientId = clientId( request );
        if ( clientId == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing client_id" );
        }
        final Set<String> scope = scopes( param( request, "scope" ) );
        final Set<String> audience = audience( request );
        final DeviceCodeStore.Pending pending = store.create( idProvider, clientId, scope, audience );

        final String url = request.getUrl();
        final int query = url.indexOf( '?' );
        final String endpointUrl = query < 0 ? url : url.substring( 0, query );
        final String verificationUri = endpointUrl.substring( 0, endpointUrl.length() - "/code".length() );
        final String userCode = DeviceCodeStore.formatUserCode( pending.userCode() );

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "device_code", pending.deviceCode() );
        body.put( "user_code", userCode );
        body.put( "verification_uri", verificationUri );
        body.put( "verification_uri_complete", verificationUri + "?user_code=" + userCode );
        body.put( "expires_in", DeviceCodeStore.CODE_TTL_SECONDS );
        body.put( "interval", DeviceCodeStore.POLL_INTERVAL_SECONDS );
        return json( HttpStatus.OK, body );
    }

    private PortalResponse verificationPage( final PortalRequest request, final IdProviderKey idProvider )
    {
        requireInteractiveSignIn( request );
        final Optional<User> user = signedInUserOf( idProvider );
        if ( user.isEmpty() )
        {
            return notSignedIn( request, idProvider );
        }
        final String userCode = param( request, "user_code" );
        if ( userCode == null || userCode.isBlank() )
        {
            return render( request, idProvider, new DeviceLoginContext.Enter( idProvider, user.get(), null ) );
        }
        final Optional<DeviceCodeStore.Pending> pending = store.findByUserCode( idProvider, userCode );
        if ( pending.isEmpty() || pending.get().status() != DeviceCodeStore.Status.PENDING )
        {
            return render( request, idProvider, new DeviceLoginContext.Enter( idProvider, user.get(),
                                                                                "The code " + userCode.trim() + " is invalid or has expired." ) );
        }
        final DeviceCodeStore.Pending p = pending.get();
        return render( request, idProvider,
                       DeviceLoginContext.Confirm.of( idProvider, user.get(), DeviceCodeStore.formatUserCode( p.userCode() ), p.clientId(),
                                                      p.scope(), p.audience(), csrfToken() ) );
    }

    private PortalResponse verificationSubmit( final PortalRequest request, final IdProviderKey idProvider )
    {
        requireInteractiveSignIn( request );
        final Optional<User> user = signedInUserOf( idProvider );
        if ( user.isEmpty() )
        {
            return notSignedIn( request, idProvider );
        }
        if ( !csrfToken().equals( param( request, "_csrf" ) ) )
        {
            throw WebException.forbidden( "Invalid or missing form token" );
        }
        final String userCode = param( request, "user_code" );
        final boolean approve = "true".equals( param( request, "approve" ) );
        if ( userCode == null || !store.resolve( idProvider, userCode, approve, user.get().getKey() ) )
        {
            return render( request, idProvider,
                           new DeviceLoginContext.Enter( idProvider, user.get(), "This code is invalid or has expired." ) );
        }
        return render( request, idProvider, new DeviceLoginContext.Done( idProvider, user.get(), approve ) );
    }

    /**
     * Renders the page through the id provider's {@code deviceVerification} function.
     */
    private PortalResponse render( final PortalRequest request, final IdProviderKey idProvider, final DeviceLoginContext context )
    {
        if ( request.getRawRequest() != null )
        {
            request.getRawRequest().setAttribute( CONTEXT_ATTRIBUTE, context.toMap() );
        }
        try
        {
            final PortalResponse page = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                 .idProviderKey( idProvider )
                                                                                 .functionName( VERIFICATION_FUNCTION )
                                                                                 .portalRequest( request )
                                                                                 .build() );
            if ( page == null )
            {
                requireSupport( idProvider );
                throw WebException.internalServerError( String.format( "'%s' id provider's %s returned no response", idProvider,
                                                                        VERIFICATION_FUNCTION ) );
            }
            return page;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private PortalResponse token( final PortalRequest request, final IdProviderKey idProvider, final Set<String> flows )
    {
        final String grantType = param( request, "grant_type" );
        if ( DEVICE_CODE_GRANT.equals( grantType ) )
        {
            if ( !flows.isEmpty() && !flows.contains( IdProviderFlow.DEVICE ) )
            {
                return oauthError( HttpStatus.BAD_REQUEST, "unauthorized_client", "The device flow is disabled here" );
            }
            requireSupport( idProvider );
            return deviceCodeGrant( request, idProvider );
        }
        if ( REFRESH_TOKEN_GRANT.equals( grantType ) )
        {
            return refreshTokenGrant( request, idProvider );
        }
        return oauthError( HttpStatus.BAD_REQUEST, "unsupported_grant_type", null );
    }

    private PortalResponse deviceCodeGrant( final PortalRequest request, final IdProviderKey idProvider )
    {
        final String deviceCode = param( request, "device_code" );
        if ( deviceCode == null || deviceCode.isBlank() )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing device_code" );
        }
        final String clientId = clientId( request );
        if ( clientId == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing client_id" );
        }
        return switch ( store.poll( idProvider, deviceCode, clientId ) )
        {
            case DeviceCodeStore.Outcome.Waiting _ -> oauthError( HttpStatus.BAD_REQUEST, "authorization_pending", null );
            case DeviceCodeStore.Outcome.SlowDown _ -> oauthError( HttpStatus.BAD_REQUEST, "slow_down", null );
            case DeviceCodeStore.Outcome.Denied _ -> oauthError( HttpStatus.BAD_REQUEST, "access_denied", null );
            case DeviceCodeStore.Outcome.Expired _ -> oauthError( HttpStatus.BAD_REQUEST, "expired_token", null );
            case DeviceCodeStore.Outcome.ClientMismatch _ -> oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "client_id mismatch" );
            case DeviceCodeStore.Outcome.Approved( var pending ) ->
            {
                final GrantWithRefreshToken grant = grantService.create( CreateGrantParams.create()
                                                                             .subject( pending.subject() )
                                                                             .clientId( pending.clientId() )
                                                                             .scope( pending.scope() )
                                                                             .audience( tokenAudience( pending ) )
                                                                             .build() );
                yield tokenResponse( grant );
            }
        };
    }

    private PortalResponse refreshTokenGrant( final PortalRequest request, final IdProviderKey idProvider )
    {
        final String refreshToken = param( request, "refresh_token" );
        if ( refreshToken == null || refreshToken.isBlank() )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing refresh_token" );
        }
        final String clientId = clientId( request );
        if ( clientId == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing client_id" );
        }
        final Optional<GrantWithRefreshToken> redeemed = grantService.redeem( RedeemRefreshTokenParams.create()
                                                                                  .refreshToken( refreshToken.trim() )
                                                                                  .idProvider( idProvider )
                                                                                  .clientId( clientId )
                                                                                  .build() );
        if ( redeemed.isEmpty() )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "Invalid or expired refresh_token" );
        }
        return tokenResponse( redeemed.get() );
    }

    private PortalResponse tokenResponse( final GrantWithRefreshToken grant )
    {
        final Grant g = grant.grant();
        final AccessToken accessToken = accessTokenService.issue( IssueAccessTokenParams.create()
                                                                      .subject( g.getSubject() )
                                                                      .audience( g.getAudience() )
                                                                      .scope( g.getScope() )
                                                                      .clientId( g.getClientId() )
                                                                      .grantId( g.getId() )
                                                                      .build() );
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "access_token", accessToken.value() );
        body.put( "token_type", "Bearer" );
        body.put( "expires_in", accessToken.claims().getExpiresAt().getEpochSecond() - accessToken.claims().getIssuedAt().getEpochSecond() );
        body.put( "refresh_token", grant.refreshToken() );
        if ( !g.getScope().isEmpty() )
        {
            body.put( "scope", String.join( " ", g.getScope() ) );
        }
        return json( HttpStatus.OK, body );
    }

    /**
     * The signed-in user, if a user of this id provider. An id provider's device endpoints issue
     * tokens for its own users only.
     */
    private static Optional<User> signedInUserOf( final IdProviderKey idProvider )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            return Optional.empty();
        }
        final User user = authInfo.getUser();
        return idProvider.equals( user.getKey().getIdProviderKey() ) ? Optional.of( user ) : Optional.empty();
    }

    /**
     * Approval needs a user at a browser. A request authenticated by an access token is a client's,
     * and a client must not turn a token into a grant, nor a restricted token into a broader one.
     */
    private static void requireInteractiveSignIn( final PortalRequest request )
    {
        if ( request.getRawRequest() != null && request.getRawRequest().getAttribute( AccessTokenFilter.CLAIMS_ATTRIBUTE ) != null )
        {
            throw WebException.forbidden( "Device sign-in must be approved from a signed-in browser, not with an access token" );
        }
    }

    private PortalResponse notSignedIn( final PortalRequest request, final IdProviderKey idProvider )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return render( request, idProvider, new DeviceLoginContext.WrongAccount( idProvider, authInfo.getUser() ) );
        }
        return PortalResponse.create().status( HttpStatus.UNAUTHORIZED ).build();
    }

    /**
     * A per-session token the approval form must echo, so another site cannot submit an approval
     * for a user code it knows.
     */
    private static String csrfToken()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session == null )
        {
            throw WebException.forbidden( "Device sign-in requires a session" );
        }
        String token = (String) session.getAttribute( CSRF_SESSION_ATTRIBUTE );
        if ( token == null )
        {
            final byte[] bytes = new byte[24];
            RANDOM.nextBytes( bytes );
            token = Base64.getUrlEncoder().withoutPadding().encodeToString( bytes );
            session.setAttribute( CSRF_SESSION_ATTRIBUTE, token );
        }
        return token;
    }

    private static String param( final PortalRequest request, final String name )
    {
        final Collection<String> values = request.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    /**
     * The client id the request carries, trimmed, or null if missing or blank.
     */
    private static String clientId( final PortalRequest request )
    {
        final String clientId = param( request, "client_id" );
        return clientId == null || clientId.isBlank() ? null : clientId.trim();
    }

    private static Set<String> scopes( final String scope )
    {
        final Set<String> scopes = new LinkedHashSet<>();
        if ( scope != null )
        {
            for ( String s : scope.split( " " ) )
            {
                if ( !s.isEmpty() )
                {
                    scopes.add( s );
                }
            }
        }
        return scopes;
    }

    /**
     * The audience the grant's tokens name: the resources the client asked for, or XP's issuer name
     * if it asked for none.
     */
    private Set<String> tokenAudience( final DeviceCodeStore.Pending pending )
    {
        return pending.audience().isEmpty() ? Set.of( accessTokenService.getIssuer() ) : pending.audience();
    }

    /**
     * The {@code resource} parameters (RFC 8707), possibly repeated: the audiences the client wants.
     * None means a token for XP itself.
     */
    private static Set<String> audience( final PortalRequest request )
    {
        final Set<String> audience = new LinkedHashSet<>();
        for ( String value : request.getParams().get( "resource" ) )
        {
            if ( value != null && !value.isBlank() )
            {
                audience.add( value.trim() );
            }
        }
        return audience;
    }

    private static PortalResponse oauthError( final HttpStatus status, final String error, final String description )
    {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "error", error );
        if ( description != null )
        {
            body.put( "error_description", description );
        }
        return json( status, body );
    }

    private static PortalResponse json( final HttpStatus status, final Map<String, Object> body )
    {
        try
        {
            return PortalResponse.create()
                .status( status )
                .contentType( MediaType.JSON_UTF_8 )
                .body( MAPPER.writeValueAsString( body ) )
                .header( "Cache-Control", "no-store" )
                .header( "Pragma", "no-cache" )
                .build();
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
