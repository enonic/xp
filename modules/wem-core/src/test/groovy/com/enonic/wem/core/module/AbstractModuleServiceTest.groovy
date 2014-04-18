package com.enonic.wem.core.module

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleVersion
import com.enonic.wem.core.config.SystemConfig
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

abstract class AbstractModuleServiceTest
    extends Specification
{
    @Rule
    def TemporaryFolder folder = new TemporaryFolder()

    def ModuleServiceImpl service

    def Path modulesDir

    def setup()
    {
        this.service = new ModuleServiceImpl()

        this.service.systemConfig = Mock( SystemConfig.class )
        this.service.moduleExporter = Mock( ModuleExporter.class )
        this.service.moduleResourcePathResolver = new ModuleResourcePathResolverImpl( this.service.systemConfig )

        def tempDir = this.folder.newFolder().toPath()
        this.modulesDir = tempDir.resolve( "modules" )
        Files.createDirectory( this.modulesDir )

        this.service.systemConfig.getModulesDir() >> this.modulesDir
    }

    def ModuleBuilder buildModule( final String key )
    {
        return new ModuleBuilder().
            moduleKey( ModuleKey.from( key ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" )
    }

    def ModuleBuilder createModule( final String name )
    {
        def moduleDir = this.modulesDir.resolve( name )
        Files.createDirectories( moduleDir.resolve( "config" ) );
        def fooModule = buildModule( name );
        this.service.moduleExporter.importFromDirectory( moduleDir ) >> fooModule
    }

    def createModules( final String... names )
    {
        names.each {
            createModule( it )
        }
    }

    def createResource( final String moduleName, final String resourcePath, final String data )
    {
        def moduleDir = this.modulesDir.resolve( moduleName )
        Files.createDirectories( moduleDir.resolve( resourcePath ).getParent() );
        Files.write( moduleDir.resolve( resourcePath ), data.getBytes() );
    }

}
