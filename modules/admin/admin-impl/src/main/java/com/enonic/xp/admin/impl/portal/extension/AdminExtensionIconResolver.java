package com.enonic.xp.admin.impl.portal.extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteStreams;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.icon.Icon;

@Component(immediate = true, service = AdminExtensionIconResolver.class)
public class AdminExtensionIconResolver
{
    private static final String DEFAULT_EXTENSION_ICON = "extension.svg";

    private final ApplicationDescriptorService applicationDescriptorService;

    @Activate
    public AdminExtensionIconResolver( @Reference final ApplicationDescriptorService applicationDescriptorService )
    {
        this.applicationDescriptorService = applicationDescriptorService;
    }

    public Icon resolve( final AdminExtensionDescriptor descriptor )
    {
        if ( descriptor.getIcon() != null )
        {
            return descriptor.getIcon();
        }

        final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( descriptor.getApplicationKey() );

        if ( appDescriptor != null && appDescriptor.getIcon() != null )
        {
            return appDescriptor.getIcon();
        }

        return loadDefaultIcon();
    }

    private Icon loadDefaultIcon()
    {
        try (InputStream in = getClass().getResourceAsStream( DEFAULT_EXTENSION_ICON ))
        {
            return Icon.from( ByteStreams.toByteArray( in ), "image/svg+xml", Instant.ofEpochMilli( 0L ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( String.format( "Failed to load default image: %s", DEFAULT_EXTENSION_ICON ), e );
        }
    }
}
