package com.enonic.xp.core.impl.idprovider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlIdProviderDescriptorParser;

@Component(immediate = true)
public final class IdProviderDescriptorServiceImpl
    implements IdProviderDescriptorService
{
    private ResourceService resourceService;

    @Override
    public IdProviderDescriptor getDescriptor( final ApplicationKey key )
    {
        final ResourceProcessor<ApplicationKey, IdProviderDescriptor> processor = newProcessor( key );
        return this.resourceService.processResource( processor );
    }

    private ResourceProcessor<ApplicationKey, IdProviderDescriptor> newProcessor( final ApplicationKey key )
    {
        return new ResourceProcessor.Builder<ApplicationKey, IdProviderDescriptor>().
            key( key ).
            segment( "authDescriptor" ).
            keyTranslator( IdProviderDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private IdProviderDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        final IdProviderDescriptor.Builder builder = IdProviderDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );

        return builder.build();
    }

    private void parseXml( final Resource resource, final IdProviderDescriptor.Builder builder )
    {
        try
        {
            final XmlIdProviderDescriptorParser parser = new XmlIdProviderDescriptorParser();
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
