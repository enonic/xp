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
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlWebappDescriptorParser;

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
        final WebappDescriptor.Builder builder = WebappDescriptor.create();
        builder.applicationKey( resource.getKey().getApplicationKey() );

        parseXml( resource, builder );

        return builder.build();
    }

    private void parseXml( final Resource resource, final WebappDescriptor.Builder builder )
    {
        try
        {
            new XmlWebappDescriptorParser().descriptorBuilder( builder )
                .currentApplication( resource.getKey().getApplicationKey() )
                .source( resource.readString() )
                .parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, String.format( "Could not load webapp descriptor [%s]: %s", resource.getKey(), e.getMessage() ) );
        }
    }
}
