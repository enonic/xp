package com.enonic.xp.admin.impl.tool;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorService;

@Component(immediate = true)
public final class AdminToolDescriptorServiceImpl
    implements AdminToolDescriptorService
{
    private final DescriptorService descriptorService;

    @Activate
    public AdminToolDescriptorServiceImpl( @Reference final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }

    @Override
    public AdminToolDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        return this.descriptorService.get( AdminToolDescriptor.class, ApplicationKeys.from( applicationKey ) )
            .stream()
            .collect( AdminToolDescriptors.collector() );
    }

    @Override
    public AdminToolDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return this.descriptorService.get( AdminToolDescriptor.class, descriptorKey );
    }

    @Override
    public AdminToolDescriptors getAll()
    {
        return this.descriptorService.getAll( AdminToolDescriptor.class ).stream().collect( AdminToolDescriptors.collector() );
    }
}
