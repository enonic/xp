package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.icon.Icon;

final class ApplicationDescriptorBuilder
{
    private static final String APP_DESCRIPTOR_PATH_YAML = "application.yaml";

    private static final String APP_DESCRIPTOR_PATH_YML = "application.yml";

    private static final String APP_ICON_FILENAME = "application.svg";

    private Bundle bundle;

    public ApplicationDescriptorBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public ApplicationDescriptor build()
    {
        final URL url = resolveDescriptorUrl( bundle );
        final String yaml = readAppYml( url );

        final String applicationName = ApplicationBundleUtils.getApplicationName( bundle );

        final ApplicationDescriptor.Builder appDescriptorBuilder =
            YmlApplicationDescriptorParser.parse( yaml, ApplicationKey.from( applicationName ) );

        if ( hasAppIcon( bundle ) )
        {
            final URL iconUrl = bundle.getResource( APP_ICON_FILENAME );
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
        final URL yamlUrl = bundle.getEntry( APP_DESCRIPTOR_PATH_YAML );
        if ( yamlUrl != null )
        {
            return bundle.getResource( APP_DESCRIPTOR_PATH_YAML );
        }
        return bundle.getResource( APP_DESCRIPTOR_PATH_YML );
    }

    private String readAppYml( final URL siteYmlURL )
    {
        try (InputStream stream = siteYmlURL.openStream())
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
        return bundle.getEntry( APP_DESCRIPTOR_PATH_YAML ) != null || bundle.getEntry( APP_DESCRIPTOR_PATH_YML ) != null;
    }

    private boolean hasAppIcon( final Bundle bundle )
    {
        return ( bundle.getEntry( APP_ICON_FILENAME ) != null );
    }
}
