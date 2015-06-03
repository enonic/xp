package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

abstract class AbstractGetPageDescriptorCommand<T extends AbstractGetPageDescriptorCommand>
{
    private InlineMixinsToFormItemsTransformer inlineMixinsTransformer;

    protected PageDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final PageDescriptor.Builder builder = PageDescriptor.create();

        try
        {
            parseXml( resourceKey.getModule(), builder, descriptorXml );
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load page descriptor [" + resource.getUrl() + "]: " + e.getMessage() );
        }

        builder.key( key );

        final PageDescriptor pageDescriptor = builder.build();

        return PageDescriptor.copyOf( pageDescriptor ).
            config( inlineMixinsTransformer.transformForm( pageDescriptor.getConfig() ) ).
            build();
    }

    private void parseXml( final ModuleKey moduleKey, final PageDescriptor.Builder builder, final String xml )
    {
        final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
        parser.builder( builder );
        parser.currentModule( moduleKey );
        parser.source( xml );
        parser.parse();
    }

    @SuppressWarnings("unchecked")
    public final T mixinService( final MixinService mixinService )
    {
        this.inlineMixinsTransformer = new InlineMixinsToFormItemsTransformer( mixinService );
        return (T) this;
    }
}
