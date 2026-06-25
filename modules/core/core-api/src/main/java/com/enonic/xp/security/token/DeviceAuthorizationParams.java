package com.enonic.xp.security.token;

import java.time.Duration;
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

    private final String clientId;

    @Nullable
    private final String scope;

    @Nullable
    private final String audience;

    private final Duration ttl;

    private final Duration pollInterval;

    private DeviceAuthorizationParams( final Builder builder )
    {
        this.idProvider = Objects.requireNonNull( builder.idProvider, "idProvider is required" );
        this.clientId = Objects.requireNonNull( builder.clientId, "clientId is required" );
        this.scope = builder.scope;
        this.audience = builder.audience;
        this.ttl = builder.ttl;
        this.pollInterval = builder.pollInterval;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

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

    public Duration getTtl()
    {
        return ttl;
    }

    public Duration getPollInterval()
    {
        return pollInterval;
    }

    public static final class Builder
    {
        private IdProviderKey idProvider;

        @Nullable
        private String clientId;

        @Nullable
        private String scope;

        @Nullable
        private String audience;

        private Duration ttl = Duration.ofMinutes( 10 );

        private Duration pollInterval = Duration.ofSeconds( 5 );

        /**
         * Sets the id provider the device authorization is for. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * Sets the requesting client identifier. Required (RFC 8628 section 3.1).
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * Sets the requested scope. Optional.
         */
        public Builder scope( final String scope )
        {
            this.scope = scope;
            return this;
        }

        /**
         * Sets the requested audience (RFC 8707 resource indicator) for the issued token. Optional.
         */
        public Builder audience( final String audience )
        {
            this.audience = audience;
            return this;
        }

        /**
         * Sets how long the device/user codes remain valid ({@code expires_in}). Defaults to ten minutes.
         */
        public Builder ttl( final Duration ttl )
        {
            this.ttl = ttl;
            return this;
        }

        /**
         * Sets the minimum interval the client must wait between polls (the {@code interval} in the
         * RFC 8628 response). Defaults to five seconds.
         */
        public Builder pollInterval( final Duration pollInterval )
        {
            this.pollInterval = pollInterval;
            return this;
        }

        public DeviceAuthorizationParams build()
        {
            return new DeviceAuthorizationParams( this );
        }
    }
}
