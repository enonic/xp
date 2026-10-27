package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.internal.UuidHelper;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.RegisterClientParams;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.AccessTokenVerification;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.security.token.VerifyAccessTokenParams;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.shared.SharedMapService;

/**
 * XP's own access tokens: JWS compact form, HS512, {@code typ} {@value JwsHs512#TYP} (RFC 9068),
 * no critical extensions. The signing keys sign nothing else. Required claims: {@code iss},
 * {@code sub} (the user key), {@code aud} (array or single string, at least one), {@code client_id},
 * {@code iat}, {@code exp} (whole seconds), {@code jti}. Optional: {@code scope} (space-separated),
 * {@code act} (RFC 8693 actor), {@code grant}. A missing required claim, or a claim in another
 * form, makes the token invalid.
 * <p>
 * Settings are read into one immutable snapshot, so a rejected configuration update leaves the
 * running settings as they are.
 * <p>
 * Configuration ({@code com.enonic.xp.security.cfg}): {@code token.issuer} (default: the
 * installation's name, {@link ServerInfo#getName()}), {@code token.ttl} (ISO-8601 duration, default
 * one hour) and the keys described in {@link ConfiguredTokenSigningKeys}; without configured keys,
 * {@link SharedMapTokenSigningKeys} is used.
 * <p>
 * The same keys sign the ids of self-registered clients (see {@link StatelessClients}), which
 * hold for {@value #REGISTRATION_DAYS} days.
 */
@Component(configurationPid = "com.enonic.xp.security")
public class AccessTokenServiceImpl
    implements AccessTokenService, TokenSigningKeys
{
    static final Duration DEFAULT_TTL = Duration.ofHours( 1 );

    /**
     * Tolerated clock difference between the issuing and the verifying node.
     */
    static final Duration CLOCK_SKEW = Duration.ofSeconds( 60 );

    private static final Logger LOG = LoggerFactory.getLogger( AccessTokenServiceImpl.class );

    private final SharedMapService sharedMapService;

    private final Clock clock;

    private final StatelessClients stateless;

    /**
     * How long a self-registration holds.
     */
    static final int REGISTRATION_DAYS = 90;

    private volatile Settings settings;

    @Activate
    public AccessTokenServiceImpl( @Reference final SharedMapService sharedMapService )
    {
        this( sharedMapService, Clock.systemUTC() );
    }

    AccessTokenServiceImpl( final SharedMapService sharedMapService, final Clock clock )
    {
        this.sharedMapService = sharedMapService;
        this.clock = clock;
        this.stateless = new StatelessClients( this, clock, Duration.ofDays( REGISTRATION_DAYS ) );
    }

    /**
     * Reads the {@code token.*} settings. SCR passes every property of the PID, including non-string
     * service properties, so the map is normalised to strings first.
     */
    @Activate
    @Modified
    public void activate( final Map<String, ?> properties )
    {
        final Map<String, String> config = ConfigBuilder.create().addAll( properties ).build().asMap();
        final String issuer = Optional.ofNullable( config.get( "token.issuer" ) )
            .map( String::trim )
            .filter( s -> !s.isEmpty() )
            .orElseGet( () -> ServerInfo.get().getName() );
        final Duration ttl =
            Optional.ofNullable( config.get( "token.ttl" ) ).map( String::trim ).map( Duration::parse ).orElse( DEFAULT_TTL );
        if ( !isLifetime( ttl ) )
        {
            throw new IllegalArgumentException( "token.ttl must be a whole number of seconds, at least one: " + ttl );
        }
        final TokenSigningKeys keys = ConfiguredTokenSigningKeys.from( config ).<TokenSigningKeys>map( configured -> {
            LOG.info( "Token signing key [{}] from configuration", configured.signingKey().kid() );
            return configured;
        } ).orElseGet( () -> new SharedMapTokenSigningKeys( sharedMapService ) );
        this.settings = new Settings( issuer, ttl, keys );
    }

    @Override
    public AccessToken issue( final IssueAccessTokenParams params )
    {
        final Settings settings = this.settings;
        final Instant now = clock.instant().truncatedTo( ChronoUnit.SECONDS );
        final Instant byLifetime = now.plus( params.getTtl() != null ? params.getTtl() : settings.ttl() );
        final Instant notAfter = params.getNotAfter() == null ? byLifetime : params.getNotAfter().truncatedTo( ChronoUnit.SECONDS );
        if ( !notAfter.isAfter( now ) )
        {
            throw new IllegalArgumentException( "The token would be past its end at issue: " + notAfter );
        }
        final AccessTokenClaims.Builder claims = AccessTokenClaims.create()
            .issuer( settings.issuer() )
            .subject( params.getSubject() )
            .audience( params.getAudience() )
            .scope( params.getScope() )
            .actor( params.getActor() )
            .clientId( params.getClientId() )
            .grantId( params.getGrantId() )
            .issuedAt( now )
            .expiresAt( notAfter.isBefore( byLifetime ) ? notAfter : byLifetime )
            .tokenId( UuidHelper.newUUIDv7( now.toEpochMilli() ).toString() );
        final AccessTokenClaims built = claims.build();
        return new AccessToken( JwsHs512.sign( settings.keys().signingKey(), toPayload( built ) ), built );
    }

    @Override
    public Client registerClient( final RegisterClientParams params )
    {
        return stateless.register( params.getClient() );
    }

    @Override
    public Optional<Client> getRegisteredClient( final IdProviderKey idProvider, final String clientId )
    {
        return StatelessClients.isStatelessId( clientId ) ? stateless.read( idProvider, clientId ) : Optional.empty();
    }

    @Override
    public boolean isSelfRegisteredClientId( final String clientId )
    {
        return StatelessClients.isStatelessId( clientId );
    }

    @Override
    public SigningKey signingKey()
    {
        return this.settings.keys().signingKey();
    }

    @Override
    public Optional<SigningKey> verificationKey( final String kid )
    {
        return this.settings.keys().verificationKey( kid );
    }

    @Override
    public String getIssuer()
    {
        return this.settings.issuer();
    }

    @Override
    public AccessTokenVerification verify( final VerifyAccessTokenParams params )
    {
        final Settings settings = this.settings;
        final Optional<JwsHs512.Parsed> parsedOpt = JwsHs512.parse( params.getToken() );
        if ( parsedOpt.isEmpty() )
        {
            return new AccessTokenVerification.InvalidIssuer();
        }
        final JwsHs512.Parsed parsed = parsedOpt.get();
        if ( !isIssuedHere( parsed, settings.issuer() ) )
        {
            return new AccessTokenVerification.InvalidIssuer();
        }
        if ( !JwsHs512.hasExpectedHeader( parsed ) )
        {
            LOG.debug( "Token rejected: header names another algorithm or token type" );
            return new AccessTokenVerification.Invalid();
        }
        final Optional<SigningKey> key = parsed.kid().flatMap( settings.keys()::verificationKey );
        if ( key.isEmpty() )
        {
            LOG.debug( "Token rejected: unknown signing key [{}]", parsed.kid().orElse( null ) );
            return new AccessTokenVerification.Invalid();
        }
        if ( !JwsHs512.verify( parsed, key.get() ) )
        {
            LOG.debug( "Token rejected: bad signature" );
            return new AccessTokenVerification.Invalid();
        }
        final AccessTokenClaims claims;
        try
        {
            claims = fromPayload( parsed.payload() );
        }
        catch ( Exception e )
        {
            LOG.debug( "Token rejected: malformed claims", e );
            return new AccessTokenVerification.Invalid();
        }
        if ( claims.getExpiresAt().plus( CLOCK_SKEW ).isBefore( clock.instant() ) )
        {
            LOG.debug( "Token [{}] rejected: expired at {}", claims.getTokenId(), claims.getExpiresAt() );
            return new AccessTokenVerification.Invalid();
        }
        return new AccessTokenVerification.Verified( claims );
    }

    private static boolean isIssuedHere( final JwsHs512.Parsed parsed, final String issuer )
    {
        final JsonNode iss = parsed.payload().get( "iss" );
        return iss != null && iss.isTextual() && issuer.equals( iss.asText() );
    }

    /**
     * Timestamps are whole seconds, so a lifetime must be a whole number of seconds, at least one.
     * Anything less would truncate to a token that expires as it is issued.
     */
    static boolean isLifetime( final Duration ttl )
    {
        return ttl.getNano() == 0 && ttl.getSeconds() >= 1;
    }

    private record Settings(String issuer, Duration ttl, TokenSigningKeys keys)
    {
    }

    private static ObjectNode toPayload( final AccessTokenClaims claims )
    {
        final ObjectNode payload = JwsHs512.newPayload();
        payload.put( "iss", claims.getIssuer() );
        payload.put( "sub", claims.getSubject().toString() );
        final ArrayNode aud = payload.putArray( "aud" );
        claims.getAudience().forEach( aud::add );
        if ( !claims.getScope().isEmpty() )
        {
            payload.put( "scope", String.join( " ", claims.getScope() ) );
        }
        if ( claims.getActor() != null )
        {
            payload.putObject( "act" ).put( "sub", claims.getActor().toString() );
        }
        payload.put( "client_id", claims.getClientId() );
        if ( claims.getGrantId() != null )
        {
            payload.put( "grant", claims.getGrantId() );
        }
        payload.put( "iat", claims.getIssuedAt().getEpochSecond() );
        payload.put( "exp", claims.getExpiresAt().getEpochSecond() );
        payload.put( "jti", claims.getTokenId() );
        return payload;
    }

    private static AccessTokenClaims fromPayload( final ObjectNode payload )
    {
        final AccessTokenClaims.Builder claims = AccessTokenClaims.create()
            .issuer( requiredText( payload, "iss" ) )
            .subject( PrincipalKey.from( requiredText( payload, "sub" ) ) )
            .issuedAt( Instant.ofEpochSecond( requiredLong( payload, "iat" ) ) )
            .expiresAt( Instant.ofEpochSecond( requiredLong( payload, "exp" ) ) )
            .tokenId( requiredText( payload, "jti" ) );

        switch ( payload.get( "aud" ) )
        {
            case null -> throw new IllegalArgumentException( "Claim [aud] is missing" );
            case ArrayNode array -> claims.audience( texts( array, "aud" ) );
            case TextNode single -> claims.audience( List.of( single.asText() ) );
            default -> throw new IllegalArgumentException( "Claim [aud] is neither an array nor a string" );
        }
        switch ( payload.get( "scope" ) )
        {
            case null -> { }
            case TextNode scope -> claims.scope( scope.asText().isEmpty() ? List.of() : List.of( scope.asText().split( " " ) ) );
            default -> throw new IllegalArgumentException( "Claim [scope] is not a string" );
        }
        switch ( payload.get( "act" ) )
        {
            case null -> { }
            case ObjectNode actor -> claims.actor( PrincipalKey.from( requiredText( actor, "sub" ) ) );
            default -> throw new IllegalArgumentException( "Claim [act] is not an object" );
        }
        claims.clientId( requiredText( payload, "client_id" ) );
        claims.grantId( optionalText( payload, "grant" ) );
        return claims.build();
    }

    private static List<String> texts( final ArrayNode array, final String field )
    {
        final List<String> values = new ArrayList<>();
        for ( Iterator<JsonNode> it = array.elements(); it.hasNext(); )
        {
            final JsonNode element = it.next();
            if ( !element.isTextual() )
            {
                throw new IllegalArgumentException( "Claim [" + field + "] holds a value that is not a string" );
            }
            values.add( element.asText() );
        }
        return values;
    }

    private static String optionalText( final ObjectNode node, final String field )
    {
        return switch ( node.get( field ) )
        {
            case null -> null;
            case TextNode text -> text.asText();
            default -> throw new IllegalArgumentException( "Claim [" + field + "] is not a string" );
        };
    }

    private static String requiredText( final ObjectNode node, final String field )
    {
        final JsonNode value = node.get( field );
        if ( value == null || !value.isTextual() )
        {
            throw new IllegalArgumentException( "Missing claim [" + field + "]" );
        }
        return value.asText();
    }

    private static long requiredLong( final ObjectNode node, final String field )
    {
        final JsonNode value = node.get( field );
        if ( value == null || !value.canConvertToLong() )
        {
            throw new IllegalArgumentException( "Missing claim [" + field + "]" );
        }
        return value.asLong();
    }
}
