package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;

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
    public ScriptSource loadFromSystem( final String name )
    {
        final URL url = this.classLoader.getResource( name );
        if ( url == null )
        {
            return null;
        }

        return new UrlScriptSource( name, url );
    }

    @Override
    public ScriptSource loadFromModule( final ModuleResourceKey key )
    {
        final String name = key.toString();
        final Path path = this.pathResolver.resolveResourcePath( key );
        final File file = path.toFile();

        if ( !file.isFile() )
        {
            return null;
        }

        return new FileScriptSource( name, file );
    }
}
