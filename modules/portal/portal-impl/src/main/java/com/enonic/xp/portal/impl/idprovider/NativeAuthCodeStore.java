package com.enonic.xp.portal.impl.idprovider;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

/**
 * Internal, cluster-shared store for native-app (RFC 8252) authorization codes.
 * <p>
 * Codes are high-entropy, single-use and expire via TTL (so there is no cleanup job and no secret
 * left at rest). This is deliberately not a public service - only {@link DeviceLoginHandler} uses
 * it. The device-grant (RFC 8628) lifecycle has its own public service ({@code DeviceAuthService});
 * the authorization-code grant is kept here until/unless it warrants its own core service.
 */
@NullMarked
final class NativeAuthCodeStore
{
    private static final String MAP_PREFIX = "com.enonic.xp.portal.idprovider.authcode.";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Base64.Encoder B64URL_NOPAD = Base64.getUrlEncoder().withoutPadding();

    private final SharedMapService sharedMapService;

    NativeAuthCodeStore( final SharedMapService sharedMapService )
    {
        this.sharedMapService = sharedMapService;
    }

    /**
     * Stores a one-time authorization code and returns it.
     */
    String create( final IdProviderKey idProvider, final AuthCode authCode, final int ttlSeconds )
    {
        final String code = generateCode();

        final HashMap<String, Object> record = new HashMap<>();
        record.put( "challenge", authCode.challenge() );
        record.put( "redirectUri", authCode.redirectUri() );
        record.put( "subject", authCode.subject() );
        record.put( "clientId", authCode.clientId() );
        record.put( "scope", authCode.scope() );
        record.put( "audience", authCode.audience() );

        getMap( idProvider ).set( code, record, ttlSeconds );
        return code;
    }

    /**
     * Atomically reads and removes a code (single use). Returns {@code null} if unknown/expired.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    AuthCode consume( final IdProviderKey idProvider, final String code )
    {
        final Object[] holder = new Object[1];
        getMap( idProvider ).modify( code, value -> {
            if ( value instanceof Map )
            {
                holder[0] = value;
            }
            return null; // always remove
        } );

        if ( holder[0] == null )
        {
            return null;
        }
        final Map<String, Object> record = (Map<String, Object>) holder[0];
        return new AuthCode( str( record, "challenge" ), str( record, "redirectUri" ), str( record, "subject" ),
                             str( record, "clientId" ), str( record, "scope" ), str( record, "audience" ) );
    }

    private SharedMap<String, Object> getMap( final IdProviderKey idProvider )
    {
        return sharedMapService.getSharedMap( MAP_PREFIX + idProvider );
    }

    private static String generateCode()
    {
        final byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes( bytes );
        return B64URL_NOPAD.encodeToString( bytes );
    }

    private static String str( final Map<String, Object> record, final String key )
    {
        final Object value = record.get( key );
        return value == null ? "" : value.toString();
    }

    record AuthCode(String challenge, String redirectUri, String subject, String clientId, String scope, String audience)
    {
    }
}
