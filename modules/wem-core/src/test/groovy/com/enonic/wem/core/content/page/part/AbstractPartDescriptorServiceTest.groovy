package com.enonic.wem.core.content.page.part

import com.enonic.wem.api.content.page.DescriptorKey
import com.enonic.wem.core.content.page.AbstractDescriptorServiceTest
import com.enonic.wem.core.content.page.region.PartDescriptorServiceImpl

abstract class AbstractPartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def PartDescriptorServiceImpl service

    def setup()
    {
        this.service = new PartDescriptorServiceImpl()
        this.service.moduleService = this.moduleService
        this.service.resourceService = this.resourceService
    }

    def DescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = DescriptorKey.from( key )
            def descriptorXml = "<part-component><display-name>" + descriptorKey.getName().toString() + "</display-name></part-component>";

            createFile( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }
}
