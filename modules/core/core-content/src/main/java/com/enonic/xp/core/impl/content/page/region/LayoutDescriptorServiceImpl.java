package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;

@Component
public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private final DescriptorService descriptorService;

    @Activate
    public LayoutDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        return descriptorService.get( LayoutDescriptor.class, key );
    }

    @Override
    public LayoutDescriptors getByApplication( final ApplicationKey key )
    {
        return LayoutDescriptors.from( descriptorService.get( LayoutDescriptor.class, ApplicationKeys.from( key ) ) );
    }

    @Override
    public LayoutDescriptors getByApplications( final ApplicationKeys keys )
    {
        return LayoutDescriptors.from( descriptorService.get( LayoutDescriptor.class, keys ) );
    }
}
