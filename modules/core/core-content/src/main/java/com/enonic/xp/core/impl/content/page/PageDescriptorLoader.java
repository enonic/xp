package com.enonic.xp.core.impl.content.page;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.parser.YmlPageDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.CmsFormFragmentService;

@Component(immediate = true)
public class PageDescriptorLoader
    implements DescriptorLoader<PageDescriptor>
{
    private static final String PATH = "/cms/pages";

    private final DescriptorKeyLocator descriptorKeyLocator;

    private final CmsFormFragmentService formFragmentService;

    @Activate
    public PageDescriptorLoader( @Reference final ResourceService resourceService,
                                 @Reference final CmsFormFragmentService formFragmentService )
    {
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, true );
        this.formFragmentService = formFragmentService;
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
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }

    @Override
    public PageDescriptor load( final DescriptorKey key, final Resource resource )
    {
        return YmlPageDescriptorParser.parse( resource.readString(), key.getApplicationKey() )
            .key( key )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .build();
    }

    @Override
    public PageDescriptor createDefault( final DescriptorKey key )
    {
        return PageDescriptor.create()
            .key( key )
            .displayName( key.getName() )
            .config( Form.empty() )
            .regions( RegionDescriptors.create().build() )
            .modifiedTime( Instant.now() )
            .build();
    }

    @Override
    public PageDescriptor postProcess( final PageDescriptor descriptor )
    {
        return PageDescriptor.copyOf( descriptor ).config( this.formFragmentService.inlineFormItems( descriptor.getConfig() ) ).build();
    }
}
