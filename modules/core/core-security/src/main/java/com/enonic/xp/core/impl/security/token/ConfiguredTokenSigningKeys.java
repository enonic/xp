package com.enonic.xp.core.impl.security.token;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

/**
 * Keys the operator provides in {@code com.enonic.xp.security.cfg}:
 * <pre>
 * token.keys.2026a = &lt;base64 secret, at least 64 bytes&gt;
 * token.keys.2026b = &lt;base64 secret&gt;
 * token.signingKey = 2026b
 * </pre>
 * Every listed key verifies; {@code token.signingKey} names the one that signs, and may be omitted
 * when exactly one key is listed. Rotation is adding a key, moving {@code token.signingKey} to it,
 * and dropping the old key once the tokens it signed have expired.
 */
final class ConfiguredTokenSigningKeys
    implements TokenSigningKeys
{
    static final String KEYS_PREFIX = "token.keys.";

    static final String SIGNING_KEY = "token.signingKey";

    private final SigningKey signingKey;

    private final ImmutableMap<String, SigningKey> keys;

    private ConfiguredTokenSigningKeys( final SigningKey signingKey, final ImmutableMap<String, SigningKey> keys )
    {
        this.signingKey = signingKey;
        this.keys = keys;
    }

    /**
     * Builds the key set from config, or returns empty when the config lists no keys.
     */
    static Optional<ConfiguredTokenSigningKeys> from( final Map<String, String> config )
    {
        final ImmutableMap.Builder<String, SigningKey> keys = ImmutableMap.builder();
        config.forEach( ( name, value ) -> {
            if ( name.startsWith( KEYS_PREFIX ) )
            {
                final String kid = name.substring( KEYS_PREFIX.length() );
                keys.put( kid, new SigningKey( kid, decode( kid, value ) ) );
            }
        } );
        final ImmutableMap<String, SigningKey> keyMap = keys.buildOrThrow();
        if ( keyMap.isEmpty() )
        {
            if ( config.containsKey( SIGNING_KEY ) )
            {
                throw new IllegalArgumentException( SIGNING_KEY + " names [" + config.get( SIGNING_KEY ) + "] but no " + KEYS_PREFIX +
                                                        "<kid> is configured" );
            }
            return Optional.empty();
        }

        final String signingKid = config.get( SIGNING_KEY );
        final SigningKey signingKey;
        if ( signingKid == null )
        {
            if ( keyMap.size() != 1 )
            {
                throw new IllegalArgumentException(
                    SIGNING_KEY + " is required when more than one key is configured: " + keyMap.keySet() );
            }
            signingKey = keyMap.values().iterator().next();
        }
        else
        {
            signingKey = keyMap.get( signingKid.trim() );
            if ( signingKey == null )
            {
                throw new IllegalArgumentException(
                    SIGNING_KEY + " names [" + signingKid + "] which is not among the configured keys " + keyMap.keySet() );
            }
        }
        return Optional.of( new ConfiguredTokenSigningKeys( signingKey, keyMap ) );
    }

    private static byte[] decode( final String kid, final String value )
    {
        try
        {
            return Base64.getDecoder().decode( value.trim() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( KEYS_PREFIX + kid + " is not valid base64", e );
        }
    }

    @Override
    public SigningKey signingKey()
    {
        return signingKey;
    }

    @Override
    public Optional<SigningKey> verificationKey( final String kid )
    {
        return Optional.ofNullable( keys.get( kid ) );
    }
}
