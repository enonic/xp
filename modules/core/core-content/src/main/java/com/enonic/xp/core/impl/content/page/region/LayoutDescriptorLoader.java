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
import com.enonic.xp.schema.content.CmsFormFragmentService;

@Component(immediate = true)
public class LayoutDescriptorLoader
    implements DescriptorLoader<LayoutDescriptor>
{
    private static final String PATH = "/cms/layouts";

    private final ResourceService resourceService;

    private final CmsFormFragmentService formFragmentService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public LayoutDescriptorLoader( @Reference final ResourceService resourceService,
                                   @Reference final CmsFormFragmentService formFragmentService )
    {
        this.resourceService = resourceService;
        this.formFragmentService = formFragmentService;
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
        final String basePath = PATH + "/" + key.getName() + "/" + key.getName();
        final ResourceKey yamlKey = ResourceKey.from( key.getApplicationKey(), basePath + ".yaml" );
        if ( resourceService.getResource( yamlKey ).exists() )
        {
            return yamlKey;
        }
        return ResourceKey.from( key.getApplicationKey(), basePath + ".yml" );
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
            .title( key.getName() )
            .config( Form.empty() )
            .regions( RegionDescriptors.create().build() )
            .build();
    }

    @Override
    public LayoutDescriptor postProcess( final LayoutDescriptor descriptor )
    {
        return LayoutDescriptor.copyOf( descriptor ).config( this.formFragmentService.inlineFormItems( descriptor.getConfig() ) ).build();
    }
}
