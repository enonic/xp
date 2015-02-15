package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.xp.xml.model.XmlPageDescriptor;
import com.enonic.xp.xml.serializer.XmlSerializers;

abstract class AbstractGetPageDescriptorCommand<T extends AbstractGetPageDescriptorCommand>
{
    private InlineMixinsToFormItemsTransformer inlineMixinsTransformer;

    protected PageDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final PageDescriptor.Builder builder = PageDescriptor.create();

        final XmlPageDescriptor xmlObject = XmlSerializers.pageDescriptor().parse( descriptorXml );
        new XmlPageDescriptorMapper( resourceKey.getModule() ).fromXml( xmlObject, builder );

        builder.key( key );

        final PageDescriptor pageDescriptor = builder.build();

        return PageDescriptor.copyOf( pageDescriptor ).
            config( inlineMixinsTransformer.transformForm( pageDescriptor.getConfig() ) ).
            build();
    }

    @SuppressWarnings("unchecked")
    public final T mixinService( final MixinService mixinService )
    {
        this.inlineMixinsTransformer = new InlineMixinsToFormItemsTransformer( mixinService );
        return (T) this;
    }
}
