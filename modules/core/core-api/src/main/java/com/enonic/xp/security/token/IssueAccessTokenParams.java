package com.enonic.xp.security.token;

import java.time.Duration;
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
 * What to put into an access token: see {@link AccessTokenClaims} for the meaning of each field.
 * The issuer, the timestamps and the token id are the service's to fill in.
 */
@PublicApi
@NullMarked
public final class IssueAccessTokenParams
{
    private final PrincipalKey subject;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final PrincipalRestriction restriction;

    private final @Nullable PrincipalKey actor;

    private final @Nullable String clientId;

    private final @Nullable String grantId;

    private final @Nullable String sid;

    private final @Nullable Duration ttl;

    private IssueAccessTokenParams( final Builder builder )
    {
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.audience = builder.audience.build();
        this.scope = builder.scope.build();
        this.restriction = builder.restriction;
        this.actor = builder.actor;
        this.clientId = builder.clientId;
        this.grantId = builder.grantId;
        this.sid = builder.sid;
        this.ttl = builder.ttl;
        checkArgument( ttl == null || ( ttl.getNano() == 0 && ttl.getSeconds() >= 1 ), "ttl must be a whole number of seconds, at least one: %s",
                       ttl );
    }

    /**
     * The user the token stands for.
     */
    public PrincipalKey getSubject()
    {
        return subject;
    }

    /**
     * The audience names the token names; empty for none.
     */
    public Set<String> getAudience()
    {
        return audience;
    }

    /**
     * The scopes granted to the token; empty for none.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The restriction on the memberships a request with the token runs with. See
     * {@link AccessTokenClaims#getRestriction()}.
     */
    public PrincipalRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * The principal acting on the subject's behalf, or null when the subject acts for itself.
     */
    public @Nullable PrincipalKey getActor()
    {
        return actor;
    }

    /**
     * The client the token is issued to, or null when unknown.
     */
    public @Nullable String getClientId()
    {
        return clientId;
    }

    /**
     * The grant the token is issued under, or null when it stands alone.
     */
    public @Nullable String getGrantId()
    {
        return grantId;
    }

    /**
     * The user's sid the token is issued under, or null for none. See
     * {@link AccessTokenClaims#getSid()}.
     */
    public @Nullable String getSid()
    {
        return sid;
    }

    /**
     * How long the token is valid, a whole number of seconds, or null for the service's configured default.
     */
    public @Nullable Duration getTtl()
    {
        return ttl;
    }

    /**
     * A builder; the subject is required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable PrincipalKey subject;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private PrincipalRestriction restriction = PrincipalRestriction.none();

        private @Nullable PrincipalKey actor;

        private @Nullable String clientId;

        private @Nullable String grantId;

        private @Nullable String sid;

        private @Nullable Duration ttl;

        private Builder()
        {
        }

        /**
         * The user the token stands for. Required.
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
         * Adds audience names; see {@link AccessTokenClaims#getAudience()}.
         */
        public Builder audience( final String... audience )
        {
            this.audience.add( audience );
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
         * Adds scopes; see {@link AccessTokenClaims#getScope()}.
         *
         * @throws IllegalArgumentException when a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final String... scope )
        {
            for ( String s : scope )
            {
                this.scope.add( Scopes.checkScope( s ) );
            }
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
         * The client the token is issued to; null (the default) when unknown.
         */
        public Builder clientId( final @Nullable String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * The grant the token is issued under; null (the default) when it stands alone.
         */
        public Builder grantId( final @Nullable String grantId )
        {
            this.grantId = grantId;
            return this;
        }

        /**
         * The user's sid the token is issued under; null (the default) for none. See
         * {@link AccessTokenClaims#getSid()}.
         */
        public Builder sid( final @Nullable String sid )
        {
            this.sid = sid;
            return this;
        }

        /**
         * How long the token is valid; null (the default) for the service's configured default.
         */
        public Builder ttl( final @Nullable Duration ttl )
        {
            this.ttl = ttl;
            return this;
        }

        /**
         * Builds the params.
         * 
         * @throws NullPointerException     when the subject is missing
         * @throws IllegalArgumentException when the subject is not a user or the ttl is not a whole number of seconds, at least one
         */
        public IssueAccessTokenParams build()
        {
            return new IssueAccessTokenParams( this );
        }
    }
}
