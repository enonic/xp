package com.enonic.xp.core.impl.content.page.region;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.DescriptorKeyLocator;
import com.enonic.xp.core.impl.content.page.OptionalDescriptorKeyLocator;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService, ApplicationInvalidator
{
    private final static String PATH = "/site/parts";

    private MixinService mixinService;

    private ResourceService resourceService;

    private final ConcurrentMap<DescriptorKey, PartDescriptor> cache;

    public PartDescriptorServiceImpl()
    {
        this.cache = Maps.newConcurrentMap();
    }

    @Override
    public PartDescriptor getByKey( final DescriptorKey key )
    {
        PartDescriptor partDescriptor = this.cache.computeIfAbsent( key, this::loadDescriptor );
        if ( partDescriptor == null )
        {
            partDescriptor = createDefaultDescriptor( key );
        }
        return partDescriptor;
    }

    @Override
    public PartDescriptors getByApplication( final ApplicationKey key )
    {
        final List<PartDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( key ) )
        {
            final PartDescriptor descriptor = getByKey( descriptorKey );
            if ( descriptor != null )
            {
                list.add( descriptor );
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

    private PartDescriptor loadDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PartDescriptor.toResourceKey( key );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final PartDescriptor.Builder builder = PartDescriptor.create();
        parseXml( resource, builder );
        builder.name( key.getName() ).key( key );
        final PartDescriptor partDescriptor = builder.build();

        return PartDescriptor.copyOf( partDescriptor ).
            config( mixinService.inlineFormItems( partDescriptor.getConfig() ) ).
            build();
    }

    private PartDescriptor createDefaultDescriptor( final DescriptorKey key )
    {
        return PartDescriptor.
            create().
            key( key ).
            name( key.getName() ).
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

    private List<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new OptionalDescriptorKeyLocator( this.resourceService, PATH ).findKeys( key );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.cache.clear();
    }
}
