package com.enonic.xp.admin.impl.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

final class GetWidgetDescriptorsByInterfaceCommand
    extends AbstractGetWidgetDescriptorCommand<GetWidgetDescriptorsByInterfaceCommand>
{
    private final static String PATH = "admin/widgets/";

    private String interfaceName;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public WidgetDescriptors execute()
    {
        final Applications applications = this.applicationService.getAllApplications();
        final List<WidgetDescriptor> widgetDescriptorList = getDescriptorsFromModules( applications ).
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

    private WidgetDescriptors getDescriptorsFromModules( final Applications applications )
    {
        final List<WidgetDescriptor> widgetDescriptors = new ArrayList<>();
        for ( final Application application : applications )
        {
            final ResourceKeys keys = this.resourceService.findResourceKeys( application.getKey(), PATH, "*", false );
            Set<Resource> resourceSet = Sets.newHashSet();

            if ( keys != null )
            {
                for ( final ResourceKey key : keys )
                {
                    resourceSet.add( this.resourceService.getResource( key ) );
                }
            }

            for ( final Resource resource : resourceSet )
            {
                final String descriptorName = resource.getKey().getName();
                final DescriptorKey key = DescriptorKey.from( application.getKey(), descriptorName );
                final WidgetDescriptor widgetDescriptor = getDescriptor( key );
                if ( widgetDescriptor != null )
                {
                    widgetDescriptors.add( widgetDescriptor );
                }
            }
        }

        return WidgetDescriptors.from( widgetDescriptors );
    }

}