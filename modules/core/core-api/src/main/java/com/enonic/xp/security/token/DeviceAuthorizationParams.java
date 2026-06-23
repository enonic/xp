package com.enonic.xp.security.token;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

/**
 * Parameters to start a device authorization request (RFC 8628 section 3.1).
 */
@NullMarked
public final class DeviceAuthorizationParams
{
    private final IdProviderKey idProvider;

    @Nullable
    private final String clientId;

    @Nullable
    private final String scope;

    @Nullable
    private final String audience;

    private final long ttlSeconds;

    private final long intervalSeconds;

    private DeviceAuthorizationParams( final Builder builder )
    {
        this.idProvider = Objects.requireNonNull( builder.idProvider, "idProvider is required" );
        this.clientId = builder.clientId;
        this.scope = builder.scope;
        this.audience = builder.audience;
        this.ttlSeconds = builder.ttlSeconds;
        this.intervalSeconds = builder.intervalSeconds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProviderKey getIdProvider()
    {
        return idProvider;
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

    @Nullable
    public String getAudience()
    {
        return audience;
    }

    public long getTtlSeconds()
    {
        return ttlSeconds;
    }

    public long getIntervalSeconds()
    {
        return intervalSeconds;
    }

    public static final class Builder
    {
        @Nullable
        private IdProviderKey idProvider;

        @Nullable
        private String clientId;

        @Nullable
        private String scope;

        @Nullable
        private String audience;

        private long ttlSeconds = 600;

        private long intervalSeconds = 5;

        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
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

        public Builder audience( @Nullable final String audience )
        {
            this.audience = audience;
            return this;
        }

        public Builder ttlSeconds( final long ttlSeconds )
        {
            this.ttlSeconds = ttlSeconds;
            return this;
        }

        public Builder intervalSeconds( final long intervalSeconds )
        {
            this.intervalSeconds = intervalSeconds;
            return this;
        }

        public DeviceAuthorizationParams build()
        {
            return new DeviceAuthorizationParams( this );
        }
    }
}
