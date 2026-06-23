package com.enonic.xp.security.token;

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

    private final long ttlSeconds;

    private AccessTokenParams( final Builder builder )
    {
        this.subject = Objects.requireNonNull( builder.subject, "subject is required" );
        this.issuer = Objects.requireNonNull( builder.issuer, "issuer is required" );
        this.audiences = ImmutableList.copyOf( builder.audiences );
        this.clientId = builder.clientId;
        this.scope = builder.scope;
        this.ttlSeconds = builder.ttlSeconds;
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

    public long getTtlSeconds()
    {
        return ttlSeconds;
    }

    public static final class Builder
    {
        @Nullable
        private PrincipalKey subject;

        @Nullable
        private String issuer;

        private final List<String> audiences = new java.util.ArrayList<>();

        @Nullable
        private String clientId;

        @Nullable
        private String scope;

        private long ttlSeconds = 3600;

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

        public Builder addAudience( @Nullable final String audience )
        {
            if ( audience != null && !audience.isEmpty() )
            {
                this.audiences.add( audience );
            }
            return this;
        }

        public Builder audiences( final Iterable<String> audiences )
        {
            audiences.forEach( this::addAudience );
            return this;
        }

        public Builder clientId( @Nullable final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        public Builder scope( @Nullable final String scope )
        {
            this.scope = scope;
            return this;
        }

        public Builder ttlSeconds( final long ttlSeconds )
        {
            this.ttlSeconds = ttlSeconds;
            return this;
        }

        public AccessTokenParams build()
        {
            return new AccessTokenParams( this );
        }
    }
}
