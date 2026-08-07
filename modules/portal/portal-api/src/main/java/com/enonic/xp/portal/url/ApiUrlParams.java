package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.google.common.base.MoreObjects;

import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNullElse;


public final class ApiUrlParams
{
    private final String type;

    private final DescriptorKey api;

    private final String path;

    private final List<String> pathSegments;

    private final String baseUrl;

    private final String apiBaseUrl;

    private final Map<String, List<String>> queryParams;

    private ApiUrlParams( final Builder builder )
    {
        this.type = requireNonNullElse( builder.type, UrlTypeConstants.SERVER_RELATIVE );
        this.api = builder.api;
        this.path = builder.path;
        this.pathSegments = builder.pathSegments;
        this.baseUrl = builder.baseUrl;
        this.apiBaseUrl = builder.apiBaseUrl;
        this.queryParams = builder.queryParams.build();

        if ( this.path != null && this.pathSegments != null )
        {
            throw new IllegalArgumentException( "Both path and pathSegments cannot be set" );
        }
    }

    public String getType()
    {
        return type;
    }

    public DescriptorKey getApi()
    {
        return api;
    }

    public String getPath()
    {
        return path;
    }

    public List<String> getPathSegments()
    {
        return pathSegments;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getApiBaseUrl()
    {
        return apiBaseUrl;
    }

    public Map<String, List<String>> getQueryParams()
    {
        return queryParams;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String type;

        private String path;

        private List<String> pathSegments;

        private String baseUrl;

        private String apiBaseUrl;

        private DescriptorKey api;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        public Builder setType( final String type )
        {
            this.type = type;
            return this;
        }

        @Deprecated
        public Builder setApi( final String api )
        {
            this.api = DescriptorKey.from( api );
            return this;
        }

        public Builder setApi( final DescriptorKey descriptorKey )
        {
            this.api = descriptorKey;
            return this;
        }

        public Builder setPath( final String path )
        {
            this.path = path;
            return this;
        }

        public Builder setPathSegments( final List<String> pathSegments )
        {
            this.pathSegments = pathSegments;
            return this;
        }

        /**
         * Base URL of a mount where the API lives under the "_" endpoint segment:
         * {@code <baseUrl>/_/<application>:<api>/...}.
         *
         * @deprecated expose the API location as a vhost mapping instead, or use
         * {@link #setApiBaseUrl(String)} when the API root is known.
         */
        @Deprecated
        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * The root where this API is exposed, used verbatim: no "_" endpoint segment and no
         * API descriptor are appended - only the path and query parameters follow. Takes
         * precedence over {@code baseUrl}.
         */
        public Builder setApiBaseUrl( final String apiBaseUrl )
        {
            this.apiBaseUrl = apiBaseUrl;
            return this;
        }

        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        public ApiUrlParams build()
        {
            return new ApiUrlParams( this );
        }
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.type );
        helper.add( "params", this.queryParams );
        helper.add( "descriptorKey", this.api );
        helper.add( "path", this.path );
        helper.add( "pathSegments", this.pathSegments );
        helper.add( "baseUrl", this.baseUrl );
        helper.add( "apiBaseUrl", this.apiBaseUrl );
        return helper.toString();
    }
}
