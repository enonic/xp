package com.enonic.xp.descriptor;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;

public final class DescriptorKeyLocator
{
    private final ResourceService service;

    private final String pattern;

    public DescriptorKeyLocator( final ResourceService service, final String path, final boolean optional )
    {
        this.service = service;
        this.pattern = "^" + path + "/(?<name>[^/]+)/\\k<name>\\.(?:xml" + ( optional ? "|js" : "" ) + ")$";
    }

    public Set<DescriptorKey> findKeys( final ApplicationKey key )
    {
        return this.service.findFiles( key, this.pattern )
            .stream()
            .map( resource -> DescriptorKey.from( key, getNameWithoutExtension( resource.getName() ) ) )
            .collect( ImmutableSet.toImmutableSet() );
    }

    private static String getNameWithoutExtension( final String name )
    {
        final int pos = name.lastIndexOf( '.' );
        return pos > 0 ? name.substring( 0, pos ) : name;
    }
}
