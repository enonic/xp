package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.content.page.image.ImageDescriptorKey
import com.enonic.wem.api.module.*
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.core.config.SystemConfig
import com.enonic.wem.core.resource.ResourceServiceImpl
import com.google.common.base.Charsets
import com.google.common.io.ByteSource
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory

abstract class AbstractImageDescriptorServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder temporaryFolder = new TemporaryFolder()

    def ImageDescriptorServiceImpl service

    def setup()
    {
        def config = Mock( SystemConfig.class )
        config.getModulesDir() >> this.temporaryFolder.getRoot().toPath()

        this.service = new ImageDescriptorServiceImpl()
        this.service.moduleService = Mock( ModuleService.class )
        this.service.resourceService = new ResourceServiceImpl( config )
    }

    def ImageDescriptorKey[] createImageDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = ImageDescriptorKey.from( key )
            def descriptorXml = "<image-component><display-name>" + descriptorKey.getName().toString() +
                "</display-name></image-component>";

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

    def Module createModule( final String moduleKey )
    {
        def descriptorName = ModuleKey.from( moduleKey ).getName().toString() + "-image-descr";
        final ModuleFileEntry componentDir = newModuleDirectory( "component" ).
            addEntry( newModuleDirectory( descriptorName ).addFile( "image.xml", ByteSource.wrap( "xml".getBytes() ) ) ).
            build();
        def module = Module.newModule().
            moduleKey( ModuleKey.from( moduleKey ) ).
            displayName( moduleKey.toUpperCase() ).
            addFileEntry( componentDir ).
            build();

        this.service.moduleService.getModule( ModuleKey.from( moduleKey ) ) >> module
        return module;
    }

    def createModules( final String... moduleKeys )
    {
        def moduleList = []
        moduleKeys.each {
            def module = createModule( it )
            moduleList.add( module )
        }
        def modules = Modules.from( moduleList )
        this.service.moduleService.getAllModules() >> modules
        return modules;
    }
}
