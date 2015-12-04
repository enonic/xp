package com.enonic.xp.core.impl.content.page.region;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.DescriptorKeyLocator;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlLayoutDescriptorParser;

@Component
public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private final static String PATH = "/site/layouts";

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        final ResourceProcessor<DescriptorKey, LayoutDescriptor> processor = newProcessor( key );
        final LayoutDescriptor descriptor = this.resourceService.processResource( processor );
        if ( descriptor != null )
        {
            return descriptor;
        }

        return null;
    }

    private ResourceProcessor<DescriptorKey, LayoutDescriptor> newProcessor( final DescriptorKey key )
    {
        return new ResourceProcessor.Builder<DescriptorKey, LayoutDescriptor>().
            key( key ).
            segment( "layoutDescriptor" ).
            keyTranslator( LayoutDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    @Override
    public LayoutDescriptors getByApplication( final ApplicationKey key )
    {
        final List<LayoutDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( key ) )
        {
            final LayoutDescriptor descriptor = getByKey( descriptorKey );
            if ( descriptor != null )
            {
                list.add( descriptor );
            }

        }

        return LayoutDescriptors.from( list );
    }

    @Override
    public LayoutDescriptors getByApplications( final ApplicationKeys keys )
    {
        final List<LayoutDescriptor> list = new ArrayList<>();
        for ( final ApplicationKey key : keys )
        {
            list.addAll( getByApplication( key ).getList() );
        }

        return LayoutDescriptors.from( list );
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

    private LayoutDescriptor loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        final LayoutDescriptor.Builder builder = LayoutDescriptor.create();
        parseXml( resource, builder );
        builder.name( key.getName() ).key( key );
        final LayoutDescriptor layoutDescriptor = builder.build();

        return LayoutDescriptor.copyOf( layoutDescriptor ).
            config( this.mixinService.inlineFormItems( layoutDescriptor.getConfig() ) ).
            build();
    }

    private void parseXml( final Resource resource, final LayoutDescriptor.Builder builder )
    {
        try
        {
            final XmlLayoutDescriptorParser parser = new XmlLayoutDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load layout descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    private List<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, PATH, false ).findKeys( key );
    }
}
