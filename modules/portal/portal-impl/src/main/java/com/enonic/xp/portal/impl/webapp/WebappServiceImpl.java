package com.enonic.xp.portal.impl.webapp;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

@Component
public class WebappServiceImpl
    implements WebappService
{
    private static final String WEBAPP_DESCRIPTOR_PATH_YML = "webapp/webapp.yml";

    private static final String WEBAPP_DESCRIPTOR_PATH_YAML = "webapp/webapp.yaml";

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
            .keyTranslator( this::toResourceKey )
            .processor( this::loadDescriptor )
            .build();
    }

    private WebappDescriptor loadDescriptor( final Resource resource )
    {
        return YmlWebappDescriptorParser.parse( resource.readString(), resource.getKey().getApplicationKey() ).build();
    }

    private ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        final ResourceKey yamlKey = ResourceKey.from( applicationKey, WEBAPP_DESCRIPTOR_PATH_YAML );
        if ( resourceService.getResource( yamlKey ).exists() )
        {
            return yamlKey;
        }
        return ResourceKey.from( applicationKey, WEBAPP_DESCRIPTOR_PATH_YML );
    }
}
