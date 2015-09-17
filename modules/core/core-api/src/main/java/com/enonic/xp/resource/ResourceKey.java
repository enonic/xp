package com.enonic.xp.resource;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import com.enonic.xp.app.ApplicationKey;

@Beta
public final class ResourceKey
{
    private final String uri;

    private final ApplicationKey applicationKey;

    private final String path;

    private ResourceKey( final ApplicationKey applicationKey, final String path )
    {
        this.applicationKey = applicationKey;
        this.path = normalizePath( path );
        this.uri = this.applicationKey.toString() + ":" + this.path;
    }

    public String getUri()
    {
        return this.uri;
    }

    public ApplicationKey getApplicationKey()
    {
        return this.applicationKey;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getName()
    {
        if ( isRoot() )
        {
            return "";
        }

        final int pos = this.path.lastIndexOf( '/' );
        return this.path.substring( pos + 1 );
    }

    public String getExtension()
    {
        final int pos = this.path.lastIndexOf( '.' );
        if ( pos > 0 )
        {
            return this.path.substring( pos + 1 );
        }

        return null;
    }

    public boolean isRoot()
    {
        return this.path.equals( "/" );
    }

    public ResourceKey resolve( final String relPath )
    {
        // absolute path
        if ( relPath.startsWith( "/" ) )
        {
            return new ResourceKey( this.applicationKey, relPath );
        }
        return new ResourceKey( this.applicationKey, this.path + "/" + relPath );
    }

    @Override
    public String toString()
    {
        return getUri();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ResourceKey ) && ( this.uri.equals( ( (ResourceKey) o ).uri ) );
    }

    @Override
    public int hashCode()
    {
        return this.uri.hashCode();
    }

    private static String normalizePath( final String path )
    {
        return Files.simplifyPath( "/" + path );
    }

    public static ResourceKey from( final String uri )
    {
        Preconditions.checkNotNull( uri );

        final int pos = uri.indexOf( ':' );
        Preconditions.checkArgument( pos > 0, "Invalid applicationKey file key uri specification." );

        return from( ApplicationKey.from( uri.substring( 0, pos ) ), uri.substring( pos + 1 ) );
    }

    public static ResourceKey from( final ApplicationKey application, final String path )
    {
        Preconditions.checkNotNull( application );
        Preconditions.checkNotNull( path );

        return new ResourceKey( application, path );
    }
}
