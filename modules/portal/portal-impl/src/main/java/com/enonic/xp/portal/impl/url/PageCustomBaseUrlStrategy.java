package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;

final class PageCustomBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final String baseUrl;

    private final String urlType;

    private PageCustomBaseUrlStrategy( final Builder builder )
    {
        this.baseUrl = Objects.requireNonNull( builder.baseUrl );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        final String path = UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) ? URI.create( baseUrl ).getPath() : baseUrl;
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }
        return path;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String baseUrl;

        private String urlType;

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public PageCustomBaseUrlStrategy build()
        {
            return new PageCustomBaseUrlStrategy( this );
        }
    }
}
