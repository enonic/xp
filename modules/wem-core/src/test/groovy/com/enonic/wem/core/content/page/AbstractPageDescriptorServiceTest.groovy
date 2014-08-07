package com.enonic.wem.core.content.page

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.resource.ResourceUrlTestHelper

abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def PageDescriptorServiceImpl service

    def setup()
    {
        this.service = new PageDescriptorServiceImpl()
        this.service.resourceService = this.resourceService;

        ResourceUrlTestHelper.mockModuleScheme( super.modulesDir );
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
