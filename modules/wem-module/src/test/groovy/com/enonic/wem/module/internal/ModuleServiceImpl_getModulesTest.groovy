package com.enonic.wem.module.internal

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleKeys

class ModuleServiceImpl_getModulesTest
    extends AbstractModuleServiceTest
{
    def "get modules with empty keys"()
    {
        given:
        def keys = ModuleKeys.empty()

        when:
        def result = this.service.getModules( keys )

        then:
        result.empty
    }

    def "get non-existing modules"()
    {
        given:
        def keys = ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0", "cmodule-1.0.0" )

        when:
        def result = this.service.getModules( keys )

        then:
        result.empty
    }

    def "get existing modules"()
    {
        given:
        createModules( "amodule-1.0.0", "bmodule-1.0.0" )
        def keys = ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0" )

        when:
        def result = this.service.getModules( keys )

        then:
        !result.empty
        result.size == 2
        result.moduleKeys.size == 2
        result.moduleKeys.get( 0 ) == ModuleKey.from( "amodule-1.0.0" )
        result.moduleKeys.get( 1 ) == ModuleKey.from( "bmodule-1.0.0" )
    }

    def "get existing and non-existing modules"()
    {
        given:
        createModules( "amodule-1.0.0" )
        def keys = ModuleKeys.from( "amodule-1.0.0", "bmodule-1.0.0" )

        when:
        def result = this.service.getModules( keys )

        then:
        !result.empty
        result.size == 1
        result.moduleKeys.size == 1
        result.moduleKeys.get( 0 ) == ModuleKey.from( "amodule-1.0.0" )
    }
}
