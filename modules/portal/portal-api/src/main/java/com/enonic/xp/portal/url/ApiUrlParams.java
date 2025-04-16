package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ApiUrlParams
{
    private final String type;

    private final String application;

    private final String api;

    private final String path;

    private final List<String> pathSegments;

    private final String baseUrl;

    private final Multimap<String, String> queryParams;

    private ApiUrlParams( final Builder builder )
    {
        this.type = Objects.requireNonNullElse( builder.type, UrlTypeConstants.SERVER_RELATIVE );
        this.api = Objects.requireNonNull( builder.api );
        this.application = builder.application;
        this.path = builder.path;
        this.pathSegments = builder.pathSegments;
        this.baseUrl = builder.baseUrl;
        this.queryParams = builder.queryParams;

        if ( this.path != null && this.pathSegments != null )
        {
            throw new IllegalArgumentException( "Both path and pathSegments cannot be set" );
        }
    }

    public String getType()
    {
        return type;
    }

    public String getApplication()
    {
        return application;
    }

    public String getApi()
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

    public Map<String, Collection<String>> getQueryParams()
    {
        return queryParams.asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String type;

        private String application;

        private String api;

        private String path;

        private List<String> pathSegments;

        private String baseUrl;

        private final Multimap<String, String> queryParams = LinkedListMultimap.create();

        public Builder setType( final String type )
        {
            this.type = type;
            return this;
        }

        public Builder setApplication( final String application )
        {
            this.application = application;
            return this;
        }

        public Builder setApi( final String api )
        {
            this.api = api;
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

        public Builder addQueryParams( final Map<String, Collection<String>> queryParams )
        {
            queryParams.forEach( this.queryParams::putAll );
            return this;
        }

        public Builder addQueryParam( final String key, final String value )
        {
            this.queryParams.put( key, value );
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
        helper.add( "api", this.api );
        helper.add( "application", this.application );
        helper.add( "path", this.path );
        helper.add( "pathSegments", this.pathSegments );
        helper.add( "baseUrl", this.baseUrl );
        return helper.toString();
    }
}
