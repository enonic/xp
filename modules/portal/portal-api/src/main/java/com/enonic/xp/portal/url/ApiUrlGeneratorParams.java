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
    private final BaseUrlStrategy baseUrlStrategy;

    private final String application;

    private final String api;

    private final Supplier<String> pathSupplier;

    private final Multimap<String, String> queryParams;

    private ApiUrlGeneratorParams( final Builder builder )
    {
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.application = Objects.requireNonNull( builder.application );
        this.api = Objects.requireNonNull( builder.api );
        this.pathSupplier = Objects.requireNonNull( builder.pathSupplier );
        this.queryParams = builder.queryParams;
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
        private BaseUrlStrategy baseUrlStrategy;

        private String application;

        private String api;

        private Supplier<String> pathSupplier;

        private final Multimap<String, String> queryParams = LinkedListMultimap.create();

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
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
