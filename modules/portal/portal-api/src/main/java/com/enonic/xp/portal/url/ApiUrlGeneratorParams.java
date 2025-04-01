package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Supplier;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.emptyToNull;

@PublicApi
public final class ApiUrlGeneratorParams
{
    private final String urlType;

    private final BaseUrlStrategy baseUrlStrategy;

    private final String baseUrl;

    private final String application;

    private final String api;

    private final Supplier<String> pathSupplier;

    private final Multimap<String, String> queryParams;

    private ApiUrlGeneratorParams( final Builder builder )
    {
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.baseUrl = builder.baseUrl;
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.application = Objects.requireNonNull( builder.application );
        this.api = Objects.requireNonNull( builder.api );
        this.pathSupplier = builder.pathSupplier;
        this.queryParams = builder.queryParams;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public String getApplication()
    {
        return application;
    }

    public String getApi()
    {
        return api;
    }

    public Supplier<String> getPath()
    {
        return pathSupplier;
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
        private String urlType;

        private BaseUrlStrategy baseUrlStrategy;

        private String baseUrl;

        private String application;

        private String api;

        private Supplier<String> pathSupplier;

        private final Multimap<String, String> queryParams = LinkedListMultimap.create();

        public Builder setUrlType( final String urlType )
        {
            this.urlType = emptyToNull( urlType );
            return this;
        }

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
            return this;
        }

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = emptyToNull( baseUrl );
            return this;
        }

        public Builder setApplication( final String application )
        {
            this.application = emptyToNull( application );
            return this;
        }

        public Builder setApi( final String api )
        {
            this.api = emptyToNull( api );
            return this;
        }

        public Builder setPath( final Supplier<String> pathSupplier )
        {
            this.pathSupplier = pathSupplier;
            return this;
        }

        public Builder addQueryParam( final String key, final String value )
        {
            this.queryParams.put( key, value );
            return this;
        }

        public Builder addQueryParams( final Map<String, Collection<String>> queryParams )
        {
            queryParams.forEach( this.queryParams::putAll );
            return this;
        }

        public ApiUrlGeneratorParams build()
        {
            return new ApiUrlGeneratorParams( this );
        }
    }
}
