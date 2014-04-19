package com.enonic.wem.core.content.page

import com.enonic.wem.api.content.page.PageDescriptorKey

abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def PageDescriptorServiceImpl service

    def setup()
    {
        this.service = new PageDescriptorServiceImpl()
        this.service.resourceService = this.resourceService
    }

    def PageDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = PageDescriptorKey.from( key )
            def descriptorXml = "<page-component><display-name>" + descriptorKey.getName().toString() + "</display-name></page-component>";

            createFile( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }
}
