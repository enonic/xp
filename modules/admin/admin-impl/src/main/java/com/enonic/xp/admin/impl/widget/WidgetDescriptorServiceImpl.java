package com.enonic.xp.admin.impl.widget;

import java.util.Arrays;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;

@Component(immediate = true)
public final class WidgetDescriptorServiceImpl
    implements WidgetDescriptorService
{
    private final DescriptorService descriptorService;

    @Activate
    public WidgetDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public Descriptors<WidgetDescriptor> getByInterfaces( final String... interfaceNames )
    {
        return this.descriptorService.getAll( WidgetDescriptor.class )
            .filter( widgetDescriptor -> Arrays.stream( interfaceNames ).anyMatch( widgetDescriptor::hasInterface ) );
    }

    @Override
    public Descriptors<WidgetDescriptor> getByApplication( final ApplicationKey key )
    {
        return this.descriptorService.get( WidgetDescriptor.class, ApplicationKeys.from( key ) );
    }

    @Override
    public WidgetDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return this.descriptorService.get( WidgetDescriptor.class, descriptorKey );
    }
}
