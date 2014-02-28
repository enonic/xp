package com.enonic.wem.core.module

import com.enonic.wem.api.module.CreateModuleResourceParams
import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleNotFoundException
import com.enonic.wem.api.module.ModuleResourceKey
import com.enonic.wem.api.resource.Resource

class ModuleServiceImpl_createModuleResourceTest
    extends AbstractModuleServiceTest
{
    def "create module resource"()
    {
        given:
        createModule( "foomodule-1.2.0" )
        def resource = Resource.newResource().name( "resource-name" ).stringValue( "some data" ).build();
        def params = new CreateModuleResourceParams().resourceKey( ModuleResourceKey.from( "foomodule-1.2.0:/files/resource-name" ) ).resource( resource );

        when:
        def result = this.service.createResource( params );

        then:
        result != null
    }

    def "create resource in non-existing module"()
    {
        given:
        createModule( "foomodule-1.2.0" )
        def resource = Resource.newResource().name( "resource-name" ).stringValue( "some data" ).build();
        def params = new CreateModuleResourceParams().resourceKey( ModuleResourceKey.from( "foomodule-1.0.0:/files/resource-name" ) ).resource( resource );

        when:
        this.service.deleteModule( ModuleKey.from( "foomodule-1.0.0" ) );

        then:
        thrown( ModuleNotFoundException )
    }
}
