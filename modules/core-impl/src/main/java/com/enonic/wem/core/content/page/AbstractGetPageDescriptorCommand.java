package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.xml.mapper.XmlPageDescriptorMapper;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.serializer.XmlSerializers;

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
