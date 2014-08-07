package com.enonic.wem.core.content.page

import com.enonic.wem.api.module.*
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.api.resource.ResourceService
import com.enonic.wem.core.module.ModuleBuilder
import com.enonic.wem.core.resource.ResourceServiceImpl
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractDescriptorServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder temporaryFolder = new TemporaryFolder()

    def File modulesDir

    def ResourceService resourceService

    def ModuleService moduleService

    def setup()
    {
        this.modulesDir = this.temporaryFolder.newFolder( "modules" )

        this.resourceService = new ResourceServiceImpl()
        this.moduleService = Mock( ModuleService.class )
    }

    def createFile( final ResourceKey key, final String content )
    {
        def path = key.module.toString() + key.path
        def file = new File( this.modulesDir, path )
        file.parentFile.mkdirs()
        file.write( content )
    }

    def Module createModule( final String moduleKey )
    {
        def key = ModuleKey.from( moduleKey )
        def module = new ModuleBuilder().
            moduleKey( key ).
            displayName( moduleKey.toUpperCase() ).
            moduleDir( new File( this.modulesDir, key.toString() ) ).
            build();

        this.moduleService.getModule( ModuleKey.from( moduleKey ) ) >> module
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
        this.moduleService.getAllModules() >> modules
        return modules;
    }
}
