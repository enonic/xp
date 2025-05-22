package com.enonic.xp.core.impl.content.page;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;

@Component(immediate = true)
public final class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    private final DescriptorService descriptorService;

    @Activate
    public PageDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public PageDescriptor getByKey( final DescriptorKey key )
    {
        return descriptorService.get( PageDescriptor.class, key );
    }

    @Override
    public PageDescriptors getByApplication( final ApplicationKey key )
    {
        return PageDescriptors.from( descriptorService.get( PageDescriptor.class, ApplicationKeys.from( key ) ) );
    }

    @Override
    public PageDescriptors getByApplications( final ApplicationKeys keys )
    {
        return PageDescriptors.from( descriptorService.get( PageDescriptor.class, keys ) );
    }
}
