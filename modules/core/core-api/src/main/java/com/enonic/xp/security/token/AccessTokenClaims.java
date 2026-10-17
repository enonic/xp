package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRestriction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * The claims of an access token: who it stands for, where it may be used, what it may do, and for
 * how long. A value, with no say in whether a token carrying it holds; that is what
 * {@link AccessTokenService#verify} establishes.
 */
@PublicApi
@NullMarked
public final class AccessTokenClaims
{
    private final String issuer;

    private final PrincipalKey subject;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final PrincipalRestriction restriction;

    private final @Nullable PrincipalKey actor;

    private final @Nullable String clientId;

    private final @Nullable String grantId;

    private final @Nullable String sid;

    private final Instant issuedAt;

    private final Instant expiresAt;

    private final String tokenId;

    private AccessTokenClaims( final Builder builder )
    {
        this.issuer = requireNonNull( builder.issuer, "issuer is required" );
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.audience = builder.audience.build();
        this.scope = builder.scope.build();
        this.restriction = builder.restriction;
        this.actor = builder.actor;
        this.clientId = builder.clientId;
        this.grantId = builder.grantId;
        this.sid = builder.sid;
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
     * The audiences the token may be presented to ({@code aud}): opaque names, matched against
     * what a deployment declares it serves. Empty when the token names no audience.
     */
    public Set<String> getAudience()
    {
        return audience;
    }

    /**
     * The scopes granted to the token ({@code scope}): {@link Scopes scope names} interpreted by
     * applications, not by XP. Empty when none were granted.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The restriction on the memberships a request with the token runs with. A ceiling: at use
     * time it is applied to the user's current memberships, so a role removed from the user is
     * removed from the token too.
     */
    public PrincipalRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * The principal acting on the subject's behalf ({@code act}), or null when the subject acts
     * for itself.
     */
    public @Nullable PrincipalKey getActor()
    {
        return actor;
    }

    /**
     * The client the token was issued to ({@code client_id}), or null when unknown.
     */
    public @Nullable String getClientId()
    {
        return clientId;
    }

    /**
     * The grant the token was issued under, or null when the token stands alone.
     */
    public @Nullable String getGrantId()
    {
        return grantId;
    }

    /**
     * The user's sid the token was issued under, or null when it has none. A sid is an opaque
     * random id every token of a user carries; it is replaced when all the user's grants are
     * revoked or the user is recreated, so a token carrying an old one is to be rejected by
     * whoever checks tokens against the user's current sid.
     */
    public @Nullable String getSid()
    {
        return sid;
    }

    /**
     * When the token was issued ({@code iat}).
     */
    public Instant getIssuedAt()
    {
        return issuedAt;
    }

    /**
     * When the token expires ({@code exp}); a verifier rejects it after this.
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
            audience.equals( that.audience ) && scope.equals( that.scope ) && restriction.equals( that.restriction ) &&
            Objects.equals( actor, that.actor ) && Objects.equals( clientId, that.clientId ) && Objects.equals( grantId, that.grantId ) &&
            Objects.equals( sid, that.sid ) &&
            issuedAt.equals( that.issuedAt ) && expiresAt.equals( that.expiresAt ) && tokenId.equals( that.tokenId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( issuer, subject, audience, scope, restriction, actor, clientId, grantId, sid, issuedAt, expiresAt, tokenId );
    }

    @Override
    public String toString()
    {
        return "AccessTokenClaims{" + "issuer=" + issuer + ", subject=" + subject + ", audience=" + audience + ", scope=" + scope +
            ", restriction=" + restriction + ", actor=" + actor + ", clientId=" + clientId + ", grantId=" + grantId + ", sid=" + sid + ", issuedAt=" +
            issuedAt + ", expiresAt=" + expiresAt + ", tokenId=" + tokenId + '}';
    }

    public static final class Builder
    {
        private @Nullable String issuer;

        private @Nullable PrincipalKey subject;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private PrincipalRestriction restriction = PrincipalRestriction.none();

        private @Nullable PrincipalKey actor;

        private @Nullable String clientId;

        private @Nullable String grantId;

        private @Nullable String sid;

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
         * The user the token stands for. Required, and must be a user key.
         */
        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        /**
         * Adds audience names; see {@link AccessTokenClaims#getAudience()}.
         */
        public Builder audience( final Iterable<String> audience )
        {
            this.audience.addAll( audience );
            return this;
        }

        /**
         * Adds scopes; see {@link AccessTokenClaims#getScope()}.
         *
         * @throws IllegalArgumentException when a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( Scopes.checkScope( s ) ) );
            return this;
        }

        /**
         * The principal restriction; none by default. See {@link AccessTokenClaims#getRestriction()}.
         */
        public Builder restriction( final PrincipalRestriction restriction )
        {
            this.restriction = restriction;
            return this;
        }

        /**
         * The principal acting on the subject's behalf; null (the default) when the subject acts for itself.
         */
        public Builder actor( final @Nullable PrincipalKey actor )
        {
            this.actor = actor;
            return this;
        }

        /**
         * The client the token was issued to; null (the default) when unknown.
         */
        public Builder clientId( final @Nullable String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * The grant the token was issued under; null (the default) when it stands alone.
         */
        public Builder grantId( final @Nullable String grantId )
        {
            this.grantId = grantId;
            return this;
        }

        /**
         * The user's sid the token is issued under; null (the default) for none. See {@link #getSid()}.
         */
        public Builder sid( final @Nullable String sid )
        {
            this.sid = sid;
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
