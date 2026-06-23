package com.enonic.xp.security.token;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

/**
 * The result of polling a device authorization request. When the state is
 * {@link DeviceAuthorizationState#APPROVED} the approved subject and the original request
 * details (used to mint the access token) are populated.
 */
public final class DeviceAuthorizationPoll
{
    private final DeviceAuthorizationState state;

    private final PrincipalKey subject;

    private final IdProviderKey idProvider;

    private final String audience;

    private final String scope;

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

    public PrincipalKey getSubject()
    {
        return subject;
    }

    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    public String getAudience()
    {
        return audience;
    }

    public String getScope()
    {
        return scope;
    }

    public String getClientId()
    {
        return clientId;
    }

    public static final class Builder
    {
        private DeviceAuthorizationState state;

        private PrincipalKey subject;

        private IdProviderKey idProvider;

        private String audience;

        private String scope;

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
