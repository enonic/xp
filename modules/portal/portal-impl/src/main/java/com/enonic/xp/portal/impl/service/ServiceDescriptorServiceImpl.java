package com.enonic.xp.portal.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.service.ServiceDescriptors;

@Component(immediate = true)
public final class ServiceDescriptorServiceImpl
    implements ServiceDescriptorService
{
    private static final Logger LOG = LoggerFactory.getLogger( ServiceDescriptorServiceImpl.class );

    private static final String ROOT_PATH = "/services";

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public ServiceDescriptorServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( this.resourceService, ROOT_PATH, true );
    }

    @Override
    public ServiceDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        final ResourceProcessor<DescriptorKey, ServiceDescriptor> processor = newRootProcessor( descriptorKey );
        final ServiceDescriptor descriptor = this.resourceService.processResource( processor );

        return Objects.requireNonNullElseGet( descriptor, () -> createDefaultDescriptor( descriptorKey ) );
    }

    @Override
    public ServiceDescriptors getByApplication( final ApplicationKey key )
    {
        final List<ServiceDescriptor> list = new ArrayList<>();
        for ( final DescriptorKey descriptorKey : descriptorKeyLocator.findKeys( key ) )
        {
            try
            {
                final ServiceDescriptor descriptor = getByKey( descriptorKey );
                if ( descriptor != null )
                {
                    list.add( descriptor );
                }
            }
            catch ( final IllegalArgumentException e )
            {
                LOG.error( "Error in page descriptor: {}", descriptorKey.toString(), e );
            }
        }

        return ServiceDescriptors.from( list );
    }

    private ResourceProcessor<DescriptorKey, ServiceDescriptor> newRootProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, ServiceDescriptor>().key( key )
            .segment( "rootServiceDescriptor" )
            .keyTranslator( this::toResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private ServiceDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        return YmlServiceDescriptorParser.parse( resource.readString(), key.getApplicationKey() ).key( key ).build();
    }

    private ServiceDescriptor createDefaultDescriptor( final DescriptorKey descriptorKey )
    {
        return ServiceDescriptor.create().key( descriptorKey ).build();
    }

    private ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), ROOT_PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }
}
