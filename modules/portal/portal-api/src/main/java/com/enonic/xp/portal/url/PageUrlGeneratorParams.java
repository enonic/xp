package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PageUrlGeneratorParams
{
    private final BaseUrlStrategy baseUrlStrategy;

    private final Supplier<String> contentPathSupplier;

    private final Multimap<String, String> queryParams;

    private PageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrlStrategy = Objects.requireNonNull( builder.baseUrlStrategy );
        this.contentPathSupplier = Objects.requireNonNull( builder.contentPathSupplier );
        this.queryParams = builder.queryParams;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public Supplier<String> getContentPathSupplier()
    {
        return contentPathSupplier;
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

        private Supplier<String> contentPathSupplier;

        private final Multimap<String, String> queryParams = HashMultimap.create();

        public Builder setBaseUrlStrategy( final BaseUrlStrategy baseUrlStrategy )
        {
            this.baseUrlStrategy = baseUrlStrategy;
            return this;
        }

        public Builder setContentPathSupplier( final Supplier<String> contentPathSupplier )
        {
            this.contentPathSupplier = contentPathSupplier;
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

        public PageUrlGeneratorParams build()
        {
            return new PageUrlGeneratorParams( this );
        }
    }
}
