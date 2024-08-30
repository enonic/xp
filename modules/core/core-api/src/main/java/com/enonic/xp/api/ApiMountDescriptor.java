package com.enonic.xp.api;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class ApiMountDescriptor
{
    private static final String DEFAULT_API_KEY = "";

    private final ApplicationKey applicationKey;

    private final String apiKey;

    private ApiMountDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.apiKey = Objects.requireNonNullElse( builder.apiKey, DEFAULT_API_KEY );
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
            return new ApiMountDescriptor( this );
        }
    }
}
