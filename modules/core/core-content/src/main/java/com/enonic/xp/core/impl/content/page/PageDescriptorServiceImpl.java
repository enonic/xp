package com.enonic.xp.core.impl.content.page;

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
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

@Component(immediate = true)
public final class PageDescriptorServiceImpl
    implements PageDescriptorService, ApplicationInvalidator
{
    private final static String PATH = "/site/pages";

    private MixinService mixinService;

    private ResourceService resourceService;

    private final ConcurrentMap<DescriptorKey, PageDescriptor> cache;

    public PageDescriptorServiceImpl()
    {
        this.cache = Maps.newConcurrentMap();
    }

    @Override
    public PageDescriptor getByKey( final DescriptorKey key )
    {
        PageDescriptor pageDescriptor = this.cache.computeIfAbsent( key, this::loadDescriptor );
        if ( pageDescriptor == null )
        {
            pageDescriptor = createDefaultDescriptor( key );
        }
        return pageDescriptor;
    }

    @Override
    public PageDescriptors getByApplication( final ApplicationKey key )
    {
        final List<PageDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( key ) )
        {
            final PageDescriptor descriptor = getByKey( descriptorKey );
            if ( descriptor != null )
            {
                list.add( descriptor );
            }

        }

        return PageDescriptors.from( list );
    }

    @Override
    public PageDescriptors getByApplications( final ApplicationKeys keys )
    {
        final List<PageDescriptor> list = new ArrayList<>();
        for ( final ApplicationKey key : keys )
        {
            list.addAll( getByApplication( key ).getList() );
        }

        return PageDescriptors.from( list );
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

    private void parseXml( final Resource resource, final PageDescriptor.Builder builder )
    {
        try
        {
            final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load page descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }
    }

    private List<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new OptionalDescriptorKeyLocator( this.resourceService, PATH ).findKeys( key );
    }

    private final PageDescriptor loadDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final PageDescriptor.Builder builder = PageDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );

        final PageDescriptor pageDescriptor = builder.build();

        return PageDescriptor.copyOf( pageDescriptor ).
            config( mixinService.inlineFormItems( pageDescriptor.getConfig() ) ).
            build();
    }

    private final PageDescriptor createDefaultDescriptor( final DescriptorKey key )
    {
        return PageDescriptor.
            create().
            key( key ).
            displayName( key.getName() ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            build();
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.cache.clear();
    }
}
