package com.enonic.xp.core.impl.content.page;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.form.Form;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

@Component(immediate = true)
public class PageDescriptorLoader
    implements DescriptorLoader<PageDescriptor>
{
    private static final String PATH = "/site/pages";

    private final DescriptorKeyLocator descriptorKeyLocator;

    private final MixinService mixinService;

    @Activate
    public PageDescriptorLoader( @Reference final ResourceService resourceService, @Reference final MixinService mixinService )
    {
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, true );
        this.mixinService = mixinService;
    }

    @Override
    public Class<PageDescriptor> getType()
    {
        return PageDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    public PageDescriptor load( final DescriptorKey key, final Resource resource )
    {
        final PageDescriptor.Builder builder = PageDescriptor.create();
        parseXml( resource, builder );
        builder.key( key );
        return builder.build();
    }

    @Override
    public PageDescriptor createDefault( final DescriptorKey key )
    {
        return PageDescriptor.create()
            .key( key )
            .displayName( key.getName() )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();
    }

    @Override
    public PageDescriptor postProcess( final PageDescriptor descriptor )
    {
        return PageDescriptor.copyOf( descriptor ).config( this.mixinService.inlineFormItems( descriptor.getConfig() ) ).build();
    }

    private void parseXml( final Resource resource, final PageDescriptor.Builder builder )
    {
        try
        {
            final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );

            final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
            builder.modifiedTime( modifiedTime );

            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load page descriptor [" + resource.getKey() + "]: " + e.getMessage() );
        }
    }
}
