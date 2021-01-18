package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolverBase;

public final class PrefixApplicationUrlResolver
    extends ApplicationUrlResolverBase
{
    private final ApplicationUrlResolver resolver;

    private final String prefix;

    public PrefixApplicationUrlResolver( final ApplicationUrlResolver resolver, final String prefix )
    {
        this.resolver = resolver;
        this.prefix = normalizePath( prefix ) + "/";
    }

    @Override
    public Set<String> findFiles()
    {
        return this.resolver.findFiles().stream().
            filter( name -> name.startsWith( this.prefix ) ).
            map( name -> name.substring( this.prefix.length() ) ).
            collect( Collectors.toSet() );
    }

    @Override
    public URL findUrl( final String path )
    {
        final String normalized = this.prefix + normalizePath( path );
        return this.resolver.findUrl( normalized );
    }
}
