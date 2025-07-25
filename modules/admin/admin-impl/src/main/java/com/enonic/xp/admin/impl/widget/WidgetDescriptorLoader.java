package com.enonic.xp.admin.impl.widget;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class WidgetDescriptorLoader
    implements DescriptorLoader<WidgetDescriptor>
{
    private static final String PATH = "/admin/widgets";

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public WidgetDescriptorLoader( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( this.resourceService, PATH, false );
    }

    @Override
    public Class<WidgetDescriptor> getType()
    {
        return WidgetDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
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
        final String iconPath = PATH + "/" + key.getName() + "/" + key.getName() + ".svg";

        final ResourceKey resourceKey = ResourceKey.from( key.getApplicationKey(), iconPath );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), "image/svg+xml", modifiedTime );
    }
}
