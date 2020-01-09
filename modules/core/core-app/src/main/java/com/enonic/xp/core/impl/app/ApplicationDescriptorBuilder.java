package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.xml.parser.XmlApplicationParser;

final class ApplicationDescriptorBuilder
{
    private static final String APP_DESCRIPTOR_FILENAME = "application.xml";

    private static final String APP_ICON_FILENAME = "application.svg";

    private Bundle bundle;

    public ApplicationDescriptorBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public ApplicationDescriptor build()
    {
        final URL url = bundle.getResource( APP_DESCRIPTOR_FILENAME );
        final String xml = parseAppXml( url );

        ApplicationDescriptor.Builder appDescriptorBuilder = ApplicationDescriptor.create();
        final XmlApplicationParser parser = new XmlApplicationParser().
            currentApplication( ApplicationKey.from( bundle ) ).
            appDescriptorBuilder( appDescriptorBuilder ).
            source( xml );
        parser.parse();

        if ( hasAppIcon( bundle ) )
        {
            final URL iconUrl = bundle.getResource( APP_ICON_FILENAME );
            try (final InputStream stream = iconUrl.openStream())
            {
                final byte[] iconData = stream.readAllBytes();
                final Icon icon = Icon.from( iconData, "image/svg+xml", Instant.ofEpochMilli( this.bundle.getLastModified() ) );
                appDescriptorBuilder.icon( icon );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "Unable to load application icon for " + bundle.getSymbolicName(), e );
            }
        }

        return appDescriptorBuilder.build();
    }

    private String parseAppXml( final URL siteXmlURL )
    {
        try (InputStream stream = siteXmlURL.openStream())
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Invalid " + APP_DESCRIPTOR_FILENAME + " file", e );
        }
    }

    public static boolean hasAppDescriptor( final Bundle bundle )
    {
        return ( bundle.getEntry( APP_DESCRIPTOR_FILENAME ) != null );
    }

    private boolean hasAppIcon( final Bundle bundle )
    {
        return ( bundle.getEntry( APP_ICON_FILENAME ) != null );
    }
}
