package com.enonic.wem.core.module

import com.enonic.wem.api.module.Module
import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleNotFoundException
import com.enonic.wem.api.module.ModuleVersion
import com.enonic.wem.core.config.SystemConfig
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class ModuleServiceImpl_deleteModuleTest
    extends Specification
{
    @Rule
    def TemporaryFolder folder = new TemporaryFolder()

    def ModuleServiceImpl service

    def Path modulesDir

    def setup()
    {
        this.service = new ModuleServiceImpl()

        def systemConfig = Mock( SystemConfig.class )
        this.service.moduleExporter = Mock( ModuleExporter.class )
        this.service.moduleResourcePathResolver = new ModuleResourcePathResolverImpl( systemConfig )

        def tempDir = this.folder.newFolder().toPath()
        this.modulesDir = tempDir.resolve( "modules" )
        Files.createDirectory( this.modulesDir )

        systemConfig.getModulesDir() >> this.modulesDir
    }

    def "delete existing module"()
    {
        given:
        def moduleDir = this.modulesDir.resolve( "foomodule-1.2.0" )
        Files.createDirectories( moduleDir.resolve( "config" ) );

        and:
        def fooModule = createModule();
        this.service.moduleExporter.importFromDirectory( moduleDir ) >> fooModule

        when:
        def result = this.service.deleteModule( ModuleKey.from( "foomodule-1.2.0" ) );

        then:
        result != null
    }

    def "delete non-existing module"()
    {
        when:
        this.service.deleteModule( ModuleKey.from( "foomodule-1.0.0" ) );

        then:
        thrown( ModuleNotFoundException )
    }

    private static Module.Builder createModule()
    {
        return Module.newModule().
            moduleKey( ModuleKey.from( "foomodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) )
    }
}
