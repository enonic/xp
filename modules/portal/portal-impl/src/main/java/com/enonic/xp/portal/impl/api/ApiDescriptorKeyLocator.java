package com.enonic.xp.portal.impl.api;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceService;

public class ApiDescriptorKeyLocator
{
    private final ResourceService service;

    private final String pattern;

    public ApiDescriptorKeyLocator( final ResourceService service )
    {
        this.service = service;
        this.pattern = "^/apis/(api|(?<name>[^/]+)/\\k<name>)\\.(xml|js)$";
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
