package com.enonic.xp.portal.impl.url2;

import java.util.Objects;

public class UrlGenerator
{
    public String generateUrl( final UrlGeneratorParams params )
    {
        final String path = params.pathGenerator().generatePath();
        final String rewrittenUri = params.pathRewriter().rewritePath( path );
        final String baseUrl = Objects.requireNonNullElse( params.baseUrlResolver().resolveBaseUrl(), "" );

        return baseUrl + rewrittenUri;
    }
}
