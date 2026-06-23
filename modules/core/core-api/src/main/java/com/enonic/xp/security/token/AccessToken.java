package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.GenericValue;

/**
 * A verified access token. The signature, issuer and expiry have already been validated;
 * audience enforcement is left to the caller via {@link #getAudiences()}. The subject is always a
 * user principal, which also identifies the id provider via {@link PrincipalKey#getIdProviderKey()}.
 */
@NullMarked
public final class AccessToken
{
    private final PrincipalKey subject;

    private final String issuer;

    private final ImmutableSet<String> audiences;

    private final Instant expiresAt;

    private final ImmutableMap<String, GenericValue> claims;

    private AccessToken( final Builder builder )
    {
        this.subject = Objects.requireNonNull( builder.subject, "subject is required" );
        this.issuer = Objects.requireNonNull( builder.issuer, "issuer is required" );
        this.audiences = ImmutableSet.copyOf( builder.audiences );
        this.expiresAt = Objects.requireNonNull( builder.expiresAt, "expiresAt is required" );
        this.claims = ImmutableMap.copyOf( builder.claims );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PrincipalKey getSubject()
    {
        return subject;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public Set<String> getAudiences()
    {
        return audiences;
    }

    public Instant getExpiresAt()
    {
        return expiresAt;
    }

    /**
     * The token claims as an immutable JSON object (claim name to value). Values are typed JSON
     * values and are never null.
     */
    public Map<String, GenericValue> getClaims()
    {
        return claims;
    }

    public static final class Builder
    {
        private PrincipalKey subject;

        private String issuer;

        private Set<String> audiences = Set.of();

        private Instant expiresAt;

        private Map<String, GenericValue> claims = Map.of();

        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        public Builder issuer( final String issuer )
        {
            this.issuer = issuer;
            return this;
        }

        public Builder audiences( final Set<String> audiences )
        {
            this.audiences = audiences;
            return this;
        }

        public Builder expiresAt( final Instant expiresAt )
        {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder claims( final Map<String, GenericValue> claims )
        {
            this.claims = claims;
            return this;
        }

        public AccessToken build()
        {
            return new AccessToken( this );
        }
    }
}
