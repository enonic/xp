package com.enonic.xp.security.auth;

import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

/**
 * The access token a request was authenticated with: issuer, client, grant, scopes and
 * audiences. Set by whoever accepted the token: XP's bearer filter for XP's own tokens, or an id
 * provider that verified another issuer's token and logged the user in. Nothing here is verified
 * again.
 */
@PublicApi
@NullMarked
public final class AccessTokenInfo
{
    private final String issuer;

    private final @Nullable String clientId;

    private final @Nullable String grantId;

    private final ImmutableSet<String> scope;

    private final ImmutableSet<String> audience;

    private AccessTokenInfo( final Builder builder )
    {
        this.issuer = requireNonNull( builder.issuer, "issuer is required" );
        this.clientId = builder.clientId;
        this.grantId = builder.grantId;
        this.scope = builder.scope.build();
        this.audience = builder.audience.build();
    }

    /**
     * Who issued the token.
     */
    public String getIssuer()
    {
        return issuer;
    }

    /**
     * The client the token was issued to, or null if the token does not say.
     */
    public @Nullable String getClientId()
    {
        return clientId;
    }

    /**
     * The XP grant the token was issued under, or null. Tokens of other issuers have no grant.
     */
    public @Nullable String getGrantId()
    {
        return grantId;
    }

    /**
     * The scopes the request was granted; empty if none.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The token's audiences; empty if none.
     */
    public Set<String> getAudience()
    {
        return audience;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof AccessTokenInfo that && issuer.equals( that.issuer ) && Objects.equals( clientId, that.clientId ) &&
            Objects.equals( grantId, that.grantId ) && scope.equals( that.scope ) && audience.equals( that.audience );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( issuer, clientId, grantId, scope, audience );
    }

    @Override
    public String toString()
    {
        return "AccessTokenInfo{" + "issuer=" + issuer + ", clientId=" + clientId + ", grantId=" + grantId + ", scope=" + scope + ", audience=" +
            audience + '}';
    }

    /**
     * Builds an {@link AccessTokenInfo}. Only the issuer is required.
     */
    public static final class Builder
    {
        private @Nullable String issuer;

        private @Nullable String clientId;

        private @Nullable String grantId;

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private Builder()
        {
        }

        /**
         * The issuer. Required.
         */
        public Builder issuer( final String issuer )
        {
            this.issuer = issuer;
            return this;
        }

        /**
         * The client the token was issued to.
         */
        public Builder clientId( final @Nullable String clientId )
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
         * Adds scopes.
         */
        public Builder scope( final Iterable<String> scope )
        {
            this.scope.addAll( scope );
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

        public AccessTokenInfo build()
        {
            return new AccessTokenInfo( this );
        }
    }
}
