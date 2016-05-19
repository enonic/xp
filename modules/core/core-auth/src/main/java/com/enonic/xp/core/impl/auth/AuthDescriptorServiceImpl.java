package com.enonic.xp.core.impl.auth;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlAuthDescriptorParser;

@Component(immediate = true)
public final class AuthDescriptorServiceImpl
    implements AuthDescriptorService
{
    private static final ApplicationKey DEFAULT_AUTH_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.system" );

    private ResourceService resourceService;

    @Override
    public AuthDescriptor getDescriptor( final ApplicationKey key )
    {
        final ResourceProcessor<ApplicationKey, AuthDescriptor> processor = newProcessor( key );
        return this.resourceService.processResource( processor );
    }

    @Override
    public AuthDescriptor getDefaultDescriptor()
    {
        return getDescriptor( DEFAULT_AUTH_APPLICATION_KEY );
    }

    private ResourceProcessor<ApplicationKey, AuthDescriptor> newProcessor( final ApplicationKey key )
    {
        return new ResourceProcessor.Builder<ApplicationKey, AuthDescriptor>().
            key( key ).
            segment( "authDescriptor" ).
            keyTranslator( AuthDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private AuthDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        final AuthDescriptor.Builder builder = AuthDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );

        return builder.build();
    }

    private void parseXml( final Resource resource, final AuthDescriptor.Builder builder )
    {
        try
        {
            final XmlAuthDescriptorParser parser = new XmlAuthDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load auth descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
