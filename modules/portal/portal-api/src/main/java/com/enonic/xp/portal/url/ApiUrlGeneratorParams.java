package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


public final class ApiUrlGeneratorParams
{
    private final String baseUrl;

    private final String apiBaseUrl;

    private final String urlType;

    private final DescriptorKey descriptorKey;

    private final Supplier<String> pathSupplier;

    private final Map<String, List<String>> queryParams;

    private ApiUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.apiBaseUrl = builder.apiBaseUrl;
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.descriptorKey = requireNonNull( builder.descriptorKey );
        this.pathSupplier = builder.pathSupplier;
        this.queryParams = builder.queryParams.build();
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getApiBaseUrl()
    {
        return apiBaseUrl;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public DescriptorKey getDescriptorKey()
    {
        return descriptorKey;
    }

    public Supplier<String> getPath()
    {
        return pathSupplier;
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
        private String urlType;

        private String baseUrl;

        private String apiBaseUrl;

        private DescriptorKey descriptorKey;

        private Supplier<String> pathSupplier;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        public Builder setUrlType( final String urlType )
        {
            this.urlType = emptyToNull( urlType );
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
            this.baseUrl = emptyToNull( baseUrl );
            return this;
        }

        /**
         * The root where this API is exposed, used verbatim: no "_" endpoint segment and no
         * API descriptor are appended - only the path and query parameters follow. Takes
         * precedence over {@code baseUrl}.
         */
        public Builder setApiBaseUrl( final String apiBaseUrl )
        {
            this.apiBaseUrl = emptyToNull( apiBaseUrl );
            return this;
        }

        public Builder setDescriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        public Builder setPath( final Supplier<String> pathSupplier )
        {
            this.pathSupplier = pathSupplier;
            return this;
        }

        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        public ApiUrlGeneratorParams build()
        {
            return new ApiUrlGeneratorParams( this );
        }
    }
}
