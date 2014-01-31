package com.enonic.wem.core.module

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleNotFoundException

class ModuleServiceImpl_deleteModuleTest
    extends AbstractModuleServiceTest
{
    def "delete existing module"()
    {
        given:
        createModule( "foomodule-1.2.0" )

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
}
