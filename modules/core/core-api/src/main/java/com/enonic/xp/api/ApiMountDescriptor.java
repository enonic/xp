package com.enonic.xp.api;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class ApiMountDescriptor
{
    private final ApplicationKey applicationKey;

    private final String apiKey;

    private ApiMountDescriptor( final Builder builder )
    {
        this.applicationKey = builder.applicationKey;
        this.apiKey = builder.apiKey;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey applicationKey;

        private String apiKey;

        private Builder()
        {

        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder apiKey( final String apiKey )
        {
            this.apiKey = apiKey;
            return this;
        }

        public ApiMountDescriptor build()
        {
            Preconditions.checkArgument( applicationKey != null, "applicationKey must be set." );
            Preconditions.checkArgument( apiKey != null && !apiKey.isBlank(), "apiKey must be set." );
            return new ApiMountDescriptor( this );
        }
    }
}
