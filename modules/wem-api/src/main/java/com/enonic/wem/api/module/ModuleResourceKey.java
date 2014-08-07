package com.enonic.wem.api.module;

import java.net.URL;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public final class ModuleResourceKey
{
    private final static String URL_PROTOCOL_PREFIX = "module";

    private final String uri;

    private final ModuleKey module;

    private final String path;

    private ModuleResourceKey( final ModuleKey module, final String path )
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

    public ModuleResourceKey resolve( final String relPath )
    {
        return new ModuleResourceKey( this.module, this.path + "/" + relPath );
    }

    @Override
    public String toString()
    {
        return getUri();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ModuleResourceKey ) && ( this.uri.equals( ( (ModuleResourceKey) o ).uri ) );
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

    public static ModuleResourceKey from( final String uri )
    {
        Preconditions.checkNotNull( uri );

        final int pos = uri.indexOf( ':' );
        Preconditions.checkArgument( pos > 0, "Invalid module file key uri specification." );

        return from( ModuleKey.from( uri.substring( 0, pos ) ), uri.substring( pos + 1 ) );
    }

    public static ModuleResourceKey from( final ModuleKey module, final String path )
    {
        Preconditions.checkNotNull( module );
        Preconditions.checkNotNull( path );

        return new ModuleResourceKey( module, path );
    }

    public static ModuleResourceKey from( final URL url )
    {
        Preconditions.checkArgument( URL_PROTOCOL_PREFIX.equals( url.getProtocol() ), "Invalid module resource key URL." );
        return ModuleResourceKey.from( url.getPath() );
    }
}
