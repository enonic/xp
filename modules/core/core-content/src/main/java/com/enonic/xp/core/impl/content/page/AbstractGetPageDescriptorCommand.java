package com.enonic.xp.core.impl.content.page;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.app.Application;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

abstract class AbstractGetPageDescriptorCommand<T extends AbstractGetPageDescriptorCommand>
{
    private final static String PATH = "/site/pages";

    private MixinService mixinService;

    protected ResourceService resourceService;

    protected final PageDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = resourceService.getResource( resourceKey );

        final PageDescriptor.Builder builder = PageDescriptor.create();

        if ( resource.exists() )
        {
            parseXml( resource, builder );
        }
        else
        {
            builder.displayName( key.getName() ).
                config( Form.create().build() ).
                regions( RegionDescriptors.create().build() );
        }

        builder.key( key );

        final PageDescriptor pageDescriptor = builder.build();

        return PageDescriptor.copyOf( pageDescriptor ).
            config( mixinService.inlineFormItems( pageDescriptor.getConfig() ) ).
            build();
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

    @SuppressWarnings("unchecked")
    public final T mixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        return (T) this;
    }

    public final T resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return (T) this;
    }

    protected final PageDescriptors getDescriptorsFromApplication( final Application application )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        final ResourceKeys resourceKeys = this.resourceService.findFolders( application.getKey(), PATH );

        for ( final ResourceKey resourceKey : resourceKeys )
        {
            final String descriptorName = resourceKey.getName();
            final DescriptorKey key = DescriptorKey.from( application.getKey(), descriptorName );
            final PageDescriptor pageDescriptor = getDescriptor( key );
            if ( pageDescriptor != null )
            {
                pageDescriptors.add( pageDescriptor );
            }

        }

        return PageDescriptors.from( pageDescriptors );
    }
}
