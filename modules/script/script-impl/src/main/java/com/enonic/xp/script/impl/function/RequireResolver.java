package com.enonic.xp.script.impl.function;

import java.util.stream.Stream;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public final class RequireResolver
{
    private final ResourceService resourceService;

    private final ResourceKey baseKey;

    public RequireResolver( final ResourceService resourceService, final ResourceKey baseKey )
    {
        this.baseKey = baseKey;
        this.resourceService = resourceService;
    }

    public ResourceKey resolve( final String path )
    {
        return doResolve( this.baseKey.resolve( path.startsWith( "/" ) ? path : "../" + path ) );
    }

    private ResourceKey doResolve( final ResourceKey key )
    {
        final ApplicationKey applicationKey = key.getApplicationKey();
        return findSearchPaths( key.getPath() ).map( path -> ResourceKey.from( applicationKey, path ) )
            .filter( this::exists )
            .findFirst()
            .orElse( key );
    }

    static Stream<String> findSearchPaths( final String path )
    {
        if ( path.endsWith( ".js" ) || path.endsWith( ".json" ) )
        {
            return Stream.of( path );
        }

        if ( path.endsWith( "/" ) )
        {
            return Stream.of( "index.js", "index.json" ).map( s -> path + s );
        }

        return Stream.of( ".js", "/index.js", ".json", "/index.json" ).map( s -> path + s );
    }

    private boolean exists( final ResourceKey key )
    {
        return this.resourceService.getResource( key ).exists();
    }
}
