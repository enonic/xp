package com.enonic.wem.core.content.page

import com.enonic.wem.api.module.*
import com.enonic.wem.api.resource.ResourceKey
import com.enonic.wem.api.resource.ResourceUrlTestHelper
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractDescriptorServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder temporaryFolder = new TemporaryFolder()

    def File modulesDir

    def ModuleService moduleService

    def setup()
    {
        this.modulesDir = this.temporaryFolder.newFolder( "modules" )
        ResourceUrlTestHelper.mockModuleScheme().modulesDir( this.modulesDir )
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
        def module = null;

        /*new ModuleBuilder().
            moduleKey( key ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            displayName( moduleKey.toUpperCase() ).
            build();*/

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
