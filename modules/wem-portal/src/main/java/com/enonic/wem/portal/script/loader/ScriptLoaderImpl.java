package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleKeyResolver;
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

    @Override
    public ScriptSource load( final String name )
    {
        return load( null, null, name );
    }

    @Override
    public ScriptSource load( final ModuleKeyResolver resolver, final ModuleKey defaultModule, final String name )
    {
        if ( name.contains( ":" ) )
        {
            return loadFromModule( resolver, defaultModule, name );
        }
        else
        {
            return loadFromSystem( name );
        }
    }

    private ScriptSource loadFromModule( final ModuleKeyResolver resolver, final ModuleKey defaultModule, final String name )
    {
        final ModuleResourceKeyResolver resourceKeyResolver = new ModuleResourceKeyResolver( resolver, defaultModule );
        final ModuleResourceKey key = resourceKeyResolver.resolve( name );
        if ( key == null )
        {
            return null;
        }

        return loadFromModule( key );
    }
}
