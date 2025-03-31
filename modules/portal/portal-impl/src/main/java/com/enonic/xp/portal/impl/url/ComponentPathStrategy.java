package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

final class ComponentPathStrategy
    implements PathStrategy
{
    private final Supplier<String> componentPath;

    ComponentPathStrategy( final Supplier<String> componentPath )
    {
        this.componentPath = Objects.requireNonNull( componentPath );
    }

    @Override
    public String generatePath()
    {
        final String path = componentPath.get();
        if ( path == null )
        {
            return null;
        }

        final StringBuilder result = new StringBuilder();

        UrlBuilderHelper.appendSubPath( result, "component" );
        UrlBuilderHelper.appendAndEncodePathParts( result, path );

        return result.toString();
    }
}
