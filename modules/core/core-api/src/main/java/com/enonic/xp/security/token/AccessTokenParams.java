package com.enonic.xp.security.token;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.security.PrincipalKey;

/**
 * Parameters for issuing an access token. The subject is the principal the token authenticates
 * as (it also identifies the id provider); the audiences (RFC 8707 resource indicators)
 * constrain where the token may be accepted.
 */
@NullMarked
public final class AccessTokenParams
{
    private final PrincipalKey subject;

    private final String issuer;

    private final ImmutableList<String> audiences;

    @Nullable
    private final String clientId;

    @Nullable
    private final String scope;

    private final Duration ttl;

    private AccessTokenParams( final Builder builder )
    {
        this.subject = Objects.requireNonNull( builder.subject, "subject is required" );
        this.issuer = Objects.requireNonNull( builder.issuer, "issuer is required" );
        this.audiences = builder.audiences.build();
        this.clientId = builder.clientId;
        this.scope = builder.scope;
        this.ttl = builder.ttl;
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

    public List<String> getAudiences()
    {
        return audiences;
    }

    @Nullable
    public String getClientId()
    {
        return clientId;
    }

    @Nullable
    public String getScope()
    {
        return scope;
    }

    public Duration getTtl()
    {
        return ttl;
    }

    public static final class Builder
    {
        private PrincipalKey subject;

        private String issuer;

        private final ImmutableList.Builder<String> audiences = ImmutableList.builder();

        @Nullable
        private String clientId;

        @Nullable
        private String scope;

        private Duration ttl = Duration.ofHours( 1 );

        /**
         * Sets the principal the token authenticates as. Required; must be a user (it also
         * identifies the id provider).
         */
        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        /**
         * Sets the token issuer (the {@code iss} claim). Required.
         */
        public Builder issuer( final String issuer )
        {
            this.issuer = issuer;
            return this;
        }

        /**
         * Adds an audience (RFC 8707 resource indicator) that the token is valid for. May be called
         * more than once.
         */
        public Builder addAudience( final String audience )
        {
            this.audiences.add( Objects.requireNonNull( audience, "audience is required" ) );
            return this;
        }

        /**
         * Adds all the given audiences (RFC 8707 resource indicators).
         */
        public Builder audiences( final Iterable<String> audiences )
        {
            audiences.forEach( this::addAudience );
            return this;
        }

        /**
         * Sets the client identifier (the {@code client_id} claim). Optional.
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * Sets the space-delimited scope (the {@code scope} claim). Optional.
         */
        public Builder scope( final String scope )
        {
            this.scope = scope;
            return this;
        }

        /**
         * Sets the token lifetime, used to compute the {@code exp} claim. Defaults to one hour.
         */
        public Builder ttl( final Duration ttl )
        {
            this.ttl = ttl;
            return this;
        }

        public AccessTokenParams build()
        {
            return new AccessTokenParams( this );
        }
    }
}
