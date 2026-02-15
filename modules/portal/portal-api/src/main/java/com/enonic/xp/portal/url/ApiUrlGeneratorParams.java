package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.DescriptorKey;

import static com.google.common.base.Strings.emptyToNull;

@PublicApi
public final class ApiUrlGeneratorParams
{
    private final String baseUrl;

    private final String urlType;

    private final DescriptorKey descriptorKey;

    private final Supplier<String> pathSupplier;

    private final Map<String, List<String>> queryParams;

    private ApiUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.descriptorKey = Objects.requireNonNull( builder.descriptorKey );
        this.pathSupplier = builder.pathSupplier;
        this.queryParams = builder.queryParams.build();
    }

    public String getBaseUrl()
    {
        return baseUrl;
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

        private DescriptorKey descriptorKey;

        private Supplier<String> pathSupplier;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        public Builder setUrlType( final String urlType )
        {
            this.urlType = emptyToNull( urlType );
            return this;
        }

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = emptyToNull( baseUrl );
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
