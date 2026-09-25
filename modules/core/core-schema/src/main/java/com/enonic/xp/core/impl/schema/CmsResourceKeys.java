package com.enonic.xp.core.impl.schema;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

/**
 * Resource keys of CMS schema descriptors and their icons.
 * <p>
 * A schema is resolved in the flat structure {@code <root>/<name>.yaml|yml}, with its icon {@code <root>/<name>.svg|png} next to it,
 * and in the legacy folder structure {@code <root>/<name>/<name>.yaml|yml} still used by application bundles (local applications,
 * applications that do not persist their schema). The flat structure wins when both exist, {@code .yaml} wins over {@code .yml}.
 * Controllers are not schemas: they stay in the folder structure.
 */
public final class CmsResourceKeys
{
    private static final String[] DESCRIPTOR_EXTENSIONS = {"yaml", "yml"};

    private CmsResourceKeys()
    {
    }

    /**
     * Key of the existing descriptor of the schema {@code name} below {@code root} (e.g. {@code /cms/content-types}),
     * or the key of the flat {@code .yaml} descriptor if the schema has none.
     */
    public static ResourceKey descriptorKey( final ResourceService resourceService, final ApplicationKey applicationKey, final String root,
                                             final String name )
    {
        final ResourceKey key = findDescriptorKey( resourceService, applicationKey, root, name );
        return key != null ? key : ResourceKey.from( applicationKey, root + "/" + name + "." + DESCRIPTOR_EXTENSIONS[0] );
    }

    /**
     * Key of the existing descriptor of the schema {@code name} below {@code root}, or {@code null} if the schema has none.
     */
    public static ResourceKey findDescriptorKey( final ResourceService resourceService, final ApplicationKey applicationKey,
                                                 final String root, final String name )
    {
        for ( final String basePath : new String[]{root + "/" + name, root + "/" + name + "/" + name} )
        {
            for ( final String extension : DESCRIPTOR_EXTENSIONS )
            {
                final ResourceKey key = ResourceKey.from( applicationKey, basePath + "." + extension );
                if ( resourceService.getResource( key ).exists() )
                {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * Key of the resource of the application with the given extension next to the descriptor, e.g. its {@code svg} or {@code png} icon:
     * the icon lives in the same structure as the descriptor. The application is passed explicitly, a resolved descriptor resource
     * may carry the key of the application actually serving it (e.g. the system application for reserved application keys).
     */
    public static ResourceKey siblingKey( final ApplicationKey applicationKey, final ResourceKey descriptorKey, final String extension )
    {
        final String path = descriptorKey.getPath();
        final int dot = path.lastIndexOf( '.' );
        final int slash = path.lastIndexOf( '/' );
        final String basePath = dot > slash ? path.substring( 0, dot ) : path;
        return ResourceKey.from( applicationKey, basePath + "." + extension );
    }
}