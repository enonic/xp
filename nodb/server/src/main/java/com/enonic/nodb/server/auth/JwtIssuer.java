package com.enonic.nodb.server.auth;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * Mints RS256 tokens for the trivial dev issuer (DESIGN.md §7.2: audience {@code nodb},
 * claims {@code tenant}/{@code scope}/{@code subject}/{@code jti}). In a real deployment
 * this logic lives in the control plane; here it backs {@link NodbTokenTool} directly.
 */
public final class JwtIssuer
{
    public static final String AUDIENCE = "nodb";

    private JwtIssuer()
    {
    }

    public static String mint( RSAPrivateKey privateKey, RSAPublicKey publicKey, String tenantId, Scope scope, String subject,
                                Duration ttl )
    {
        Instant now = Instant.now();
        Algorithm algorithm = Algorithm.RSA256( publicKey, privateKey );
        return JWT.create()
            .withAudience( AUDIENCE )
            .withSubject( subject )
            .withJWTId( UUID.randomUUID().toString() )
            .withClaim( "tenant", tenantId )
            .withClaim( "scope", scope.name().toLowerCase() )
            .withIssuedAt( Date.from( now ) )
            .withExpiresAt( Date.from( now.plus( ttl ) ) )
            .sign( algorithm );
    }
}
