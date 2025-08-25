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
            .map( resource -> {
                String nameWithoutExtension = getNameWithoutExtension( resource.getName() );
                String extension = resource.getName().length() - nameWithoutExtension.length() > 1 ? resource.getName()
                    .substring( nameWithoutExtension.length() + 1 ) : "";
                if ( "yml".equals( extension ) )
                {
                    return DescriptorKey.from( key, nameWithoutExtension, extension );
                }
                else
                {
                    return DescriptorKey.from( key, nameWithoutExtension );
                }
            } )
            .collect( DescriptorKeys.collector() );
    }

    private static String getNameWithoutExtension( final String name )
    {
        final int pos = name.lastIndexOf( '.' );
        return pos > 0 ? name.substring( 0, pos ) : name;
    }
}
