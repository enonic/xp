package com.enonic.wem.core.module

import com.enonic.wem.api.module.ModuleKey

class ModuleServiceImpl_getAllModulesTest
    extends AbstractModuleServiceTest
{
    def "get all modules"()
    {
        given:
        createModules( "foomodule-1.2.0", "othermodule-1.3.0" )

        when:
        def result = this.service.getAllModules()

        then:
        !result.empty
        result.size == 2
        result.moduleKeys.size == 2
        result.moduleKeys.get( 0 ) == ModuleKey.from( "foomodule-1.2.0" )
        result.moduleKeys.get( 1 ) == ModuleKey.from( "othermodule-1.3.0" )
    }
}
