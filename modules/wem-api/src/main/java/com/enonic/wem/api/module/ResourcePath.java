package com.enonic.wem.api.module;


import com.google.common.base.Splitter;

import com.enonic.wem.api.BasePath;

public final class ResourcePath
    extends BasePath<ResourcePath>
{
    private static final char SEPARATOR = '/';

    private static final ResourcePath ROOT = ResourcePath.from( "/" );

    private ResourcePath( final Iterable<String> elements, final boolean isAbsolute )
    {
        super( SEPARATOR, elements, isAbsolute );
    }

    @Override
    protected ResourcePath newPath( final Iterable<String> elements, final boolean isAbsolute )
    {
        return new ResourcePath( elements, isAbsolute );
    }

    public static ResourcePath root()
    {
        return ROOT;
    }

    public static ResourcePath from( final String path )
    {
        final Iterable<String> pathElements = Splitter.on( SEPARATOR ).omitEmptyStrings().split( path );
        final boolean isAbsolute = path.startsWith( String.valueOf( SEPARATOR ) );
        return new ResourcePath( pathElements, isAbsolute );
    }

}
