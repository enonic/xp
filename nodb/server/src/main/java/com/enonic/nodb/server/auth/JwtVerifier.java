package com.enonic.nodb.server.auth;

import java.security.interfaces.RSAPublicKey;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Verifies an RS256 JWT against the configured public key: signature, expiry, and
 * audience ({@code nodb}) — all offline, no per-request callout to an issuer (DESIGN.md
 * §7.2). Extracts the {@code tenant}/{@code scope}/{@code subject}/{@code jti} claims into
 * a {@link TenantPrincipal}. Does NOT itself validate the tenant id's shape — callers
 * (the {@link TenantAuthInterceptor}) run the result through {@link TenantPrincipal#tenantContext()}
 * for that, so a malformed/unrecognized tenant claim is rejected the same way an invalid
 * token is.
 */
public final class JwtVerifier
{
    private final Algorithm algorithm;

    public JwtVerifier( RSAPublicKey publicKey )
    {
        // Verification only needs the public key; passing null for the private key half
        // means this instance can never be (mis)used to sign.
        this.algorithm = Algorithm.RSA256( publicKey, null );
    }

    /** @throws JWTVerificationException on a missing/invalid/expired/wrong-audience token, or one missing required claims. */
    public TenantPrincipal verify( String token )
    {
        DecodedJWT decoded = JWT.require( algorithm ).withAudience( JwtIssuer.AUDIENCE ).build().verify( token );

        String tenant = decoded.getClaim( "tenant" ).asString();
        String scopeClaim = decoded.getClaim( "scope" ).asString();
        if ( tenant == null || tenant.isBlank() )
        {
            throw new JWTVerificationException( "Token missing required 'tenant' claim" );
        }
        Scope scope;
        try
        {
            scope = Scope.fromClaim( scopeClaim );
        }
        catch ( IllegalArgumentException e )
        {
            throw new JWTVerificationException( e.getMessage(), e );
        }

        return new TenantPrincipal( tenant, scope, decoded.getSubject(), decoded.getId() );
    }
}
