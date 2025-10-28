package com.enonic.xp.admin.impl.extension;

import java.util.Arrays;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.admin.extension.AdminExtensionDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;

@Component(immediate = true)
public final class AdminExtensionDescriptorServiceImpl
    implements AdminExtensionDescriptorService
{
    private final DescriptorService descriptorService;

    @Activate
    public AdminExtensionDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public Descriptors<AdminExtensionDescriptor> getByInterfaces( final String... interfaceNames )
    {
        return this.descriptorService.getAll( AdminExtensionDescriptor.class )
            .stream()
            .filter( widgetDescriptor -> Arrays.stream( interfaceNames ).anyMatch( widgetDescriptor::hasInterface ) )
            .collect( Descriptors.collector() );
    }

    @Override
    public Descriptors<AdminExtensionDescriptor> getByApplication( final ApplicationKey key )
    {
        return this.descriptorService.get( AdminExtensionDescriptor.class, ApplicationKeys.from( key ) );
    }

    @Override
    public AdminExtensionDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return this.descriptorService.get( AdminExtensionDescriptor.class, descriptorKey );
    }
}
