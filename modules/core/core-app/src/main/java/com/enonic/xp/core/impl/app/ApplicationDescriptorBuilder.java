package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;

/**
 * Builds the {@link ApplicationDescriptor} from the application descriptor and icon resources, resolved the same way as any other
 * application resource: from the persisted schema nodes when the application owns its schema, from the bundle otherwise.
 */
final class ApplicationDescriptorBuilder
{
    private static final List<String> APP_ICON_PATHS = List.of( SchemaResourcePaths.APP_ICON_NAME, "application.svg" );

    private ApplicationDescriptorBuilder()
    {
    }

    static ApplicationDescriptor build( final ApplicationKey applicationKey, final ApplicationUrlResolver urlResolver )
    {
        final Resource descriptorResource = findFirst( urlResolver, ApplicationBundleUtils.DESCRIPTOR_PATHS );

        final ApplicationDescriptor.Builder builder;
        if ( descriptorResource != null )
        {
            builder = YmlApplicationDescriptorParser.parse( readDescriptor( descriptorResource ), applicationKey );
        }
        else
        {
            builder = ApplicationDescriptor.create().key( applicationKey );
        }

        final Resource iconResource = findFirst( urlResolver, APP_ICON_PATHS );
        if ( iconResource != null )
        {
            try
            {
                builder.icon( Icon.from( iconResource.readBytes(), SchemaResourcePaths.SVG_MIME_TYPE,
                                         Instant.ofEpochMilli( Math.max( iconResource.getTimestamp(), 0 ) ) ) );
            }
            catch ( final Exception e )
            {
                throw new RuntimeException( "Unable to load application icon for " + applicationKey, e );
            }
        }

        return builder.build();
    }

    private static Resource findFirst( final ApplicationUrlResolver urlResolver, final List<String> paths )
    {
        return paths.stream()
            .map( path -> urlResolver.findResource( "/" + path ) )
            .filter( Objects::nonNull )
            .filter( Resource::exists )
            .findFirst()
            .orElse( null );
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