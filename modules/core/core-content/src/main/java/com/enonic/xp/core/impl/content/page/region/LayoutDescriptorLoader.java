package com.enonic.xp.core.impl.content.page.region;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlLayoutDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.form.Form;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.formfragment.FormFragmentService;

@Component(immediate = true)
public class LayoutDescriptorLoader
    implements DescriptorLoader<LayoutDescriptor>
{
    private static final String PATH = "/cms/layouts";

    private final FormFragmentService mixinService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public LayoutDescriptorLoader( @Reference final ResourceService resourceService, @Reference final FormFragmentService mixinService )
    {
        this.mixinService = mixinService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, false );
    }

    @Override
    public Class<LayoutDescriptor> getType()
    {
        return LayoutDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }

    @Override
    public LayoutDescriptor load( final DescriptorKey key, final Resource resource )
        throws Exception
    {
        return YmlLayoutDescriptorParser.parse( resource.readString(), key.getApplicationKey() )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .key( key )
            .build();
    }

    @Override
    public LayoutDescriptor createDefault( final DescriptorKey key )
    {
        return LayoutDescriptor.create()
            .key( key )
            .displayName( key.getName() )
            .config( Form.empty() )
            .regions( RegionDescriptors.create().build() )
            .build();
    }

    @Override
    public LayoutDescriptor postProcess( final LayoutDescriptor descriptor )
    {
        return LayoutDescriptor.copyOf( descriptor ).config( this.mixinService.inlineFormItems( descriptor.getConfig() ) ).build();
    }
}
