package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

final class ComponentPathSupplier
    implements Supplier<String>
{
    private final Supplier<String> componentPath;

    ComponentPathSupplier( final Supplier<String> componentPath )
    {
        this.componentPath = requireNonNull( componentPath );
    }

    @Override
    public String get()
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
