package com.enonic.xp.core.impl.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;
import com.enonic.xp.widget.WidgetDescriptor;
import com.enonic.xp.widget.WidgetDescriptors;

final class GetWidgetDescriptorsByInterfaceCommand
    extends AbstractGetWidgetDescriptorCommand<GetWidgetDescriptorsByInterfaceCommand>
{
    private final static String PATH = "/ui/widgets";

    private String interfaceName;

    private ApplicationService applicationService;

    private ResourceService resourceService;


    public WidgetDescriptors execute()
    {
        final Applications applications = this.applicationService.getAllModules();
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

    private WidgetDescriptors getDescriptorsFromModules( final Applications applications )
    {
        final List<WidgetDescriptor> widgetDescriptors = new ArrayList<>();
        for ( final Application application : applications )
        {
            final Resources resources = this.resourceService.findResources( application.getKey(), PATH, "*", false );

            for ( final Resource resource : resources )
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