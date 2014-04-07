package com.enonic.wem.core.module.source;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.config.SystemConfig;

/**
 * Resolve order:
 * - local module
 * - system module
 * <p/>
 * Resolve ./test.js
 * - resolve from module base
 * - else not found
 * <p/>
 * Resolve test.js
 * - resolve from module base (and osgi bundle classpath soon)
 * - else resolve from system
 * - else not found
 * <p/>
 */
public final class SourceResolverImpl
    implements SourceResolver
{
    private final static String BASE_PATH = "/module";

    private final SystemConfig config;

    @Inject
    public SourceResolverImpl( final SystemConfig config )
    {
        this.config = config;
    }

    @Override
    public ModuleSource resolve( final ModuleResourceKey key )
    {
        final ModuleKey moduleKey = key.getModuleKey();
        final String path = key.getPath().toRelativePath().toString();
        return resolve( moduleKey, path );
    }

    private ModuleSource resolve( final ModuleKey moduleKey, final String resourcePath )
    {
        final String path = Files.simplifyPath( resourcePath );
        final ModuleResourceKey key = new ModuleResourceKey( moduleKey, ResourcePath.from( path ) );
        final URL url = resolveUrl( moduleKey, path );
        return new ModuleSourceImpl( key, url );
    }

    @Override
    public ModuleSource resolve( final ModuleResourceKey base, final String uri )
    {
        if ( uri.startsWith( "./" ) || uri.startsWith( "../" ) )
        {
            final ResourcePath parent = base.getPath().toRelativePath().parent();
            final String path = parent != null ? ( parent.toString() + "/" + uri ) : uri;
            return resolve( base.getModuleKey(), path );
        }

        return resolve( ModuleKey.SYSTEM, uri );
    }

    private URL resolveUrl( final ModuleKey moduleKey, final String path )
    {
        if ( moduleKey.isSystem() )
        {
            return resolveUrlFromSystem( path );
        }

        final Path basePath = this.config.getModulesDir().resolve( moduleKey.toString() ).resolve( path );
        if ( !basePath.toFile().exists() )
        {
            return null;
        }

        try
        {
            return basePath.toUri().toURL();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private URL resolveUrlFromSystem( final String path )
    {
        return getClass().getResource( BASE_PATH + "/" + path );
    }
}
