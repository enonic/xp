package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

final class UrlGeneratorParams
{
    private final Supplier<String> baseUrlSupplier;

    private final Supplier<String> pathSupplier;

    private final Supplier<String> queryStringSupplier;

    private UrlGeneratorParams( final Builder builder )
    {
        this.baseUrlSupplier = builder.baseUrlSupplier;
        this.pathSupplier = builder.pathSupplier;
        this.queryStringSupplier = builder.queryStringSupplier;
    }

    public Supplier<String> getBaseUrl()
    {
        return baseUrlSupplier;
    }

    public Supplier<String> getPath()
    {
        return pathSupplier;
    }

    public Supplier<String> getQueryString()
    {
        return queryStringSupplier;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private Supplier<String> baseUrlSupplier;

        private Supplier<String> pathSupplier;

        private Supplier<String> queryStringSupplier;

        public Builder setBaseUrl( final Supplier<String> baseUrlSupplier )
        {
            this.baseUrlSupplier = baseUrlSupplier;
            return this;
        }

        public Builder setPath( final Supplier<String> pathSupplier )
        {
            this.pathSupplier = pathSupplier;
            return this;
        }

        public Builder setQueryString( final Supplier<String> queryStringSupplier )
        {
            this.queryStringSupplier = queryStringSupplier;
            return this;
        }

        public UrlGeneratorParams build()
        {
            return new UrlGeneratorParams( this );
        }
    }
}
