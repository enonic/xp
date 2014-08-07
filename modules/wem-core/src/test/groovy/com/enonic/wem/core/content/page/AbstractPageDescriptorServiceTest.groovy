package com.enonic.wem.core.content.page

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.api.resource.ResourceUrlResolver

abstract class AbstractPageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    def PageDescriptorServiceImpl service

    def setup()
    {
        this.service = new PageDescriptorServiceImpl()
        this.service.resourceService = this.resourceService;

        def modDir = super.modulesDir;
        new ResourceUrlResolver() {
            protected URL doResolve( final ResourceKey key )
                throws Exception
            {
                return new URL( "file:" + modDir.getPath() + "/" + key.getModule().toString() + key.getPath() );
            }
        }
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
