package com.enonic.xp.core.impl.style;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlStyleDescriptorParser;

@Component(immediate = true)
public class StyleDescriptorServiceImpl
    implements StyleDescriptorService
{
    private final static Logger LOG = LoggerFactory.getLogger( StyleDescriptorServiceImpl.class );

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
        final ImmutableList.Builder<StyleDescriptor> list = new ImmutableList.Builder<>();
        for ( final ApplicationKey key : applicationKeys )
        {
            final StyleDescriptor desc = getByApplication( key );
            if ( desc != null )
            {
                list.add( desc );
            }
        }
        return StyleDescriptors.from( list.build() );
    }

    @Override
    public StyleDescriptors getAll()
    {
        final ImmutableList.Builder<StyleDescriptor> list = new ImmutableList.Builder<>();
        this.applicationService.getInstalledApplications().stream().
            map( ( app ) -> getByApplication( app.getKey() ) ).
            filter( Objects::nonNull ).
            forEach( list::add );
        return StyleDescriptors.from( list.build() );
    }

    private ResourceProcessor<ApplicationKey, StyleDescriptor> newProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, StyleDescriptor>().
            key( applicationKey ).
            segment( "styleDescriptor" ).
            keyTranslator( StyleDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( applicationKey, resource ) ).
            build();
    }

    private StyleDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        final StyleDescriptor.Builder builder = StyleDescriptor.create().application( key );
        try
        {
            parseXml( resource, builder );
        }
        catch ( XmlException e )
        {
            LOG.warn( "Could not load style descriptor: " + resource.getKey().toString(), e );
            return null;
        }
        return builder.build();
    }

    private void parseXml( final Resource resource, final StyleDescriptor.Builder builder )
    {
        try
        {
            new XmlStyleDescriptorParser().
                styleDescriptorBuilder( builder ).
                currentApplication( resource.getKey().getApplicationKey() ).
                source( resource.readString() ).
                parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load style descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
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
