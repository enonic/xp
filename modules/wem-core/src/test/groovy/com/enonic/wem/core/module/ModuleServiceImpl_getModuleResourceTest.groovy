package com.enonic.wem.core.module

import com.enonic.wem.api.module.ModuleNotFoundException
import com.enonic.wem.api.module.ModuleResourceKey
import com.enonic.wem.api.resource.ResourceNotFoundException

class ModuleServiceImpl_getModuleResourceTest
        extends AbstractModuleServiceTest
{
    def "get existing module resource"()
    {
        given:
        createModule( "foomodule-1.2.0" )
        createResource( "foomodule-1.2.0", "shared/files/image.jpg", "some data" )

        when: def result = this.service.getResource( ModuleResourceKey.from( "foomodule-1.2.0:/shared/files/image.jpg" ) );

        then: result != null
    }

    def "get non-existing module resource"()
    {
        given:
        createModule( "foomodule-1.2.0" )
        createResource( "foomodule-1.2.0", "shared/files/image.jpg", "some data" )

        when: this.service.getResource( ModuleResourceKey.from( "foomodule-1.0.0:/shared/files/image.jpg" ) );

        then: thrown( ModuleNotFoundException )
    }

    def "get non-existing resource"()
    {
        given:
        createModule( "foomodule-1.2.0" )
        createResource( "foomodule-1.2.0", "shared/files/image.jpg", "some data" )

        when: this.service.getResource( ModuleResourceKey.from( "foomodule-1.2.0:/shared/files/missing_file.jpg" ) );

        then: thrown( ResourceNotFoundException )
    }
}
