package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import com.enonic.xp.descriptor.DescriptorKey;


public final class ApiUrlParams
{
    private final String type;

    private final DescriptorKey api;

    private final String path;

    private final List<String> pathSegments;

    private final String baseUrl;

    private final Map<String, List<String>> queryParams;

    private ApiUrlParams( final Builder builder )
    {
        this.type = Objects.requireNonNullElse( builder.type, UrlTypeConstants.SERVER_RELATIVE );
        this.api = builder.api;
        this.path = builder.path;
        this.pathSegments = builder.pathSegments;
        this.baseUrl = builder.baseUrl;
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

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
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
        return helper.toString();
    }
}
