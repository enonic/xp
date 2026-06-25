package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.GenericValue;

/**
 * A verified access token. The signature, issuer and expiry have already been validated;
 * audience enforcement is left to the caller via {@link #audiences()}. The subject is always a
 * user principal, which also identifies the id provider via {@link PrincipalKey#getIdProviderKey()}.
 * The {@code claims} are an immutable JSON object ({@link GenericValue.Type#OBJECT}); values are
 * typed JSON values and never null.
 */
@NullMarked
public record AccessToken(PrincipalKey subject, String issuer, Set<String> audiences, Instant expiresAt, GenericValue claims)
{
    public AccessToken
    {
        Objects.requireNonNull( subject, "subject is required" );
        Objects.requireNonNull( issuer, "issuer is required" );
        audiences = ImmutableSet.copyOf( audiences );
        Objects.requireNonNull( expiresAt, "expiresAt is required" );
        Objects.requireNonNull( claims, "claims is required" );
        if ( claims.getType() != GenericValue.Type.OBJECT )
        {
            throw new IllegalArgumentException( "claims must be a JSON object" );
        }
    }
}
