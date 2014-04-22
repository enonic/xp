package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.content.page.image.ImageDescriptorKey
import com.enonic.wem.core.content.page.AbstractDescriptorServiceTest

abstract class AbstractImageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def ImageDescriptorServiceImpl service

    def setup()
    {
        this.service = new ImageDescriptorServiceImpl()
        this.service.moduleService = this.moduleService
        this.service.resourceService = this.resourceService
    }

    def ImageDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = ImageDescriptorKey.from( key )
            def descriptorXml = "<image-component><display-name>" + descriptorKey.getName().toString() +
                "</display-name></image-component>";

            createFile( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }
}
