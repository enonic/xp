package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.base.Optional;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    private final ClassLoader classLoader;

    private final ModuleResourcePathResolver pathResolver;

    @Inject
    public ScriptLoaderImpl( final ModuleResourcePathResolver pathResolver )
    {
        this.classLoader = getClass().getClassLoader();
        this.pathResolver = pathResolver;
    }

    @Override
    public Optional<ScriptSource> loadFromSystem( final String name )
    {
        final URL url = this.classLoader.getResource( name );
        if ( url == null )
        {
            return Optional.absent();
        }

        final ScriptSource source = new UrlScriptSource( name, url );
        return Optional.of( source );
    }

    @Override
    public Optional<ScriptSource> loadFromModule( final ModuleResourceKey key )
    {
        final String name = key.toString();
        final Path path = this.pathResolver.resolveResourcePath( key );
        final File file = path.toFile();

        if ( !file.isFile() )
        {
            return Optional.absent();
        }

        final ScriptSource source = new FileScriptSource( name, file );
        return Optional.of( source );
    }
}
