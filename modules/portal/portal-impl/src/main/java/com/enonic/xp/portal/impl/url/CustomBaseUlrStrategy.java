package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;

final class CustomBaseUlrStrategy
    implements BaseUrlStrategy
{
    private final String baseUrl;

    private final String urlType;

    CustomBaseUlrStrategy( final String baseUrl, final String urlType )
    {
        this.baseUrl = Objects.requireNonNull( baseUrl );
        this.urlType = urlType;
    }

    @Override
    public String generateBaseUrl()
    {
        final String path = UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) ? URI.create( baseUrl ).getPath() : baseUrl;
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 ) + "/_/";
        }
        return path + "/_/";
    }
}
