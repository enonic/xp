package com.enonic.xp.portal.impl.api;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;

@Component(immediate = true)
public final class ApiDescriptorServiceImpl
    implements ApiDescriptorService
{
    private final ResourceService resourceService;

    @Activate
    public ApiDescriptorServiceImpl( final @Reference ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public ApiDescriptor getByApplication( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, ApiDescriptor> processor = newRootProcessor( applicationKey );
        return this.resourceService.processResource( processor );
    }

    private ResourceProcessor<ApplicationKey, ApiDescriptor> newRootProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, ApiDescriptor>().key( applicationKey )
            .segment( "rootApiDescriptor" )
            .keyTranslator( ApiDescriptor::toResourceKey )
            .processor( resource -> loadDescriptor( applicationKey, resource ) )
            .build();
    }

    private ApiDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        final ApiDescriptor.Builder builder = ApiDescriptor.create();
        builder.applicationKey( key );
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
