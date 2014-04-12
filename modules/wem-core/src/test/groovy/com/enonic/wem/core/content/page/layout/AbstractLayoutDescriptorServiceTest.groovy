package com.enonic.wem.core.content.page.layout

import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey
import com.enonic.wem.api.module.*
import com.enonic.wem.api.resource.Resource
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.core.config.SystemConfig
import com.google.common.base.Charsets
import com.google.common.io.ByteSource
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory

abstract class AbstractLayoutDescriptorServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder temporaryFolder = new TemporaryFolder()

    def LayoutDescriptorServiceImpl service

    def setup()
    {
        def config = Mock( SystemConfig.class )
        config.getModulesDir() >> this.temporaryFolder.getRoot().toPath()

        this.service = new LayoutDescriptorServiceImpl()
        this.service.moduleService = Mock( ModuleService.class )
    }

    def LayoutDescriptorKey[] createDescriptor( final String... keys )
    {
        def resources = [];
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = LayoutDescriptorKey.from( key )
            def descriptorXml = "<layout-component><display-name>" + descriptorKey.getName().toString() +
                "</display-name></layout-component>";
            def resource = Resource.newResource().name( "layout.xml" ).stringValue( descriptorXml ).build();
            resources.add( resource );
            descriptorKeys.add( descriptorKey );
        }
        this.service.moduleService.getResource( _ ) >>> resources;
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
        def descriptorName = ModuleKey.from( moduleKey ).getName().toString() + "-layout-descr";
        final ModuleFileEntry componentDir = newModuleDirectory( "component" ).
            addEntry( newModuleDirectory( descriptorName ).addFile( "layout.xml", ByteSource.wrap( "xml".getBytes() ) ) ).
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
