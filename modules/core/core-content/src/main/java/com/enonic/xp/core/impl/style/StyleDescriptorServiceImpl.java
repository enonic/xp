package com.enonic.xp.core.impl.style;

import java.time.Instant;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.content.parser.YmlStyleDescriptorParser;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

@Component(immediate = true)
public class StyleDescriptorServiceImpl
    implements StyleDescriptorService
{
    private static final Logger LOG = LoggerFactory.getLogger( StyleDescriptorServiceImpl.class );

    private ResourceService resourceService;

    private ApplicationService applicationService;

    @Override
    public StyleDescriptor getByApplication( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, StyleDescriptor> processor = newProcessor( applicationKey );
        return this.resourceService.processResource( processor );
    }

    @Override
    public StyleDescriptors getByApplications( final ApplicationKeys applicationKeys )
    {
        return applicationKeys.stream().map( this::getByApplication ).filter( Objects::nonNull ).collect( StyleDescriptors.collector() );
    }

    @Override
    public StyleDescriptors getAll()
    {
        return this.applicationService.getInstalledApplications()
            .stream()
            .map( Application::getKey )
            .map( this::getByApplication )
            .filter( Objects::nonNull )
            .collect( StyleDescriptors.collector() );
    }

    private ResourceProcessor<ApplicationKey, StyleDescriptor> newProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, StyleDescriptor>().key( applicationKey )
            .segment( "styleDescriptor" )
            .keyTranslator( StyleDescriptor::toResourceKey )
            .processor( resource -> loadDescriptor( applicationKey, resource ) )
            .build();
    }

    private StyleDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        return YmlStyleDescriptorParser.parse( resource.readString(), key )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .build();
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
