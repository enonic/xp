package com.enonic.xp.admin.impl.widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class WidgetDescriptorLoader
    implements DescriptorLoader<WidgetDescriptor>
{
    private final static String PATH = "/admin/widgets";

    private ResourceService resourceService;

    private ApplicationService applicationService;

    @Override
    public Class<WidgetDescriptor> getType()
    {
        return WidgetDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return DescriptorKeys.from( new DescriptorKeyLocator( this.resourceService, PATH, true ).findKeys( key ) );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    public WidgetDescriptor load( final DescriptorKey key, final Resource resource )
    {
        final WidgetDescriptor.Builder builder = WidgetDescriptor.create();
        builder.key( key );

        final String descriptorXml = resource.readString();
        parseXml( key.getApplicationKey(), builder, descriptorXml );
        final Icon icon = loadIcon( key );
        builder.setIcon( icon );
        return builder.build();
    }

    @Override
    public WidgetDescriptor createDefault( final DescriptorKey key )
    {
        return WidgetDescriptor.create().key( key ).displayName( key.getName() ).build();
    }

    @Override
    public WidgetDescriptor postProcess( final WidgetDescriptor descriptor )
    {
        return descriptor;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    private void parseXml( final ApplicationKey applicationKey, final WidgetDescriptor.Builder builder, final String xml )
    {
        final XmlWidgetDescriptorParser parser = new XmlWidgetDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }

    private Icon loadIcon( final DescriptorKey key )
    {
        final Application application = this.applicationService.getInstalledApplication( key.getApplicationKey() );

        if ( application != null )
        {
            final String iconPath = PATH + "/" + key.getName() + "/" + key.getName() + ".svg";
            final Bundle bundle = application.getBundle();

            if ( this.hasAppIcon( bundle, iconPath ) )
            {
                final URL iconUrl = bundle.getResource( iconPath );
                try (InputStream stream = iconUrl.openStream())
                {
                    final byte[] iconData = stream.readAllBytes();
                    return Icon.from( iconData, "image/svg+xml", Instant.ofEpochMilli( bundle.getLastModified() ) );
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( "Unable to load widget icon for " + bundle.getSymbolicName() + ":" + key.getName(), e );
                }
            }
        }

        return null;
    }

    private boolean hasAppIcon( final Bundle bundle, final String iconPath )
    {
        final URL entry = bundle.getEntry( iconPath );
        return entry != null;
    }
}
