package com.enonic.xp.core.impl.app.resolver;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.resource.Resource;

public final class RealOverVirtualApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationUrlResolver virtualAppResolver;

    private final ApplicationUrlResolver realAppResolver;

    public RealOverVirtualApplicationUrlResolver( final ApplicationUrlResolver realAppResolver,
                                                  final ApplicationUrlResolver virtualAppResolver )
    {
        this.virtualAppResolver = virtualAppResolver;
        this.realAppResolver = realAppResolver;
    }

    @Override
    public Set<String> findFiles()
    {
        final Set<String> set = new HashSet<>();

        if ( realAppResolver != null )
        {
            set.addAll( realAppResolver.findFiles() );
        }
        else if ( virtualAppResolver != null )
        {
            set.addAll( virtualAppResolver.findFiles() );

        }

        return set;
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( realAppResolver != null )
        {
            return realAppResolver.findResource( path );
        }

        if ( virtualAppResolver != null )
        {
            return virtualAppResolver.findResource( path );
        }

        return null;
    }
}
