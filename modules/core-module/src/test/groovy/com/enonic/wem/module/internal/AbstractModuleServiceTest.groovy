package com.enonic.wem.module.internal

import com.enonic.wem.api.module.ModuleBuilder
import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleVersion
import spock.lang.Specification

abstract class AbstractModuleServiceTest
    extends Specification
{
    def ModuleServiceImpl service

    def setup()
    {
        this.service = new ModuleServiceImpl()
    }

    def ModuleBuilder buildModule( final String key )
    {
        return new ModuleBuilder().
            moduleKey( ModuleKey.from( key ) ).
            moduleVersion( ModuleVersion.from( "1.0.0") ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" )
    }

    def createModule( final String name )
    {
        def fooModule = buildModule( name );

        this.service.installModule( fooModule.build() );
    }

    def createModules( final String... names )
    {
        names.each {
            createModule( it )
        }
    }

}
