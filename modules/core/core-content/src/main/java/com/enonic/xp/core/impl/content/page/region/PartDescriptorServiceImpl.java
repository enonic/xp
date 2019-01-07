package com.enonic.xp.core.impl.content.page.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    private final static Logger LOG = LoggerFactory.getLogger( PartDescriptorServiceImpl.class );

    private final static String PATH = "/site/parts";

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public PartDescriptor getByKey( final DescriptorKey key )
    {
        final ResourceProcessor<DescriptorKey, PartDescriptor> processor = newProcessor( key );
        final PartDescriptor descriptor = this.resourceService.processResource( processor );

        if ( descriptor == null )
        {
            return createDefaultDescriptor( key );
        }

        return PartDescriptor.copyOf( descriptor ).
            config( this.mixinService.inlineFormItems( descriptor.getConfig() ) ).
            build();
    }

    private ResourceProcessor<DescriptorKey, PartDescriptor> newProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, PartDescriptor>().
            key( key ).
            segment( "partDescriptor" ).
            keyTranslator( PartDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    @Override
    public PartDescriptors getByApplication( final ApplicationKey key )
    {
        final List<PartDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( key ) )
        {
            try
            {
                final PartDescriptor descriptor = getByKey( descriptorKey );
                if ( descriptor != null )
                {
                    list.add( descriptor );
                }
            }
            catch ( IllegalArgumentException e )
            {
                LOG.error( "Error in part descriptor: " + descriptorKey.toString(), e );
            }
        }

        return PartDescriptors.from( list );
    }

    @Override
    public PartDescriptors getByApplications( final ApplicationKeys keys )
    {
        final List<PartDescriptor> list = new ArrayList<>();
        for ( final ApplicationKey key : keys )
        {
            list.addAll( getByApplication( key ).getList() );
        }

        return PartDescriptors.from( list );
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    private PartDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final PartDescriptor.Builder builder = PartDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );
        return builder.build();
    }

    private PartDescriptor createDefaultDescriptor( final DescriptorKey key )
    {
        return PartDescriptor.
            create().
            key( key ).
            displayName( key.getName() ).
            config( Form.create().build() ).
            build();
    }

    private void parseXml( final Resource resource, final PartDescriptor.Builder builder )
    {
        try
        {
            final XmlPartDescriptorParser parser = new XmlPartDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load part descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    private Set<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, PATH, true ).findKeys( key );
    }
}
