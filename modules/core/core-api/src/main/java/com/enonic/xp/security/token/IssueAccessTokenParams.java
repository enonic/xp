package com.enonic.xp.security.token;

import java.time.Duration;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * What to put into an access token; see {@link AccessTokenClaims} for each claim. The issuer, the
 * timestamps and the token id are filled in by the service.
 */
@PublicApi
@NullMarked
public final class IssueAccessTokenParams
{
    private final PrincipalKey subject;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final @Nullable PrincipalKey actor;

    private final String clientId;

    private final @Nullable String grantId;

    private final @Nullable Duration ttl;

    private IssueAccessTokenParams( final Builder builder )
    {
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.audience = builder.audience.build();
        checkArgument( !this.audience.isEmpty(), "audience is required" );
        this.scope = builder.scope.build();
        this.actor = builder.actor;
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        checkArgument( !this.clientId.isBlank(), "clientId is required" );
        this.grantId = builder.grantId;
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
     * The audiences the token names; at least one.
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
     * The principal acting on the subject's behalf, or null.
     */
    public @Nullable PrincipalKey getActor()
    {
        return actor;
    }

    /**
     * The client the token is issued to.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * The grant the token is issued under, or null.
     */
    public @Nullable String getGrantId()
    {
        return grantId;
    }

    /**
     * How long the token is valid, in whole seconds, or null for the configured default.
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

        private @Nullable PrincipalKey actor;

        private @Nullable String clientId;

        private @Nullable String grantId;

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
         * Adds audiences.
         */
        public Builder audience( final Iterable<String> audience )
        {
            this.audience.addAll( audience );
            return this;
        }

        /**
         * Adds audiences.
         */
        public Builder audience( final String... audience )
        {
            this.audience.add( audience );
            return this;
        }

        /**
         * Adds scopes.
         *
         * @throws IllegalArgumentException if a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( Scopes.checkScope( s ) ) );
            return this;
        }

        /**
         * Adds scopes.
         *
         * @throws IllegalArgumentException if a scope is not a {@link Scopes scope name}
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
         * The principal acting on the subject's behalf.
         */
        public Builder actor( final @Nullable PrincipalKey actor )
        {
            this.actor = actor;
            return this;
        }

        /**
         * The client the token is issued to. Required.
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * The grant the token is issued under.
         */
        public Builder grantId( final @Nullable String grantId )
        {
            this.grantId = grantId;
            return this;
        }

        /**
         * How long the token is valid; null (the default) for the configured default.
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
