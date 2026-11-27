package com.enonic.xp.descriptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;

public final class DescriptorKeyLocator
{
    private final ResourceService service;

    private final String pattern;

    /**
     * Descriptors are located in a folder named like the descriptor: {@code <path>/<name>/<name>.yaml|yml}.
     * With {@code optional}, a {@code <path>/<name>/<name>.js} controller without descriptor counts as well, for descriptors
     * the owner can do without, such as a service. A CMS schema ({@code path} below {@code /cms}: a part, page, layout, macro,
     * content type, ...) exists only by its own descriptor, which is also located in the flat structure
     * {@code <path>/<name>.yaml|yml}.
     */
    public DescriptorKeyLocator( final ResourceService service, final String path, final boolean optional )
    {
        this.service = service;
        final String folder = "(?<name>[^/]+)/\\k<name>\\.(?:yaml|yml" + ( optional ? "|js" : "" ) + ")";
        final String flat = "[^/]+\\.(?:yaml|yml)";
        this.pattern = "^" + path + "/(?:" + folder + ( isCms( path ) ? "|" + flat : "" ) + ")$";
    }

    private static boolean isCms( final String path )
    {
        return path.startsWith( "/cms/" ) || path.startsWith( "cms/" );
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
