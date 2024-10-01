package com.enonic.xp.admin.impl.portal.widget;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteStreams;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.icon.Icon;

@Component(immediate = true, service = WidgetIconResolver.class)
public class WidgetIconResolver
{
    private static final String DEFAULT_WIDGET_ICON = "widget.svg";

    private final ApplicationDescriptorService applicationDescriptorService;

    @Activate
    public WidgetIconResolver( @Reference final ApplicationDescriptorService applicationDescriptorService )
    {
        this.applicationDescriptorService = applicationDescriptorService;
    }

    public Icon resolve( final WidgetDescriptor widgetDescriptor )
    {
        if ( widgetDescriptor.getIcon() != null )
        {
            return widgetDescriptor.getIcon();
        }

        final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( widgetDescriptor.getApplicationKey() );

        if ( appDescriptor != null && appDescriptor.getIcon() != null )
        {
            return appDescriptor.getIcon();
        }

        return loadDefaultIcon();
    }

    private Icon loadDefaultIcon()
    {
        try (InputStream in = getClass().getResourceAsStream( DEFAULT_WIDGET_ICON ))
        {
            return Icon.from( ByteStreams.toByteArray( in ), "image/svg+xml", Instant.ofEpochMilli( 0L ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( String.format( "Failed to load default image: %s", DEFAULT_WIDGET_ICON ), e );
        }
    }
}
