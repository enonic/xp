package com.enonic.xp.core.impl.app.resolver;

public abstract class ApplicationUrlResolverBase
    implements ApplicationUrlResolver
{
    @Override
    public long filesHash( final String path )
    {
        return System.currentTimeMillis();
    }

    protected final String normalizePath( final String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return normalizePath( path.substring( 1 ) );
        }

        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }

        return path;
    }
}
