package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

/**
 * The result of polling a device authorization request. When the state is
 * {@link DeviceAuthorizationState#APPROVED} the approved subject and the original request
 * details (used to mint the access token) are populated.
 */
@NullMarked
public final class DeviceAuthorizationPoll
{
    private final DeviceAuthorizationState state;

    @Nullable
    private final PrincipalKey subject;

    @Nullable
    private final IdProviderKey idProvider;

    @Nullable
    private final String audience;

    @Nullable
    private final String scope;

    @Nullable
    private final String clientId;

    private DeviceAuthorizationPoll( final Builder builder )
    {
        this.state = builder.state;
        this.subject = builder.subject;
        this.idProvider = builder.idProvider;
        this.audience = builder.audience;
        this.scope = builder.scope;
        this.clientId = builder.clientId;
    }

    public static DeviceAuthorizationPoll of( final DeviceAuthorizationState state )
    {
        return new Builder().state( state ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DeviceAuthorizationState getState()
    {
        return state;
    }

    @Nullable
    public PrincipalKey getSubject()
    {
        return subject;
    }

    @Nullable
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    @Nullable
    public String getAudience()
    {
        return audience;
    }

    @Nullable
    public String getScope()
    {
        return scope;
    }

    @Nullable
    public String getClientId()
    {
        return clientId;
    }

    public static final class Builder
    {
        @Nullable
        private DeviceAuthorizationState state;

        @Nullable
        private PrincipalKey subject;

        @Nullable
        private IdProviderKey idProvider;

        @Nullable
        private String audience;

        @Nullable
        private String scope;

        @Nullable
        private String clientId;

        public Builder state( final DeviceAuthorizationState state )
        {
            this.state = state;
            return this;
        }

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

        public Builder audience( final String audience )
        {
            this.audience = audience;
            return this;
        }

        public Builder scope( final String scope )
        {
            this.scope = scope;
            return this;
        }

        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        public DeviceAuthorizationPoll build()
        {
            return new DeviceAuthorizationPoll( this );
        }
    }
}
