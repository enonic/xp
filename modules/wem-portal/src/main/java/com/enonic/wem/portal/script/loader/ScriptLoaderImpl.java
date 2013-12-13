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
    private final static String BASE_CLASSPATH = "js/system/";

    private final ClassLoader classLoader;

    private final ModuleResourcePathResolver pathResolver;

    @Inject
    public ScriptLoaderImpl( final ModuleResourcePathResolver pathResolver )
    {
        this.classLoader = getClass().getClassLoader();
        this.pathResolver = pathResolver;
    }

    @Override
    public ScriptSource load( final String name )
    {
        try
        {
            return loadFromModule( ModuleResourceKey.from( name ) );
        }
        catch ( final Exception e )
        {
            return loadFromSystem( name );
        }
    }

    @Override
    public ScriptSource loadFromSystem( final String name )
    {
        final URL url = this.classLoader.getResource( BASE_CLASSPATH + name );
        if ( url == null )
        {
            return null;
        }

        return new UrlScriptSource( name, url );
    }

    @Override
    public ScriptSource loadFromModule( final ModuleResourceKey key )
    {
        final Path path = this.pathResolver.resolveResourcePath( key );
        final File file = path.toFile();

        if ( !file.isFile() )
        {
            return null;
        }

        return new ModuleScriptSource( key, file );
    }
}
