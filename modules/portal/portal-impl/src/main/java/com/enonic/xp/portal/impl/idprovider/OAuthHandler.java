package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.jspecify.annotations.Nullable;
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
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.security.token.RedeemRefreshTokenParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.session.Session;
import com.enonic.xp.shared.SharedMapService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

import static java.util.Objects.requireNonNull;

/**
 * Serves the OAuth endpoints under {@code /_/idprovider/<idp>/} for an id provider's users:
 * <ul>
 * <li>{@code POST device/code}: a device code and a user code (RFC 8628).</li>
 * <li>{@code GET device}, {@code POST device}: the consent page for a device code, rendered by the id
 * provider; 401 when unauthenticated, so the vhost's interactive login takes over.</li>
 * <li>{@code POST token}: tokens for a device code or a refresh token.</li>
 * </ul>
 * What may be asked for and granted is checked here: against the id provider's scopes, the
 * client's record when it has one, the audiences XP serves, and the choice the user posted from
 * the page (see {@link OAuthConsentContext}). An id provider without {@code oauthConsent}
 * answers 404 on these endpoints.
 */
@Component(service = OAuthHandler.class)
public class OAuthHandler
{
    static final String DEVICE_CODE_GRANT = GrantTypes.DEVICE_CODE;

    static final String REFRESH_TOKEN_GRANT = GrantTypes.REFRESH_TOKEN;

    /**
     * The request attribute holding the {@link OAuthConsentContext} data for the consent page.
     */
    public static final String CONTEXT_ATTRIBUTE = "com.enonic.xp.portal.idprovider.oauth.consent";

    static final String CONSENT_FUNCTION = "oauthConsent";

    private static final String CSRF_SESSION_ATTRIBUTE = "com.enonic.xp.portal.idprovider.device.csrf";

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * What only the posted form may say on the verification submit.
     */
    private static final Set<String> FORM_ONLY = Set.of( "approve", "scope" );

    private final GrantService grantService;

    private final AccessTokenService accessTokenService;

    private final SecurityService securityService;

    private final VirtualHostService virtualHostService;

    private final IdProviderControllerService idProviderControllerService;

    private final DeviceCodeStore store;

    @Activate
    public OAuthHandler( @Reference final GrantService grantService, @Reference final AccessTokenService accessTokenService,
                               @Reference final SecurityService securityService, @Reference final VirtualHostService virtualHostService,
                               @Reference final IdProviderControllerService idProviderControllerService,
                               @Reference final SharedMapService sharedMapService )
    {
        this( grantService, accessTokenService, securityService, virtualHostService, idProviderControllerService, sharedMapService,
              Clock.systemUTC() );
    }

    OAuthHandler( final GrantService grantService, final AccessTokenService accessTokenService, final SecurityService securityService,
                        final VirtualHostService virtualHostService, final IdProviderControllerService idProviderControllerService,
                        final SharedMapService sharedMapService, final Clock clock )
    {
        this.grantService = grantService;
        this.accessTokenService = accessTokenService;
        this.securityService = securityService;
        this.virtualHostService = virtualHostService;
        this.idProviderControllerService = idProviderControllerService;
        this.store = new DeviceCodeStore( sharedMapService, clock );
    }

    /**
     * Whether the id provider supports device login, that is, has the consent page function.
     */
    public boolean supports( final IdProviderKey idProvider )
    {
        return idProviderControllerService.hasFunction( idProvider, CONSENT_FUNCTION );
    }

    /**
     * @param settings the id provider's settings on the vhost: its flows (empty means no restriction)
     */
    public PortalResponse handle( final PortalRequest request, final IdProviderKey idProvider, final String endpoint,
                                  final VirtualHostIdProvider settings )
    {
        final HttpMethod method = request.getMethod();
        return switch ( endpoint )
        {
            case OAuthEndpoints.DEVICE_CODE -> requirePost( method, () -> {
                requireSupport( idProvider );
                return deviceAuthorization( request, idProvider );
            } );
            case OAuthEndpoints.TOKEN -> requirePost( method, () -> token( request, idProvider, settings ) );
            case OAuthEndpoints.DEVICE -> switch ( method )
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
            throw WebException.notFound( String.format( "'%s' id provider has no device login page (%s)", idProvider, CONSENT_FUNCTION ) );
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
        final Optional<Client> client = securityService.getClient( idProvider, clientId );
        final PortalResponse refused = client.map( c -> refusal( c, GrantTypes.DEVICE_CODE ) ).orElse( null );
        if ( refused != null )
        {
            return refused;
        }
        final Scopes known = securityService.getScopes( idProvider );
        final Set<String> scope = withinClient( scopes( param( request, "scope" ) ), client.map( Client::getScope ).orElse( Set.of() ) );
        if ( scope == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_scope", "Scope beyond the client's: " + client.orElseThrow().getScope() );
        }
        if ( known.isEmpty() )
        {
            if ( !scope.isEmpty() )
            {
                return oauthError( HttpStatus.BAD_REQUEST, "invalid_scope",
                                   "The id provider has no scopes; ask for none" );
            }
        }
        else
        {
            if ( scope.isEmpty() && client.isEmpty() )
            {
                return oauthError( HttpStatus.BAD_REQUEST, "invalid_scope", "scope is required; known scopes: " + known.getIds() );
            }
            final Set<String> unknown = new LinkedHashSet<>( scope );
            unknown.removeAll( known.getIds() );
            if ( !unknown.isEmpty() )
            {
                return oauthError( HttpStatus.BAD_REQUEST, "invalid_scope",
                                   "Unknown scope " + unknown + "; known scopes: " + known.getIds() );
            }
        }
        final Set<String> allowedAudience = servedAudiences();
        final Set<String> requestedAudience =
            withinClient( audience( request ), client.map( Client::getResource ).orElse( Set.of() ) );
        if ( requestedAudience == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_target", "Resource beyond the client's: " + client.orElseThrow().getResource() );
        }
        final Set<String> audience;
        if ( requestedAudience.isEmpty() )
        {
            // RFC 8707: no resource named, so the token is for XP itself, by its issuer name,
            // which a vhost declaring no audience accepts, and for everything XP serves.
            audience = new LinkedHashSet<>();
            audience.add( accessTokenService.getIssuer() );
            audience.addAll( allowedAudience );
        }
        else if ( allowedAudience.containsAll( requestedAudience ) )
        {
            audience = requestedAudience;
        }
        else
        {
            final Set<String> unknown = new LinkedHashSet<>( requestedAudience );
            unknown.removeAll( allowedAudience );
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_target", "Unknown resource " + unknown );
        }
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
            return render( request, idProvider, new OAuthConsentContext.Enter( idProvider, user.get(), null ) );
        }
        final Optional<DeviceCodeStore.Pending> pending = store.findByUserCode( idProvider, userCode );
        if ( pending.isEmpty() || pending.get().status() != DeviceCodeStore.Status.PENDING )
        {
            return render( request, idProvider, new OAuthConsentContext.Enter( idProvider, user.get(), OAuthConsentContext.INVALID_CODE ) );
        }
        final DeviceCodeStore.Pending p = pending.get();
        if ( !store.show( idProvider, userCode, user.get().getKey() ) )
        {
            return render( request, idProvider, new OAuthConsentContext.Enter( idProvider, user.get(), OAuthConsentContext.INVALID_CODE ) );
        }
        return render( request, idProvider,
                       OAuthConsentContext.Confirm.of( idProvider, user.get(), DeviceCodeStore.formatUserCode( p.userCode() ), p.clientId(),
                                                      securityService.getClient( idProvider, p.clientId() ).orElse( null ), p.scope(), p.audience(),
                                                      securityService.getScopes( idProvider ), csrfToken( user.get().getKey() ) ) );
    }

    private PortalResponse verificationSubmit( final PortalRequest request, final IdProviderKey idProvider )
    {
        requireInteractiveSignIn( request );
        final Optional<User> user = signedInUserOf( idProvider );
        if ( user.isEmpty() )
        {
            return notSignedIn( request, idProvider );
        }
        if ( !csrfToken( user.get().getKey() ).equals( param( request, "_csrf" ) ) )
        {
            throw WebException.forbidden( "Invalid or missing form token" );
        }
        // The decision is the form's alone. Query and form parameters arrive merged, query first, so a
        // link that opened the page could otherwise carry the choice.
        if ( queryNames( request.getRawRequest().getQueryString() ).stream().anyMatch( FORM_ONLY::contains ) )
        {
            throw WebException.badRequest( "approve and scope are form fields, not query parameters" );
        }
        final String userCode = param( request, "user_code" );
        final boolean approve = "true".equals( param( request, "approve" ) );
        final Optional<DeviceCodeStore.Pending> pending = userCode == null ? Optional.empty() : store.findByUserCode( idProvider, userCode );
        if ( pending.isEmpty() )
        {
            return render( request, idProvider,
                           new OAuthConsentContext.Enter( idProvider, user.get(), OAuthConsentContext.INVALID_CODE ) );
        }
        final Scopes known = securityService.getScopes( idProvider );
        final Set<String> kept = new LinkedHashSet<>( request.getParams().get( "scope" ) );
        known.stream().filter( Scope::isRequired ).map( Scope::getId ).forEach( kept::add );
        kept.retainAll( pending.get().scope() );
        kept.retainAll( known.getIds() );
        final boolean granted = approve && ( pending.get().scope().isEmpty() || ( known.isNotEmpty() && !kept.isEmpty() ) );
        final RoleRestriction restriction =
            known.restrictionFor( kept ).narrow( ContextAccessor.current().getAuthInfo().getRestriction() );
        return switch ( store.resolve( idProvider, userCode, granted, user.get().getKey(), kept, restriction ) )
        {
            case GONE -> render( request, idProvider, new OAuthConsentContext.Enter( idProvider, user.get(), OAuthConsentContext.INVALID_CODE ) );
            case SHOWN_TO_ANOTHER -> verificationPage( request, idProvider );
            case RESOLVED -> render( request, idProvider, new OAuthConsentContext.Done( idProvider, user.get(), granted ) );
        };
    }

    /**
     * Renders the page through the id provider's {@code oauthConsent} function.
     */
    private PortalResponse render( final PortalRequest request, final IdProviderKey idProvider, final OAuthConsentContext context )
    {
        if ( request.getRawRequest() != null )
        {
            request.getRawRequest().setAttribute( CONTEXT_ATTRIBUTE, context.toMap() );
        }
        try
        {
            final PortalResponse page = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                 .idProviderKey( idProvider )
                                                                                 .functionName( CONSENT_FUNCTION )
                                                                                 .portalRequest( request )
                                                                                 .build() );
            if ( page == null )
            {
                requireSupport( idProvider );
                throw WebException.internalServerError( String.format( "'%s' id provider's %s returned no response", idProvider,
                                                                        CONSENT_FUNCTION ) );
            }
            return page;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private PortalResponse token( final PortalRequest request, final IdProviderKey idProvider, final VirtualHostIdProvider settings )
    {
        final String grantType = param( request, "grant_type" );
        if ( DEVICE_CODE_GRANT.equals( grantType ) )
        {
            if ( !settings.serves( IdProviderFlow.DEVICE ) )
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
                final Optional<Client> client = securityService.getClient( idProvider, clientId );
                final PortalResponse refused = client.map( c -> refusal( c, GrantTypes.DEVICE_CODE ) )
                    .or( () -> client.map( c -> beyondClient( c, pending.approvedScope(), pending.audience() ) ) )
                    .orElse( null );
                if ( refused != null )
                {
                    yield refused;
                }
                final Set<String> audience = tokenAudience( request, pending.audience() );
                if ( audience == null )
                {
                    yield beyondGrant( pending.audience() );
                }
                final GrantWithRefreshToken grant = grantService.create( CreateGrantParams.create()
                                                                             .subject( pending.subject() )
                                                                             .clientId( pending.clientId() )
                                                                             .scope( pending.approvedScope() )
                                                                             .audience( pending.audience() )
                                                                             .restriction( pending.approvedRestriction() )
                                                                             .ttl( client.map( Client::getGrantTtl ).orElse( null ) )
                                                                             .build() );
                yield tokenResponse( grant, client.orElse( null ), audience );
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
        final Optional<Client> client = securityService.getClient( idProvider, clientId );
        final PortalResponse refused = client.map( c -> refusal( c, GrantTypes.REFRESH_TOKEN ) ).orElse( null );
        if ( refused != null )
        {
            return refused;
        }
        final RedeemRefreshTokenParams params =
            RedeemRefreshTokenParams.create().refreshToken( refreshToken.trim() ).idProvider( idProvider ).clientId( clientId ).build();
        final Optional<Grant> found = grantService.lookup( params );
        if ( found.isPresent() )
        {
            final PortalResponse refusedBefore = refusedAtRefresh( request, client.orElse( null ), found.get() );
            if ( refusedBefore != null )
            {
                return refusedBefore;
            }
        }
        final Optional<GrantWithRefreshToken> redeemed = grantService.redeem( params );
        if ( redeemed.isEmpty() )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "Invalid or expired refresh_token" );
        }
        final Grant grant = redeemed.get().grant();
        final PortalResponse refusedAfter = refusedAtRefresh( request, client.orElse( null ), grant );
        if ( refusedAfter != null )
        {
            return refusedAfter;
        }
        return tokenResponse( redeemed.get(), client.orElse( null ), requireNonNull( tokenAudience( request, grant.getAudience() ) ) );
    }

    /**
     * Why a refresh is refused for the grant as it is: beyond the client's lists, which ends the
     * grant, or asking for a resource the grant does not hold. Checked before the refresh token is
     * redeemed, so a refusal leaves it as it was, and again on the grant as redeemed, since the
     * grant may have been changed in between.
     */
    private @Nullable PortalResponse refusedAtRefresh( final PortalRequest request, final @Nullable Client client, final Grant grant )
    {
        final PortalResponse beyond = client == null ? null : beyondClient( client, grant.getScope(), grant.getAudience() );
        if ( beyond != null )
        {
            grantService.revoke( grant.getSubject(), grant.getId() );
            return beyond;
        }
        return tokenAudience( request, grant.getAudience() ) == null ? beyondGrant( grant.getAudience() ) : null;
    }

    /**
     * The audiences a token is for (RFC 8707): those asked for with {@code resource}, each one the
     * grant holds, or all the grant's when none is asked for. Null when one is beyond the grant.
     */
    private static @Nullable Set<String> tokenAudience( final PortalRequest request, final Set<String> granted )
    {
        final Set<String> asked = audience( request );
        if ( asked.isEmpty() )
        {
            return granted;
        }
        return granted.containsAll( asked ) ? asked : null;
    }

    private static PortalResponse beyondGrant( final Set<String> granted )
    {
        return oauthError( HttpStatus.BAD_REQUEST, "invalid_target", "Resource beyond the grant's: " + granted );
    }

    /**
     * The token response: an access token for the audiences, ending with the grant at the latest,
     * and the refresh token when the client may use one.
     */
    private PortalResponse tokenResponse( final GrantWithRefreshToken grant, final @Nullable Client client, final Set<String> audience )
    {
        final Grant g = grant.grant();
        final AccessToken accessToken = accessTokenService.issue( IssueAccessTokenParams.create()
                                                                      .subject( g.getSubject() )
                                                                      .audience( audience )
                                                                      .scope( g.getScope() )
                                                                      .clientId( g.getClientId() )
                                                                      .grantId( g.getId() )
                                                                      .notAfter( g.getExpiresAt() )
                                                                      .build() );
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "access_token", accessToken.value() );
        body.put( "token_type", "Bearer" );
        body.put( "expires_in", accessToken.claims().getExpiresAt().getEpochSecond() - accessToken.claims().getIssuedAt().getEpochSecond() );
        if ( client == null || client.getGrantTypes().contains( GrantTypes.REFRESH_TOKEN ) )
        {
            body.put( "refresh_token", grant.refreshToken() );
        }
        if ( !g.getScope().isEmpty() )
        {
            body.put( "scope", String.join( " ", g.getScope() ) );
        }
        return json( HttpStatus.OK, body );
    }

    /**
     * Why a registered client may not use the grant type now, as an OAuth error, or null if it may.
     */
    private static @Nullable PortalResponse refusal( final Client client, final String grantType )
    {
        if ( !client.isEnabled() )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "unauthorized_client", "The client is disabled" );
        }
        if ( !client.getGrantTypes().contains( grantType ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "unauthorized_client", "The client may not use " + grantType );
        }
        return null;
    }

    /**
     * Why a registered client may not hold a grant for these scopes and audiences now, as an OAuth
     * error, or null if it may. A list on the client bounds its grants, those made before the list
     * was set included; a grant for everything is beyond any list.
     */
    private static @Nullable PortalResponse beyondClient( final Client client, final Set<String> scope, final Set<String> audience )
    {
        if ( exceeds( scope, client.getScope() ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "The grant's scope is beyond the client's: " + client.getScope() );
        }
        if ( exceeds( audience, client.getResource() ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant",
                               "The grant's audience is beyond the client's resources: " + client.getResource() );
        }
        return null;
    }

    private static boolean exceeds( final Set<String> granted, final Set<String> allowed )
    {
        return !allowed.isEmpty() && ( granted.isEmpty() || !allowed.containsAll( granted ) );
    }

    /**
     * What the client asked for, held to its list: the list itself when it asked for nothing, or
     * null when it asked beyond it. An empty list holds it to nothing.
     */
    private static @Nullable Set<String> withinClient( final Set<String> requested, final Set<String> allowed )
    {
        if ( allowed.isEmpty() )
        {
            return requested;
        }
        if ( requested.isEmpty() )
        {
            return new LinkedHashSet<>( allowed );
        }
        return allowed.containsAll( requested ) ? requested : null;
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
        if ( ContextAccessor.current().getAuthInfo().getAccessToken() != null )
        {
            throw WebException.forbidden( "Device sign-in must be approved from a signed-in browser, not with an access token" );
        }
    }

    private PortalResponse notSignedIn( final PortalRequest request, final IdProviderKey idProvider )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return render( request, idProvider, new OAuthConsentContext.WrongAccount( idProvider, authInfo.getUser() ) );
        }
        return PortalResponse.create().status( HttpStatus.UNAUTHORIZED ).build();
    }

    /**
     * A per-session token the approval form must echo, so another site cannot submit an approval
     * for a user code it knows.
     */
    /**
     * The consent form's token for the signed-in user: the session's secret, which a sign-in as
     * another user on the same browser keeps, hashed with the user, so a form rendered for one
     * user is no good to another.
     */
    private static String csrfToken( final PrincipalKey user )
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session == null )
        {
            throw WebException.forbidden( "Device sign-in requires a session" );
        }
        String secret = (String) session.getAttribute( CSRF_SESSION_ATTRIBUTE );
        if ( secret == null )
        {
            final byte[] bytes = new byte[24];
            RANDOM.nextBytes( bytes );
            secret = Base64.getUrlEncoder().withoutPadding().encodeToString( bytes );
            session.setAttribute( CSRF_SESSION_ATTRIBUTE, secret );
        }
        try
        {
            final MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            digest.update( secret.getBytes( StandardCharsets.UTF_8 ) );
            digest.update( (byte) 0 );
            digest.update( user.toString().getBytes( StandardCharsets.UTF_8 ) );
            return Base64.getUrlEncoder().withoutPadding().encodeToString( digest.digest() );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    static Set<String> queryNames( final String query )
    {
        if ( query == null || query.isEmpty() )
        {
            return Set.of();
        }
        final Set<String> names = new HashSet<>();
        for ( String pair : query.split( "&" ) )
        {
            final int eq = pair.indexOf( '=' );
            names.add( URLDecoder.decode( eq < 0 ? pair : pair.substring( 0, eq ), StandardCharsets.UTF_8 ) );
        }
        return names;
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
     * The audiences XP serves (RFC 8707): its issuer name, for XP as a whole, and the {@code audience}
     * lists of all vhost mappings.
     */
    private Set<String> servedAudiences()
    {
        final Set<String> audiences = new LinkedHashSet<>();
        audiences.add( accessTokenService.getIssuer() );
        for ( final VirtualHost virtualHost : virtualHostService.getVirtualHosts() )
        {
            audiences.addAll( virtualHost.getAudience() );
        }
        return audiences;
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
