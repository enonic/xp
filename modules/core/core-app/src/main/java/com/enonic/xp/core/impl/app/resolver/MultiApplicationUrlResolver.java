package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class MultiApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationUrlResolver[] list;

    public MultiApplicationUrlResolver( final ApplicationUrlResolver... list )
    {
        this.list = list;
    }

    @Override
    public long filesHash( final String path )
    {
        return Arrays.stream( list ).
            mapToLong( resolver -> resolver.filesHash( path ) ).
            reduce( 1, ( a, b ) -> 31 * a + b );
    }

    @Override
    public Set<String> findFiles()
    {
        final Set<String> set = new HashSet<>();
        for ( final ApplicationUrlResolver resolver : this.list )
        {
            set.addAll( resolver.findFiles() );
        }

        return set;
    }

    @Override
    public URL findUrl( final String path )
    {
        for ( final ApplicationUrlResolver resolver : this.list )
        {
            final URL url = resolver.findUrl( path );
            if ( url != null )
            {
                return url;
            }
        }

        return null;
    }
}
