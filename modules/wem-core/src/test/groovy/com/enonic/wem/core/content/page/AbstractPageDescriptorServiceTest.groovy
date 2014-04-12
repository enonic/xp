package com.enonic.wem.core.content.page

import com.enonic.wem.api.content.page.PageDescriptorKey
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.core.config.SystemConfig
import com.enonic.wem.core.resource.ResourceServiceImpl
import com.google.common.base.Charsets
import com.google.common.io.ByteSource
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractPageDescriptorServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder temporaryFolder = new TemporaryFolder()

    def PageDescriptorServiceImpl service

    def setup()
    {
        def config = Mock( SystemConfig.class )
        config.getModulesDir() >> this.temporaryFolder.getRoot().toPath()

        this.service = new PageDescriptorServiceImpl()
        this.service.resourceService = new ResourceServiceImpl( config )
    }

    def PageDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = PageDescriptorKey.from( key )
            def descriptorXml = "<page-component><display-name>" + descriptorKey.getName().toString() + "</display-name></page-component>";

            createResouce( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }

    def void createResouce( final ResourceKey key, final String content )
    {
        def file = new File( this.temporaryFolder.getRoot(), key.getModule().toString() + key.getPath() )
        file.getParentFile().mkdirs()
        ByteSource.wrap( content.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) )
    }
}
