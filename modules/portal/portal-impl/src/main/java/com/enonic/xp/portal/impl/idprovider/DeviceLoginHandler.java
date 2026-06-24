package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessTokenParams;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.DeviceAuthService;
import com.enonic.xp.security.token.DeviceAuthorization;
import com.enonic.xp.security.token.DeviceAuthorizationParams;
import com.enonic.xp.security.token.DeviceAuthorizationPoll;
import com.enonic.xp.shared.SharedMapService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.vhost.IdProviderFlow;

/**
 * Owns the browserless / native-app login HTTP endpoints (device grant - RFC 8628, and native-app
 * authorization-code grant - RFC 8252/7636). XP core runs the whole OAuth protocol here:
 * code/token issuance ({@link AccessTokenService}), the device-code lifecycle
 * ({@link DeviceAuthService}), native authorization codes ({@link NativeAuthCodeStore}),
 * PKCE S256 and redirect validation.
 * <p>
 * The id-provider-specific steps are delegated to predefined controller hooks, the way
 * {@code autoLogin} is invoked - the id provider implements only the UI/policy and never routes or
 * reads flow config:
 * <ul>
 *     <li>{@code deviceVerification(req, context)} - the device verification / approval page;</li>
 *     <li>{@code authorizeConsent(req, context)} - the native authorization consent page;</li>
 *     <li>{@code allowRedirectUri(req, context)} - optional redirect policy: returns whether a
 *     non-loopback, non-private-use redirect (e.g. a claimed {@code https} URI) is registered for
 *     this id provider. Loopback and RFC 8252 private-use schemes are always allowed.</li>
 * </ul>
 * Per-vhost flow gating (DEVICE / NATIVE) is enforced by the caller and re-checked here per grant.
 */
@Component(service = DeviceLoginHandler.class)
@NullMarked
public class DeviceLoginHandler
{
    static final String DEVICE_VERIFICATION_FUNCTION = "deviceVerification";

    static final String AUTHORIZE_CONSENT_FUNCTION = "authorizeConsent";

    static final String ALLOW_REDIRECT_FUNCTION = "allowRedirectUri";

    static final String DEVICE_CODE_GRANT = "urn:ietf:params:oauth:grant-type:device_code";

    static final String AUTH_CODE_GRANT = "authorization_code";

    private static final Logger LOG = LoggerFactory.getLogger( DeviceLoginHandler.class );

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours( 1 );

    private static final Duration NATIVE_CODE_TTL = Duration.ofMinutes( 2 );

    // The native consent request parameters echoed to the consent page (as hidden fields) so the
    // approve POST carries the original authorization request.
    private static final String[] NATIVE_REQUEST_PARAMS =
        {"redirect_uri", "code_challenge", "code_challenge_method", "scope", "state", "client_id", "resource"};

    // RFC 8252 loopback redirect (any port), which needs no registration.
    private static final Pattern LOOPBACK = Pattern.compile( "^http://(127\\.0\\.0\\.1|\\[::1\\]|localhost)(:\\d+)?(/.*)?$" );

    private final DeviceAuthService deviceAuthService;

    private final AccessTokenService accessTokenService;

    private final IdProviderControllerService idProviderControllerService;

    private final NativeAuthCodeStore nativeAuthCodeStore;

    @Activate
    public DeviceLoginHandler( @Reference final DeviceAuthService deviceAuthService,
                               @Reference final AccessTokenService accessTokenService,
                               @Reference final IdProviderControllerService idProviderControllerService,
                               @Reference final SharedMapService sharedMapService )
    {
        this.deviceAuthService = deviceAuthService;
        this.accessTokenService = accessTokenService;
        this.idProviderControllerService = idProviderControllerService;
        this.nativeAuthCodeStore = new NativeAuthCodeStore( sharedMapService );
    }

    /**
     * Whether {@code subPath} (the id provider URL remainder, e.g. {@code device/code}) is a
     * device/native login endpoint owned by core.
     */
    public static boolean isFlowEndpoint( final String subPath )
    {
        return switch ( subPath )
        {
            case "device/code", "device", "authorize", "token" -> true;
            default -> false;
        };
    }

    public PortalResponse handle( final PortalRequest req, final IdProviderKey idProvider, final String subPath,
                                  final Set<IdProviderFlow> flows )
        throws IOException
    {
        final HttpMethod method = req.getMethod();
        switch ( subPath )
        {
            case "device/code":
                return requirePost( method, () -> requireFlow( flows, IdProviderFlow.DEVICE, () -> deviceAuthorization( req, idProvider ) ) );
            case "device":
                return requireFlow( flows, IdProviderFlow.DEVICE,
                                    () -> method == HttpMethod.POST ? verificationSubmit( req, idProvider ) : verificationPage( req, idProvider ) );
            case "authorize":
                return requireFlow( flows, IdProviderFlow.NATIVE,
                                    () -> method == HttpMethod.POST ? authorizeSubmit( req, idProvider ) : authorizePrompt( req, idProvider ) );
            case "token":
                return requirePost( method, () -> token( req, idProvider, flows ) );
            default:
                return status( HttpStatus.NOT_FOUND );
        }
    }

    // POST .../device/code - RFC 8628 device authorization endpoint.
    private PortalResponse deviceAuthorization( final PortalRequest req, final IdProviderKey idProvider )
    {
        final DeviceAuthorizationParams.Builder params = DeviceAuthorizationParams.create().idProvider( idProvider );
        applyIfPresent( param( req, "client_id" ), params::clientId );
        applyIfPresent( param( req, "scope" ), params::scope );
        applyIfPresent( param( req, "resource" ), params::audience );

        final DeviceAuthorization auth = deviceAuthService.start( params.build() );

        final String verificationUri = endpointUrl( req, "device" );
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "device_code", auth.getDeviceCode() );
        body.put( "user_code", auth.getUserCode() );
        body.put( "verification_uri", verificationUri );
        body.put( "verification_uri_complete", verificationUri + "?user_code=" + enc( auth.getUserCode() ) );
        body.put( "expires_in", auth.getExpiresInSeconds() );
        body.put( "interval", auth.getIntervalSeconds() );
        return json( HttpStatus.OK, body );
    }

    // POST .../token - RFC 6749 token endpoint (device_code and authorization_code grants).
    private PortalResponse token( final PortalRequest req, final IdProviderKey idProvider, final Set<IdProviderFlow> flows )
    {
        final String grantType = param( req, "grant_type" );
        if ( DEVICE_CODE_GRANT.equals( grantType ) )
        {
            return flows.contains( IdProviderFlow.DEVICE ) ? deviceCodeGrant( req, idProvider ) : status( HttpStatus.NOT_FOUND );
        }
        if ( AUTH_CODE_GRANT.equals( grantType ) )
        {
            return flows.contains( IdProviderFlow.NATIVE ) ? authorizationCodeGrant( req, idProvider ) : status( HttpStatus.NOT_FOUND );
        }
        return oauthError( HttpStatus.BAD_REQUEST, "unsupported_grant_type", null );
    }

    private PortalResponse deviceCodeGrant( final PortalRequest req, final IdProviderKey idProvider )
    {
        final String deviceCode = param( req, "device_code" );
        if ( deviceCode == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing device_code" );
        }

        final DeviceAuthorizationPoll poll = deviceAuthService.poll( idProvider, deviceCode );
        switch ( poll.getState() )
        {
            case PENDING:
                return oauthError( HttpStatus.BAD_REQUEST, "authorization_pending", null );
            case SLOW_DOWN:
                return oauthError( HttpStatus.BAD_REQUEST, "slow_down", null );
            case DENIED:
                return oauthError( HttpStatus.BAD_REQUEST, "access_denied", null );
            case APPROVED:
                final PrincipalKey subject = poll.getSubject();
                if ( subject == null )
                {
                    return oauthError( HttpStatus.BAD_REQUEST, "expired_token", null );
                }
                return tokenResponse( idProvider, subject, poll.getAudience(), poll.getClientId(), poll.getScope() );
            case EXPIRED:
            default:
                return oauthError( HttpStatus.BAD_REQUEST, "expired_token", null );
        }
    }

    private PortalResponse authorizationCodeGrant( final PortalRequest req, final IdProviderKey idProvider )
    {
        final String code = param( req, "code" );
        final String redirectUri = param( req, "redirect_uri" );
        final String codeVerifier = param( req, "code_verifier" );
        if ( code == null || redirectUri == null || codeVerifier == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "Missing code, redirect_uri or code_verifier" );
        }

        final NativeAuthCodeStore.AuthCode authCode = nativeAuthCodeStore.consume( idProvider, code );
        if ( authCode == null )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "Invalid or expired code" );
        }
        if ( !authCode.redirectUri.equals( redirectUri ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "redirect_uri mismatch" );
        }
        if ( !pkceMatches( codeVerifier, authCode.challenge ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_grant", "PKCE verification failed" );
        }

        return tokenResponse( idProvider, PrincipalKey.from( authCode.subject ), authCode.audience, authCode.clientId, authCode.scope );
    }

    private PortalResponse tokenResponse( final IdProviderKey idProvider, final PrincipalKey subject, @Nullable final String audience,
                                          @Nullable final String clientId, @Nullable final String scope )
    {
        final AccessTokenParams.Builder params =
            AccessTokenParams.create().subject( subject ).issuer( issuer( idProvider ) ).ttl( ACCESS_TOKEN_TTL );
        for ( final String aud : splitAudience( audience ) )
        {
            params.addAudience( aud );
        }
        applyIfPresent( clientId, params::clientId );
        applyIfPresent( scope, params::scope );

        final String token = accessTokenService.issue( params.build() );

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "access_token", token );
        body.put( "token_type", "Bearer" );
        body.put( "expires_in", (int) ACCESS_TOKEN_TTL.toSeconds() );
        if ( scope != null && !scope.isEmpty() )
        {
            body.put( "scope", scope );
        }
        return json( HttpStatus.OK, body );
    }

    // GET .../device - human verification + approval page (rendered by the id provider hook).
    private PortalResponse verificationPage( final PortalRequest req, final IdProviderKey idProvider )
        throws IOException
    {
        final User user = authenticatedUser();
        if ( user == null )
        {
            return status( HttpStatus.UNAUTHORIZED );
        }

        final String userCode = param( req, "user_code" );
        final String status;
        if ( userCode == null || userCode.isEmpty() )
        {
            status = "need_code";
        }
        else
        {
            status = deviceAuthService.findByUserCode( idProvider, userCode.toUpperCase() ).isPresent() ? "confirm" : "invalid";
        }

        return render( req, idProvider, DEVICE_VERIFICATION_FUNCTION,
                       deviceContext( req, user, status, userCode == null ? null : userCode.toUpperCase() ) );
    }

    // POST .../device - approve / deny submission.
    private PortalResponse verificationSubmit( final PortalRequest req, final IdProviderKey idProvider )
        throws IOException
    {
        final User user = authenticatedUser();
        if ( user == null )
        {
            return status( HttpStatus.UNAUTHORIZED );
        }

        final String userCode = upper( param( req, "user_code" ) );
        final boolean approve = "true".equals( param( req, "approve" ) );

        final Optional<String> deviceCode = userCode == null ? Optional.empty() : deviceAuthService.findByUserCode( idProvider, userCode );
        if ( deviceCode.isEmpty() )
        {
            return render( req, idProvider, DEVICE_VERIFICATION_FUNCTION, deviceContext( req, user, "invalid", userCode ) );
        }

        deviceAuthService.resolve( idProvider, deviceCode.get(), approve, approve ? user.getKey() : null );
        return render( req, idProvider, DEVICE_VERIFICATION_FUNCTION,
                       deviceContext( req, user, approve ? "approved" : "denied", userCode ) );
    }

    // GET .../authorize - native-app authorization endpoint (PKCE S256 required), renders consent.
    private PortalResponse authorizePrompt( final PortalRequest req, final IdProviderKey idProvider )
        throws IOException
    {
        final PortalResponse validation = validateAuthorize( req, idProvider );
        if ( validation != null )
        {
            return validation;
        }

        final User user = authenticatedUser();
        if ( user == null )
        {
            return status( HttpStatus.UNAUTHORIZED );
        }

        return render( req, idProvider, AUTHORIZE_CONSENT_FUNCTION, nativeContext( req, user ) );
    }

    // POST .../authorize - consent submission: issue a one-time code and redirect, or deny.
    private PortalResponse authorizeSubmit( final PortalRequest req, final IdProviderKey idProvider )
        throws IOException
    {
        final PortalResponse validation = validateAuthorize( req, idProvider );
        if ( validation != null )
        {
            return validation;
        }

        final User user = authenticatedUser();
        if ( user == null )
        {
            return status( HttpStatus.UNAUTHORIZED );
        }

        final String redirectUri = param( req, "redirect_uri" );
        final String state = param( req, "state" );

        if ( !"true".equals( param( req, "approve" ) ) )
        {
            return redirect( appendParam( appendParam( redirectUri, "error", "access_denied" ), "state", state ) );
        }

        final String code = nativeAuthCodeStore.create( idProvider, new NativeAuthCodeStore.AuthCode( param( req, "code_challenge" ),
                                                                                                       redirectUri, user.getKey().toString(),
                                                                                                       orEmpty( param( req, "client_id" ) ),
                                                                                                       orEmpty( param( req, "scope" ) ),
                                                                                                       orEmpty( param( req, "resource" ) ) ),
                                                         (int) NATIVE_CODE_TTL.toSeconds() );

        return redirect( appendParam( appendParam( redirectUri, "code", code ), "state", state ) );
    }

    @Nullable
    private PortalResponse validateAuthorize( final PortalRequest req, final IdProviderKey idProvider )
        throws IOException
    {
        final String redirectUri = param( req, "redirect_uri" );
        if ( redirectUri == null || !redirectAllowed( req, idProvider, redirectUri ) )
        {
            // An invalid redirect target must never be redirected to (open-redirect / code-leak guard).
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "redirect_uri is not allowed" );
        }
        if ( param( req, "code_challenge" ) == null || !"S256".equals( param( req, "code_challenge_method" ) ) )
        {
            return oauthError( HttpStatus.BAD_REQUEST, "invalid_request", "code_challenge with S256 is required" );
        }
        return null;
    }

    /**
     * Loopback and RFC 8252 private-use-scheme redirects are always allowed (PKCE-protected).
     * Anything else (e.g. a claimed {@code https} URI) is allowed only if the id provider's
     * {@code allowRedirectUri} hook approves it - registration is the id provider's policy.
     */
    private boolean redirectAllowed( final PortalRequest req, final IdProviderKey idProvider, final String redirectUri )
        throws IOException
    {
        if ( isBuiltInRedirect( redirectUri ) )
        {
            return true;
        }
        final Map<String, Object> context = new LinkedHashMap<>();
        context.put( "redirectUri", redirectUri );
        applyIfPresent( param( req, "client_id" ), value -> context.put( "clientId", value ) );

        return idProviderControllerService.executeBoolean( IdProviderControllerExecutionParams.create()
                                                               .idProviderKey( idProvider )
                                                               .functionName( ALLOW_REDIRECT_FUNCTION )
                                                               .portalRequest( req )
                                                               .contextArg( new HookContext( context ) )
                                                               .build() );
    }

    /**
     * Invokes a predefined id provider hook to render the human-facing page, passing the context as
     * its second argument. The hook is required: a missing implementation is a server misconfiguration.
     */
    private PortalResponse render( final PortalRequest req, final IdProviderKey idProvider, final String functionName,
                                  final HookContext context )
        throws IOException
    {
        final PortalResponse response = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                 .idProviderKey( idProvider )
                                                                                 .functionName( functionName )
                                                                                 .portalRequest( req )
                                                                                 .contextArg( context )
                                                                                 .build() );
        if ( response == null )
        {
            LOG.warn( "Id provider [{}] does not implement the required [{}] hook", idProvider, functionName );
            return status( HttpStatus.INTERNAL_SERVER_ERROR );
        }
        return response;
    }

    private HookContext deviceContext( final PortalRequest req, final User user, final String status, @Nullable final String userCode )
    {
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put( "status", status );
        values.put( "actionUrl", endpointUrl( req, "device" ) );
        if ( userCode != null )
        {
            values.put( "userCode", userCode );
        }
        putUser( values, user );
        return new HookContext( values );
    }

    private HookContext nativeContext( final PortalRequest req, final User user )
    {
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put( "actionUrl", endpointUrl( req, "authorize" ) );
        // The consent page echoes these as hidden fields, so the POST carries the original request.
        for ( final String name : NATIVE_REQUEST_PARAMS )
        {
            applyIfPresent( param( req, name ), value -> values.put( name, value ) );
        }
        putUser( values, user );
        return new HookContext( values );
    }

    private static void putUser( final Map<String, Object> values, final User user )
    {
        values.put( "userKey", user.getKey().toString() );
        values.put( "userDisplayName", user.getDisplayName() == null ? user.getLogin() : user.getDisplayName() );
        values.put( "userLogin", user.getLogin() );
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    @Nullable
    private static User authenticatedUser()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.isAuthenticated() ? authInfo.getUser() : null;
    }

    private static String issuer( final IdProviderKey idProvider )
    {
        return idProvider.toString();
    }

    private static boolean pkceMatches( final String verifier, final String challenge )
    {
        try
        {
            final MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            final byte[] hash = digest.digest( verifier.getBytes( StandardCharsets.US_ASCII ) );
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString( hash ).equals( challenge );
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    static boolean isBuiltInRedirect( final String uri )
    {
        if ( LOOPBACK.matcher( uri ).matches() )
        {
            return true;
        }
        // RFC 8252 private-use URI scheme (reverse-DNS, e.g. com.example.app:/cb). PKCE is mandatory,
        // which mitigates code interception. Remote http(s) redirects are deferred to the id provider's
        // allowRedirectUri policy hook.
        final int colon = uri.indexOf( ':' );
        if ( colon <= 0 )
        {
            return false;
        }
        final String scheme = uri.substring( 0, colon ).toLowerCase();
        if ( scheme.equals( "http" ) || scheme.equals( "https" ) )
        {
            return false;
        }
        return scheme.contains( "." );
    }

    private static String[] splitAudience( @Nullable final String value )
    {
        if ( value == null || value.isBlank() )
        {
            return new String[0];
        }
        return Arrays.stream( value.trim().split( "\\s+" ) ).filter( s -> !s.isEmpty() ).toArray( String[]::new );
    }

    private static String endpointUrl( final PortalRequest req, final String suffix )
    {
        return ServletRequestUrlHelper.getServerUrl( req.getRawRequest() ) + req.getContextPath() + "/" + suffix;
    }

    @Nullable
    private static String param( final PortalRequest req, final String name )
    {
        final var values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    @Nullable
    private static String upper( @Nullable final String value )
    {
        return value == null ? null : value.toUpperCase();
    }

    private static String orEmpty( @Nullable final String value )
    {
        return value == null ? "" : value;
    }

    private static void applyIfPresent( @Nullable final String value, final java.util.function.Consumer<String> consumer )
    {
        if ( value != null && !value.isEmpty() )
        {
            consumer.accept( value );
        }
    }

    private static String appendParam( final String uri, final String name, @Nullable final String value )
    {
        if ( value == null )
        {
            return uri;
        }
        return uri + ( uri.indexOf( '?' ) >= 0 ? '&' : '?' ) + name + "=" + enc( value );
    }

    private static String enc( final String value )
    {
        return URLEncoder.encode( value, StandardCharsets.UTF_8 );
    }

    private interface ResponseSupplier
    {
        PortalResponse get()
            throws IOException;
    }

    private static PortalResponse requirePost( final HttpMethod method, final ResponseSupplier supplier )
        throws IOException
    {
        return method == HttpMethod.POST ? supplier.get() : status( HttpStatus.METHOD_NOT_ALLOWED );
    }

    private static PortalResponse requireFlow( final Set<IdProviderFlow> flows, final IdProviderFlow flow, final ResponseSupplier supplier )
        throws IOException
    {
        return flows.contains( flow ) ? supplier.get() : status( HttpStatus.NOT_FOUND );
    }

    private static PortalResponse json( final HttpStatus status, final Map<String, Object> body )
    {
        return PortalResponse.create()
            .status( status )
            .contentType( MediaType.JSON_UTF_8 )
            .body( toJson( body ) )
            .header( "Cache-Control", "no-store" )
            .header( "Pragma", "no-cache" )
            .build();
    }

    private static PortalResponse oauthError( final HttpStatus status, final String error, @Nullable final String description )
    {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put( "error", error );
        if ( description != null )
        {
            body.put( "error_description", description );
        }
        return json( status, body );
    }

    private static PortalResponse redirect( final String location )
    {
        return PortalResponse.create().status( HttpStatus.SEE_OTHER ).header( "Location", location ).header( "Cache-Control", "no-store" ).build();
    }

    private static PortalResponse status( final HttpStatus status )
    {
        return PortalResponse.create().status( status ).build();
    }

    private static String toJson( final Map<String, Object> map )
    {
        final StringBuilder sb = new StringBuilder( "{" );
        boolean first = true;
        for ( final Map.Entry<String, Object> entry : map.entrySet() )
        {
            if ( !first )
            {
                sb.append( ',' );
            }
            first = false;
            sb.append( jsonString( entry.getKey() ) ).append( ':' );
            final Object value = entry.getValue();
            if ( value instanceof Number || value instanceof Boolean )
            {
                sb.append( value );
            }
            else
            {
                sb.append( jsonString( String.valueOf( value ) ) );
            }
        }
        return sb.append( '}' ).toString();
    }

    private static String jsonString( final String value )
    {
        final StringBuilder sb = new StringBuilder( "\"" );
        for ( int i = 0; i < value.length(); i++ )
        {
            final char c = value.charAt( i );
            switch ( c )
            {
                case '"' -> sb.append( "\\\"" );
                case '\\' -> sb.append( "\\\\" );
                case '\n' -> sb.append( "\\n" );
                case '\r' -> sb.append( "\\r" );
                case '\t' -> sb.append( "\\t" );
                default ->
                {
                    if ( c < 0x20 )
                    {
                        sb.append( String.format( "\\u%04x", (int) c ) );
                    }
                    else
                    {
                        sb.append( c );
                    }
                }
            }
        }
        return sb.append( '"' ).toString();
    }

    private static final class HookContext
        implements MapSerializable
    {
        private final Map<String, Object> values;

        private HookContext( final Map<String, Object> values )
        {
            this.values = values;
        }

        @Override
        public void serialize( final MapGenerator gen )
        {
            values.forEach( gen::value );
        }
    }
}
