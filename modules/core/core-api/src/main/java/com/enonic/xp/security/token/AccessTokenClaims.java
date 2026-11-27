package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * The claims of an access token (RFC 9068): who it stands for, where it may be used, for how long,
 * and under which grant.
 */
@PublicApi
@NullMarked
public final class AccessTokenClaims
{
    private final String issuer;

    private final PrincipalKey subject;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final @Nullable PrincipalKey actor;

    private final String clientId;

    private final @Nullable String grantId;

    private final Instant issuedAt;

    private final Instant expiresAt;

    private final String tokenId;

    private AccessTokenClaims( final Builder builder )
    {
        this.issuer = requireNonNull( builder.issuer, "issuer is required" );
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.audience = builder.audience.build();
        checkArgument( !this.audience.isEmpty(), "audience is required" );
        this.scope = builder.scope.build();
        this.actor = builder.actor;
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        checkArgument( !this.clientId.isBlank(), "clientId is required" );
        this.grantId = builder.grantId;
        this.issuedAt = requireNonNull( builder.issuedAt, "issuedAt is required" );
        this.expiresAt = requireNonNull( builder.expiresAt, "expiresAt is required" );
        this.tokenId = requireNonNull( builder.tokenId, "tokenId is required" );
    }

    /**
     * The issuer the token names ({@code iss}).
     */
    public String getIssuer()
    {
        return issuer;
    }

    /**
     * The user the token stands for ({@code sub}).
     */
    public PrincipalKey getSubject()
    {
        return subject;
    }

    /**
     * The audiences the token may be presented to ({@code aud}); at least one.
     */
    public Set<String> getAudience()
    {
        return audience;
    }

    /**
     * The scopes granted to the token ({@code scope}); empty if none.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The principal acting on the subject's behalf ({@code act}), or null.
     */
    public @Nullable PrincipalKey getActor()
    {
        return actor;
    }

    /**
     * The client the token was issued to ({@code client_id}).
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * The grant the token was issued under ({@code grant}), or null.
     */
    public @Nullable String getGrantId()
    {
        return grantId;
    }

    /**
     * When the token was issued ({@code iat}).
     */
    public Instant getIssuedAt()
    {
        return issuedAt;
    }

    /**
     * When the token expires ({@code exp}).
     */
    public Instant getExpiresAt()
    {
        return expiresAt;
    }

    /**
     * The token's unique id ({@code jti}).
     */
    public String getTokenId()
    {
        return tokenId;
    }

    /**
     * A builder; the issuer, subject, timestamps and token id are required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof AccessTokenClaims that && issuer.equals( that.issuer ) && subject.equals( that.subject ) &&
            audience.equals( that.audience ) && scope.equals( that.scope ) &&
            Objects.equals( actor, that.actor ) && clientId.equals( that.clientId ) && Objects.equals( grantId, that.grantId ) &&
            issuedAt.equals( that.issuedAt ) && expiresAt.equals( that.expiresAt ) && tokenId.equals( that.tokenId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( issuer, subject, audience, scope, actor, clientId, grantId, issuedAt, expiresAt, tokenId );
    }

    @Override
    public String toString()
    {
        return "AccessTokenClaims{" + "issuer=" + issuer + ", subject=" + subject + ", audience=" + audience + ", scope=" + scope +
            ", actor=" + actor + ", clientId=" + clientId + ", grantId=" + grantId + ", issuedAt=" +
            issuedAt + ", expiresAt=" + expiresAt + ", tokenId=" + tokenId + '}';
    }

    public static final class Builder
    {
        private @Nullable String issuer;

        private @Nullable PrincipalKey subject;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private @Nullable PrincipalKey actor;

        private @Nullable String clientId;

        private @Nullable String grantId;

        private @Nullable Instant issuedAt;

        private @Nullable Instant expiresAt;

        private @Nullable String tokenId;

        private Builder()
        {
        }

        /**
         * The issuer the token names. Required.
         */
        public Builder issuer( final String issuer )
        {
            this.issuer = issuer;
            return this;
        }

        /**
         * The user the token stands for. Required; must be a user key.
         */
        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        /**
         * Adds audiences.
         */
        public Builder audience( final Iterable<String> audience )
        {
            this.audience.addAll( audience );
            return this;
        }

        /**
         * Adds scopes.
         *
         * @throws IllegalArgumentException if a scope is not a {@link ScopeNames scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( ScopeNames.checkScope( s ) ) );
            return this;
        }

        /**
         * The principal acting on the subject's behalf.
         */
        public Builder actor( final @Nullable PrincipalKey actor )
        {
            this.actor = actor;
            return this;
        }

        /**
         * The client the token was issued to. Required.
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * The grant the token was issued under.
         */
        public Builder grantId( final @Nullable String grantId )
        {
            this.grantId = grantId;
            return this;
        }

        /**
         * When the token was issued. Required.
         */
        public Builder issuedAt( final Instant issuedAt )
        {
            this.issuedAt = issuedAt;
            return this;
        }

        /**
         * When the token expires. Required.
         */
        public Builder expiresAt( final Instant expiresAt )
        {
            this.expiresAt = expiresAt;
            return this;
        }

        /**
         * The token's unique id. Required.
         */
        public Builder tokenId( final String tokenId )
        {
            this.tokenId = tokenId;
            return this;
        }

        /**
         * Builds the claims.
         * 
         * @throws NullPointerException     when a required value is missing
         * @throws IllegalArgumentException when the subject is not a user
         */
        public AccessTokenClaims build()
        {
            return new AccessTokenClaims( this );
        }
    }
}
