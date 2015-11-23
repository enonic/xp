package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

abstract class AbstractGetPageDescriptorCommand<T extends AbstractGetPageDescriptorCommand>
{
    private MixinService mixinService;

    protected ResourceService resourceService;

    protected PageDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = resourceService.getResource( resourceKey );

        final PageDescriptor.Builder builder = PageDescriptor.create();

        if ( resource.exists() )
        {
            final String descriptorXml = resource.readString();
            try
            {
                parseXml( resourceKey.getApplicationKey(), builder, descriptorXml );
            }
            catch ( final Exception e )
            {
                throw new XmlException( e, "Could not load page descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
            }
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

    private void parseXml( final ApplicationKey applicationKey, final PageDescriptor.Builder builder, final String xml )
    {
        final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
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
}
