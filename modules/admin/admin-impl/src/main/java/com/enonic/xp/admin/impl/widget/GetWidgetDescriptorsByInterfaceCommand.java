package com.enonic.xp.admin.impl.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Files;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

final class GetWidgetDescriptorsByInterfaceCommand
    extends AbstractGetWidgetDescriptorCommand<GetWidgetDescriptorsByInterfaceCommand>
{
    private final static String PATH = "/admin/widgets/";

    private String interfaceName;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public WidgetDescriptors execute()
    {
        final ApplicationKeys keys = this.applicationService.getInstalledApplicationKeys();
        final List<WidgetDescriptor> widgetDescriptorList = getDescriptorsFromModules( keys ).
            stream().
            filter( widgetDescriptor -> widgetDescriptor.getInterfaces().contains( interfaceName ) ).
            collect( Collectors.toList() );

        return WidgetDescriptors.from( widgetDescriptorList );
    }

    public GetWidgetDescriptorsByInterfaceCommand interfaceName( final String interfaceName )
    {
        this.interfaceName = interfaceName;
        return this;
    }

    public final GetWidgetDescriptorsByInterfaceCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    public final GetWidgetDescriptorsByInterfaceCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    protected WidgetDescriptor getDescriptor( final DescriptorKey key )
    {
        if ( key == null )
        {
            return null;
        }

        final ResourceKey resourceKey = ResourceKey.from( key.getApplicationKey(), PATH + key.getName() + "/" + key.getName() + ".xml" );
        final Resource resource = this.resourceService.getResource( resourceKey );

        final WidgetDescriptor.Builder builder = WidgetDescriptor.create();

        if ( resource.exists() )
        {
            final String descriptorXml = resource.readString();
            try
            {
                parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load widget descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
        }
        else
        {
            builder.displayName( key.getName() );
        }

        builder.key( key );

        return builder.build();
    }

    private void readDescriptorsFromApp( final List<WidgetDescriptor> list, final ApplicationKey appKey )
    {
        final ResourceKeys resourceKeys = this.resourceService.findFiles( appKey, PATH + ".+\\.xml" );
        for ( final ResourceKey resourceKey : resourceKeys )
        {
            final DescriptorKey key = newDescriptorKey( appKey, resourceKey );
            final WidgetDescriptor descriptor = getDescriptor( key );
            if ( descriptor != null )
            {
                list.add( descriptor );
            }
        }
    }

    private WidgetDescriptors getDescriptorsFromModules( final ApplicationKeys appKeys )
    {
        final List<WidgetDescriptor> list = new ArrayList<>();
        for ( final ApplicationKey appKey : appKeys )
        {
            readDescriptorsFromApp( list, appKey );
        }

        return WidgetDescriptors.from( list );
    }

    private DescriptorKey newDescriptorKey( final ApplicationKey appKey, final ResourceKey key )
    {
        final String nameWithExt = key.getName();
        final String nameWithoutExt = Files.getNameWithoutExtension( nameWithExt );

        if ( key.getPath().equals( PATH + nameWithoutExt + "/" + nameWithExt ) )
        {
            return DescriptorKey.from( appKey, nameWithoutExt );
        }

        return null;
    }
}
