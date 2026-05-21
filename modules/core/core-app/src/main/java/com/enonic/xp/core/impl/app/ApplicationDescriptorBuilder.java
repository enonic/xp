package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.icon.Icon;

final class ApplicationDescriptorBuilder
{
    private static final String APP_DESCRIPTOR_PATH_YAML = "application.yaml";

    private static final String ENONIC_APP_DESCRIPTOR_PATH_YAML = "enonic.yaml";

    private static final String APP_DESCRIPTOR_PATH_YML = "application.yml";

    private static final String ENONIC_APP_DESCRIPTOR_PATH_YML = "enonic.yml";

    private static final String APP_ICON_FILENAME = "application.svg";

    private static final String ENONIC_APP_ICON_FILENAME = "enonic.svg";

    private static final List<String> DESCRIPTOR_PATHS =
        List.of( ENONIC_APP_DESCRIPTOR_PATH_YAML, ENONIC_APP_DESCRIPTOR_PATH_YML, APP_DESCRIPTOR_PATH_YAML, APP_DESCRIPTOR_PATH_YML );

    private Bundle bundle;

    public ApplicationDescriptorBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public ApplicationDescriptor build()
    {
        final String applicationName = ApplicationBundleUtils.getApplicationName( bundle );
        final ApplicationKey applicationKey = ApplicationKey.from( applicationName );

        final URL url = resolveDescriptorUrl( bundle );

        final ApplicationDescriptor.Builder appDescriptorBuilder;
        if ( url != null )
        {
            final String yaml = readAppYml( url );
            appDescriptorBuilder = YmlApplicationDescriptorParser.parse( yaml, applicationKey );
        }
        else
        {
            appDescriptorBuilder = ApplicationDescriptor.create().key( applicationKey );
        }

        if ( hasAppIcon( bundle ) )
        {
            final URL iconUrl = Objects.requireNonNullElseGet( bundle.getResource( ENONIC_APP_ICON_FILENAME ),
                                                               () -> bundle.getResource( APP_ICON_FILENAME ) );
            try (InputStream stream = iconUrl.openStream())
            {
                final byte[] iconData = stream.readAllBytes();
                final Icon icon = Icon.from( iconData, "image/svg+xml", Instant.ofEpochMilli( this.bundle.getLastModified() ) );
                appDescriptorBuilder.icon( icon );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "Unable to load application icon for " + applicationName, e );
            }
        }

        return appDescriptorBuilder.build();
    }

    private static URL resolveDescriptorUrl( final Bundle bundle )
    {
        return DESCRIPTOR_PATHS.stream()
            .filter( path -> bundle.getEntry( path ) != null )
            .findFirst()
            .map( bundle::getResource )
            .orElse( null );
    }

    private String readAppYml( final URL yamlURL )
    {
        try (InputStream stream = yamlURL.openStream())
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Invalid application descriptor file", e );
        }
    }

    public static boolean hasAppDescriptor( final Bundle bundle )
    {
        return DESCRIPTOR_PATHS.stream().anyMatch( path -> bundle.getEntry( path ) != null );
    }

    private boolean hasAppIcon( final Bundle bundle )
    {
        return bundle.getEntry( ENONIC_APP_ICON_FILENAME ) != null || bundle.getEntry( APP_ICON_FILENAME ) != null;
    }
}
