package com.enonic.xp.descriptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;

public final class DescriptorKeyLocator
{
    private final ResourceService service;

    private final String pattern;

    public DescriptorKeyLocator( final ResourceService service, final String path, final boolean optional )
    {
        this.service = service;
        this.pattern = "^" + path + "/(?<name>[^/]+)/\\k<name>\\.(?:xml|yml" + ( optional ? "|js" : "" ) + ")$";
    }

    public DescriptorKeys findKeys( final ApplicationKey key )
    {
        return this.service.findFiles( key, this.pattern )
            .stream()
            .map( resource -> DescriptorKey.from( key, getNameWithoutExtension( resource.getName() ) ) )
            .collect( DescriptorKeys.collector() );
    }

    private static String getNameWithoutExtension( final String name )
    {
        final int pos = name.lastIndexOf( '.' );
        return pos > 0 ? name.substring( 0, pos ) : name;
    }
}
