package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.PathStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class UrlGenerator
{
    public static String generateUrl( final BaseUrlStrategy baseUrlStrategy, final PathStrategy pathStrategy,
                                      final RewritePathStrategy rewritePathStrategy )
    {
        final String baseUrl = baseUrlStrategy.generateBaseUrl();
        final String path = rewritePathStrategy.rewritePath( pathStrategy.generatePath() );

        final StringBuilder url = new StringBuilder();
        appendPart( url, path );

        return baseUrl + url;
    }

}
