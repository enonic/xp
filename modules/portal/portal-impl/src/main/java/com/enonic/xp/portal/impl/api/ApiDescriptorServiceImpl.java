package com.enonic.xp.portal.impl.api;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

@Component(immediate = true)
public final class ApiDescriptorServiceImpl
    implements ApiDescriptorService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApiDescriptorServiceImpl.class );

    private final ResourceService resourceService;

    private final ApiDescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public ApiDescriptorServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new ApiDescriptorKeyLocator( this.resourceService );
    }

    @Override
    public ApiDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        final ResourceProcessor<DescriptorKey, ApiDescriptor> processor = newRootProcessor( descriptorKey );
        return resourceService.processResource( processor );
    }

    @Override
    public ApiDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        final List<ApiDescriptor> list = new ArrayList<>();
        for ( final DescriptorKey descriptorKey : descriptorKeyLocator.findKeys( applicationKey ) )
        {
            try
            {
                list.add( getByKey( descriptorKey ) );
            }
            catch ( final IllegalArgumentException e )
            {
                LOG.error( "Error in api descriptor: {}", descriptorKey.toString(), e );
            }
        }

        return ApiDescriptors.from( list );
    }

    private ResourceProcessor<DescriptorKey, ApiDescriptor> newRootProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, ApiDescriptor>().key( key )
            .segment( "rootApiDescriptor" )
            .keyTranslator( descriptorKey -> ApiDescriptor.toResourceKey( descriptorKey, "xml" ) )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private ApiDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final ApiDescriptor.Builder builder = ApiDescriptor.create();
        builder.key( key );
        parseXml( resource, builder );
        return builder.build();
    }

    private void parseXml( final Resource resource, final ApiDescriptor.Builder builder )
    {
        try
        {
            new XmlApiDescriptorParser( builder ).currentApplication( resource.getKey().getApplicationKey() )
                .source( resource.readString() )
                .parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load api descriptor [" + resource.getKey() + "]: " + e.getMessage() );
        }
    }
}
