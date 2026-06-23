package com.enonic.xp.security.token;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

/**
 * Parameters for issuing an access token. The subject is the principal the token authenticates
 * as; the audiences (RFC 8707 resource indicators) constrain where the token may be accepted.
 */
public final class AccessTokenParams
{
    private final PrincipalKey subject;

    private final IdProviderKey idProvider;

    private final String issuer;

    private final ImmutableList<String> audiences;

    private final String clientId;

    private final String scope;

    private final long ttlSeconds;

    private AccessTokenParams( final Builder builder )
    {
        this.subject = Objects.requireNonNull( builder.subject, "subject is required" );
        this.idProvider = builder.idProvider;
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

    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    public String getIssuer()
    {
        return issuer;
    }

    public List<String> getAudiences()
    {
        return audiences;
    }

    public String getClientId()
    {
        return clientId;
    }

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
        private PrincipalKey subject;

        private IdProviderKey idProvider;

        private String issuer;

        private final List<String> audiences = new java.util.ArrayList<>();

        private String clientId;

        private String scope;

        private long ttlSeconds = 3600;

        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        public Builder issuer( final String issuer )
        {
            this.issuer = issuer;
            return this;
        }

        public Builder addAudience( final String audience )
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

        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        public Builder scope( final String scope )
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
