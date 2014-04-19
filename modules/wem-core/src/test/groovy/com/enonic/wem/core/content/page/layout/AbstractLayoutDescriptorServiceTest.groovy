package com.enonic.wem.core.content.page.layout

import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey
import com.enonic.wem.core.content.page.AbstractDescriptorServiceTest

abstract class AbstractLayoutDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def LayoutDescriptorServiceImpl service

    def setup()
    {
        this.service = new LayoutDescriptorServiceImpl()
        this.service.moduleService = this.moduleService
        this.service.resourceService = this.resourceService
    }

    def LayoutDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = LayoutDescriptorKey.from( key )
            def descriptorXml = "<layout-component><display-name>" + descriptorKey.getName().toString() +
                "</display-name></layout-component>";

            createFile( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }
}
