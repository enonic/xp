package com.enonic.xp.core.impl.service;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.service.ServiceDescriptors;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlServiceDescriptorParser;

@Component(immediate = true)
public final class ServiceDescriptorServiceImpl
    implements ServiceDescriptorService
{
    private final static Logger LOG = LoggerFactory.getLogger( ServiceDescriptorServiceImpl.class );

    private final static String ROOT_PATH = "/services";

    private final static String SITE_PATH = "/site/services";

    private ResourceService resourceService;

    @Override
    public ServiceDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        ResourceProcessor<DescriptorKey, ServiceDescriptor> processor = newRootProcessor( descriptorKey );
        ServiceDescriptor descriptor = this.resourceService.processResource( processor );
        if ( descriptor == null )
        {
            processor = newSiteProcessor( descriptorKey );
            descriptor = this.resourceService.processResource( processor );
        }
        if ( descriptor != null )
        {
            return descriptor;
        }

        return createDefaultDescriptor( descriptorKey );
    }

    @Override
    public ServiceDescriptors getByApplication( final ApplicationKey key )
    {
        final List<ServiceDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( key ) )
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
                LOG.error( "Error in page descriptor: " + descriptorKey.toString(), e );
            }
        }

        return ServiceDescriptors.from( list );
    }

    private ResourceProcessor<DescriptorKey, ServiceDescriptor> newRootProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, ServiceDescriptor>().
            key( key ).
            segment( "rootServiceDescriptor" ).
            keyTranslator( ServiceDescriptor::toRootResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private ResourceProcessor<DescriptorKey, ServiceDescriptor> newSiteProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, ServiceDescriptor>().
            key( key ).
            segment( "siteServiceDescriptor" ).
            keyTranslator( ServiceDescriptor::toSiteResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private ServiceDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final ServiceDescriptor.Builder builder = ServiceDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );

        return builder.build();
    }

    private void parseXml( final Resource resource, final ServiceDescriptor.Builder builder )
    {
        try
        {
            new XmlServiceDescriptorParser().
                builder( builder ).
                currentApplication( resource.getKey().getApplicationKey() ).
                source( resource.readString() ).
                parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load service descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    private ServiceDescriptor createDefaultDescriptor( final DescriptorKey descriptorKey )
    {
        return ServiceDescriptor.create().
            key( descriptorKey ).
            build();
    }

    private Iterable<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return Iterables.concat( findDescriptorKeys( ROOT_PATH, key ), findDescriptorKeys( SITE_PATH, key ) );
    }

    private Iterable<DescriptorKey> findDescriptorKeys( final String path, final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, path, true ).findKeys( key );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
