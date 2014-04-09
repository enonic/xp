package com.enonic.wem.api.module;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public final class ModuleFileKey
{
    private final String uri;

    private final ModuleKey module;

    private final String path;

    private ModuleFileKey( final ModuleKey module, final String path )
    {
        this.module = module;
        this.path = normalizePath( path );
        this.uri = this.module.toString() + ":" + this.path;
    }

    public String getUri()
    {
        return this.uri;
    }

    public ModuleKey getModule()
    {
        return this.module;
    }

    public String getPath()
    {
        return this.path;
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

    @Override
    public String toString()
    {
        return getUri();
    }

    private static String normalizePath( final String path )
    {
        return Files.simplifyPath( "/" + path );
    }

    public static ModuleFileKey from( final String uri )
    {
        Preconditions.checkNotNull( uri );

        final int pos = uri.indexOf( ':' );
        Preconditions.checkArgument( pos > 0, "Invalid module file key uri speficiation." );

        return from( ModuleKey.from( uri.substring( 0, pos ) ), uri.substring( pos + 1 ) );
    }

    public static ModuleFileKey from( final ModuleKey module, final String path )
    {
        Preconditions.checkNotNull( module );
        Preconditions.checkNotNull( path );

        return new ModuleFileKey( module, path );
    }
}
