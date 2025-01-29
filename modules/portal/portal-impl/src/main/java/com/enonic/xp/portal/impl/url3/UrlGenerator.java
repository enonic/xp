package com.enonic.xp.portal.impl.url3;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class UrlGenerator
{
    public static final UrlGenerator INSTANCE = new UrlGenerator();

    public String generateUrl( final BaseUrlStrategy baseUrlStrategy, final PathStrategy pathStrategy )
    {
        final String baseUrl = baseUrlStrategy.generateBaseUrl();
        final String path = pathStrategy.generatePath();

        final StringBuilder url = new StringBuilder();

        appendPart( url, baseUrl );
        appendPart( url, path );

        return url.toString();
    }

}
