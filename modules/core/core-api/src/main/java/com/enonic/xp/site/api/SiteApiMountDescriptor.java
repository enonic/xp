package com.enonic.xp.site.api;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SiteApiMountDescriptor
{
    private static final String DEFAULT_API_KEY = "api";

    private final String apiKey;

    private final ApiContextPath contextPath;

    private SiteApiMountDescriptor( final Builder builder )
    {
        this.apiKey = Objects.requireNonNullElse( builder.apiKey, DEFAULT_API_KEY );
        this.contextPath = Objects.requireNonNullElse( builder.contextPath, ApiContextPath.DEFAULT );
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public ApiContextPath getContextPath()
    {
        return contextPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private String apiKey;

        private ApiContextPath contextPath;

        private Builder()
        {
        }

        public Builder apiKey( final String apiKey )
        {
            this.apiKey = apiKey;
            return this;
        }

        public Builder contextPath( final ApiContextPath contextPath )
        {
            this.contextPath = contextPath;
            return this;
        }

        public SiteApiMountDescriptor build()
        {
            if ( apiKey != null && apiKey.contains( ":" ) )
            {
                throw new IllegalArgumentException( "Site API key cannot reference to external APIs." );
            }
            return new SiteApiMountDescriptor( this );
        }
    }
}
