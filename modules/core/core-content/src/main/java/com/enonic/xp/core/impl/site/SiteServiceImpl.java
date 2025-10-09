package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlSiteDescriptorParser;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private final ResourceService resourceService;

    @Activate
    public SiteServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public SiteDescriptor getDescriptor( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, SiteDescriptor> processor = newDescriptorProcessor( applicationKey );
        final SiteDescriptor descriptor = this.resourceService.processResource( processor );

        if ( descriptor == null )
        {
            return null;
        }

        return SiteDescriptor.copyOf( descriptor ).applicationKey( applicationKey ).build();
    }

    private ResourceProcessor<ApplicationKey, SiteDescriptor> newDescriptorProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, SiteDescriptor>().key( applicationKey )
            .segment( "siteDescriptor" )
            .keyTranslator( SiteDescriptor::toResourceKey )
            .processor( this::loadDescriptor )
            .build();
    }

    private SiteDescriptor loadDescriptor( final Resource resource )
    {
        return YmlSiteDescriptorParser.parse( resource.readString(), resource.getKey().getApplicationKey() )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .build();
    }
}
