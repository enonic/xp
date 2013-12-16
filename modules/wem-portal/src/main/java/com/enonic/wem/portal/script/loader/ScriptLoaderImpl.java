package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.google.common.base.Throwables;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    private final static String BASE_CLASSPATH = "WEB-INF/js/lib/";

    private final ServletContext context;

    private final ModuleResourcePathResolver pathResolver;

    @Inject
    public ScriptLoaderImpl( final ServletContext context, final ModuleResourcePathResolver pathResolver )
    {
        this.context = context;
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
        try
        {
            final URL url = this.context.getResource( BASE_CLASSPATH + name );
            if ( url == null )
            {
                return null;
            }

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
