package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.function.Supplier;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendAndEncodePathParts;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;

final class ApiUrlPathResolver
    implements Supplier<String>
{
    private final String path;

    private final List<String> pathSegments;

    ApiUrlPathResolver( final String path, final List<String> pathSegments )
    {
        this.path = path;
        this.pathSegments = pathSegments;
    }

    @Override
    public String get()
    {
        final StringBuilder result = new StringBuilder();

        appendAndEncodePathParts( result, path );
        appendPathSegments( result, pathSegments );

        return result.toString();
    }
}
