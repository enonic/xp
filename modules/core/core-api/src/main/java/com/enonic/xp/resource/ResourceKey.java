package com.enonic.xp.resource;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class ResourceKey
{
    private final ApplicationKey applicationKey;

    private final String path;

    private ResourceKey( final ApplicationKey applicationKey, final String path )
    {
        this.applicationKey = applicationKey;
        this.path = path;
    }

    public String getUri()
    {
        return this.applicationKey + ":" + this.path;
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
        return pos > 0 ? this.path.substring( pos + 1 ) : null;
    }

    public boolean isRoot()
    {
        return this.path.equals( "/" );
    }

    public ResourceKey resolve( final String relPath )
    {
        final String path;
        if ( relPath.startsWith( "/" ) )
        {
            path = relPath;
        }
        else
        {
            path = this.path + "/" + relPath;
        }

        return new ResourceKey( this.applicationKey, normalizePath( path ) );
    }

    @Override
    public String toString()
    {
        return getUri();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ResourceKey that = (ResourceKey) o;
        return applicationKey.equals( that.applicationKey ) && path.equals( that.path );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, path );
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

        return new ResourceKey( application, normalizePath( path ) );
    }

    public static ResourceKey assets( final ApplicationKey application )
    {
        Preconditions.checkNotNull( application );

        return new ResourceKey( application, "/assets/" );
    }
}
