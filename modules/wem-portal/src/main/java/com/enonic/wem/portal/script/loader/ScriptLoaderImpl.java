package com.enonic.wem.portal.script.loader;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    private final ModuleResourcePathResolver pathResolver;

    @Inject
    public ScriptLoaderImpl( final ModuleResourcePathResolver pathResolver )
    {
        this.pathResolver = pathResolver;
    }

    @Override
    public ScriptSource load( final String name )
    {
        return load( ResourceKey.from( name ) );
    }

    @Override
    public ScriptSource load( final ResourceKey key )
    {
        Path path = this.pathResolver.resolveModulePath( key.getModule() );
        path = path.resolve( key.getPath() );

        if ( !Files.isRegularFile( path ) )
        {
            return null;
        }

        return new ScriptSourceImpl( key, path );
    }
}
