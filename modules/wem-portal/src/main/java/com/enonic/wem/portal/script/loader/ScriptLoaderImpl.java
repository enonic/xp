package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.base.Throwables;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    private final static String BASE_CLASSPATH = "js/lib";

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

        try
        {
            return new ScriptSourceImpl( name, url.toURI(), null );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
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

        return new ScriptSourceImpl( key.toString(), file.toURI(), key.getModuleKey() );
    }
}
