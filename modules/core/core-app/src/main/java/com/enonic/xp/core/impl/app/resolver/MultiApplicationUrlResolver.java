package com.enonic.xp.core.impl.app.resolver;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.resource.Resource;

public final class MultiApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationUrlResolver[] list;

    public MultiApplicationUrlResolver( final ApplicationUrlResolver... list )
    {
        this.list = list;
    }

    @Override
    public Set<String> findFiles()
    {
        return Arrays.stream( this.list ).flatMap( r -> r.findFiles().stream() ).collect( Collectors.toCollection( LinkedHashSet::new ) );
    }

    @Override
    public Resource findResource( final String path )
    {
        return Arrays.stream( this.list )
            .map( resolver -> resolver.findResource( path ) )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
    }
}
