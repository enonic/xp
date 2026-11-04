package com.enonic.xp.core.impl.app;

import java.time.Instant;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

/**
 * Builds the {@link ApplicationDescriptor} from the application descriptor resource and the icon next to it, both served by the
 * {@link ResourceService} the same way as any other application resource: from the persisted schema nodes when the application
 * owns its schema, from the bundle otherwise.
 */
final class ApplicationDescriptorBuilder
{
    private ApplicationDescriptorBuilder()
    {
    }

    static ApplicationDescriptor build( final ApplicationKey applicationKey, final Resource descriptorResource,
                                        final ResourceService resourceService )
    {
        final ApplicationDescriptor.Builder builder = descriptorResource.exists()
            ? YmlApplicationDescriptorParser.parse( readDescriptor( descriptorResource ), applicationKey )
            : ApplicationDescriptor.create().key( applicationKey );

        final Resource iconResource = SchemaResourcePaths.APP_ICON_PATHS.stream()
            .map( path -> resourceService.getResource( ResourceKey.from( applicationKey, "/" + path ) ) )
            .filter( Resource::exists )
            .findFirst()
            .orElse( null );
        if ( iconResource != null )
        {
            try
            {
                builder.icon( Icon.from( iconResource.readBytes(), SchemaResourcePaths.iconMimeType( iconResource.getKey().getPath() ),
                                         Instant.ofEpochMilli( Math.max( iconResource.getTimestamp(), 0 ) ) ) );
            }
            catch ( final Exception e )
            {
                throw new RuntimeException( "Unable to load application icon for " + applicationKey, e );
            }
        }

        return builder.build();
    }

    private static String readDescriptor( final Resource resource )
    {
        try
        {
            return resource.readString();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Invalid application descriptor file", e );
        }
    }
}
