package com.enonic.wem.core.content.page

import com.enonic.wem.api.content.page.DescriptorKey
import com.enonic.wem.api.content.page.PageDescriptor

abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def PageDescriptorServiceImpl service

    def setup()
    {
        this.service = new PageDescriptorServiceImpl()
    }

    def DescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = DescriptorKey.from( key )
            def descriptorXml = "<page-component><display-name>" + descriptorKey.getName().toString() + "</display-name></page-component>";

            createFile( PageDescriptor.toResourceKey(descriptorKey), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }
}
