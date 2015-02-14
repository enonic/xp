package com.enonic.xp.core.impl.content.page;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.core.resource.Resource;
import com.enonic.xp.core.resource.ResourceKey;
import com.enonic.xp.core.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.xp.core.xml.model.XmlPageDescriptor;
import com.enonic.xp.core.xml.serializer.XmlSerializers;

abstract class AbstractGetPageDescriptorCommand
{
    protected PageDescriptor getDescriptor( final DescriptorKey key )
    {
        final ResourceKey resourceKey = PageDescriptor.toResourceKey( key );
        final Resource resource = Resource.from( resourceKey );

        final String descriptorXml = resource.readString();
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();

        final XmlPageDescriptor xmlObject = XmlSerializers.pageDescriptor().parse( descriptorXml );
        new XmlPageDescriptorMapper( resourceKey.getModule() ).fromXml( xmlObject, builder );

        builder.key( key );

        return builder.build();
    }
}
