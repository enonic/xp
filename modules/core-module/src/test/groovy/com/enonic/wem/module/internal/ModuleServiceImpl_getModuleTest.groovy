package com.enonic.wem.module.internal

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleNotFoundException

class ModuleServiceImpl_getModuleTest
    extends AbstractModuleServiceTest
{
    def "get existing module"()
    {
        given:
        createModule( "foomodule-1.2.0" )

        when:
        def result = this.service.getModule( ModuleKey.from( "foomodule-1.2.0" ) );

        then:
        result != null
    }

    def "get non-existing module"()
    {
        when:
        this.service.getModule( ModuleKey.from( "foomodule-1.0.0" ) );

        then:
        thrown( ModuleNotFoundException )
    }
}
