package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

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

    private final PropertyTree claims;

    private AccessToken( final Builder builder )
    {
        this.subject = builder.subject;
        this.issuer = builder.issuer;
        this.audiences = ImmutableSet.copyOf( builder.audiences );
        this.expiresAt = builder.expiresAt;
        this.claims = builder.claims;
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
     * The full set of token claims. Values are typed JSON values and are never {@code null}.
     */
    public PropertyTree getClaims()
    {
        return claims;
    }

    public static final class Builder
    {
        @Nullable
        private PrincipalKey subject;

        @Nullable
        private String issuer;

        private Set<String> audiences = Set.of();

        @Nullable
        private Instant expiresAt;

        private PropertyTree claims = new PropertyTree();

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

        public Builder claims( final PropertyTree claims )
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
