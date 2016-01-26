package com.enonic.xp.core.impl.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlServiceDescriptorParser;

@Component(immediate = true)
public class ServiceDescriptorServiceImpl
    implements ServiceDescriptorService
{
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

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
