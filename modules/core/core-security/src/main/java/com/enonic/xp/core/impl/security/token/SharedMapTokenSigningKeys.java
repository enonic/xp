package com.enonic.xp.core.impl.security.token;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

/**
 * The default key source when none is configured: a key generated once and kept in a map shared
 * across the cluster, so every node signs and verifies with the same secret.
 * <p>
 * The map holds one entry per key id with the base64 secret, plus a {@value #SIGNING_ENTRY} entry
 * naming the signing key. Creation uses the map's atomic modify, so two nodes starting together
 * agree on one key. Nodes cache keys locally and look up unknown key ids in the map.
 * <p>
 * The key lives as long as the cluster. A full restart generates a new one and tokens signed
 * before it stop verifying. Operators who need more configure their own keys.
 */
final class SharedMapTokenSigningKeys
    implements TokenSigningKeys
{
    static final String MAP_NAME = "com.enonic.xp.security.token.keys";

    static final String SIGNING_ENTRY = "$signing";

    private static final int SECRET_BYTES = 64;

    private static final Logger LOG = LoggerFactory.getLogger( SharedMapTokenSigningKeys.class );

    private static final SecureRandom RANDOM = new SecureRandom();

    private final SharedMap<String, String> map;

    private final Map<String, SigningKey> cache = new ConcurrentHashMap<>();

    private volatile SigningKey signingKey;

    private final String generatedMessage;

    SharedMapTokenSigningKeys( final SharedMapService sharedMapService )
    {
        this( sharedMapService, MAP_NAME, "Generated token signing key [{}]: tokens will not survive a full cluster restart. " +
            "Configure token.keys.<kid> in com.enonic.xp.security.cfg to use a persistent key." );
    }

    /**
     * A key set in the named map, with its own log message when a key is generated.
     */
    SharedMapTokenSigningKeys( final SharedMapService sharedMapService, final String mapName, final String generatedMessage )
    {
        this.map = sharedMapService.getSharedMap( mapName );
        this.generatedMessage = generatedMessage;
    }

    @Override
    public SigningKey signingKey()
    {
        SigningKey key = this.signingKey;
        if ( key == null )
        {
            key = resolveSigningKey();
            this.signingKey = key;
        }
        return key;
    }

    @Override
    public Optional<SigningKey> verificationKey( final String kid )
    {
        if ( SIGNING_ENTRY.equals( kid ) )
        {
            return Optional.empty();
        }
        final SigningKey cached = cache.get( kid );
        if ( cached != null )
        {
            return Optional.of( cached );
        }
        final String secret = map.get( kid );
        if ( secret == null )
        {
            return Optional.empty();
        }
        final SigningKey key = new SigningKey( kid, Base64.getDecoder().decode( secret ) );
        cache.put( kid, key );
        return Optional.of( key );
    }

    private SigningKey resolveSigningKey()
    {
        final String existing = map.get( SIGNING_ENTRY );
        if ( existing != null )
        {
            final Optional<SigningKey> key = verificationKey( existing );
            if ( key.isPresent() )
            {
                return key.get();
            }
        }

        final byte[] secret = new byte[SECRET_BYTES];
        RANDOM.nextBytes( secret );
        final String kid = newKid();
        map.modify( kid, value -> value == null ? Base64.getEncoder().encodeToString( secret ) : value );
        final String winner = map.modify( SIGNING_ENTRY, value -> value == null || map.get( value ) == null ? kid : value );
        if ( !kid.equals( winner ) )
        {
            map.delete( kid );
        }
        else
        {
            LOG.info( generatedMessage, kid );
        }
        return verificationKey( winner ).orElseThrow( () -> new IllegalStateException( "Token signing key [" + winner + "] vanished" ) );
    }

    private static String newKid()
    {
        final byte[] bytes = new byte[6];
        RANDOM.nextBytes( bytes );
        return "g" + Base64.getUrlEncoder().withoutPadding().encodeToString( bytes );
    }
}
