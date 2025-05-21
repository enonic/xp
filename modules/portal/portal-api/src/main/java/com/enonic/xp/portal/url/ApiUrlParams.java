package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public final class ApiUrlParams
{
    private final String type;

    private final DescriptorKey descriptorKey;

    private final String path;

    private final List<String> pathSegments;

    private final String baseUrl;

    private final Multimap<String, String> queryParams;

    private ApiUrlParams( final Builder builder )
    {
        this.type = Objects.requireNonNullElse( builder.type, UrlTypeConstants.SERVER_RELATIVE );
        this.descriptorKey = Objects.requireNonNull( builder.descriptorKey );
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

    public DescriptorKey getDescriptorKey()
    {
        return descriptorKey;
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

        private DescriptorKey descriptorKey;

        private String path;

        private List<String> pathSegments;

        private String baseUrl;

        private final Multimap<String, String> queryParams = LinkedListMultimap.create();

        public Builder setType( final String type )
        {
            this.type = type;
            return this;
        }

        public Builder setDescriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
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
        helper.add( "descriptorKey", this.descriptorKey );
        helper.add( "path", this.path );
        helper.add( "pathSegments", this.pathSegments );
        helper.add( "baseUrl", this.baseUrl );
        return helper.toString();
    }
}
