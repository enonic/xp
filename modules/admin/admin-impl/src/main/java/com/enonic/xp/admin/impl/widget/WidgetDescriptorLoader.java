package com.enonic.xp.admin.impl.widget;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class WidgetDescriptorLoader
    implements DescriptorLoader<WidgetDescriptor>
{
    private final static String PATH = "/admin/widgets";

    private ResourceService resourceService;

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

    private void parseXml( final ApplicationKey applicationKey, final WidgetDescriptor.Builder builder, final String xml )
    {
        final XmlWidgetDescriptorParser parser = new XmlWidgetDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }
}
