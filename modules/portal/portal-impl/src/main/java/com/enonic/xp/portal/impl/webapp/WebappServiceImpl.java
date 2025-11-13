package com.enonic.xp.portal.impl.webapp;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

@Component
public class WebappServiceImpl
    implements WebappService
{
    private final ResourceService resourceService;

    @Activate
    public WebappServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public WebappDescriptor getDescriptor( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, WebappDescriptor> processor = newProcessor( applicationKey );
        return resourceService.processResource( processor );
    }

    private ResourceProcessor<ApplicationKey, WebappDescriptor> newProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, WebappDescriptor>().key( applicationKey )
            .segment( "webappDescriptor" )
            .keyTranslator( WebappDescriptor::toResourceKey )
            .processor( this::loadDescriptor )
            .build();
    }

    private WebappDescriptor loadDescriptor( final Resource resource )
    {
        return YmlWebappDescriptorParser.parse( resource.readString(), resource.getKey().getApplicationKey() ).build();
    }
}
