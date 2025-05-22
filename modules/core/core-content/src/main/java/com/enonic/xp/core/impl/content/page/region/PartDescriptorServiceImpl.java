package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    final DescriptorService descriptorService;

    @Activate
    public PartDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public PartDescriptor getByKey( final DescriptorKey key )
    {
        return descriptorService.get( PartDescriptor.class, key );
    }

    @Override
    public PartDescriptors getByApplication( final ApplicationKey key )
    {
        return PartDescriptors.from( descriptorService.get( PartDescriptor.class, ApplicationKeys.from( key ) ) );
    }

    @Override
    public PartDescriptors getByApplications( final ApplicationKeys keys )
    {
        return PartDescriptors.from( descriptorService.get( PartDescriptor.class, keys ) );
    }
}
