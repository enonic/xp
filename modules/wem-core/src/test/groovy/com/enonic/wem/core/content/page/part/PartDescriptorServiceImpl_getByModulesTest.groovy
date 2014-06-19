package com.enonic.wem.core.content.page.part

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.module.Modules
import spock.lang.Ignore

@Ignore
class PartDescriptorServiceImpl_getByModulesTest
    extends AbstractPartDescriptorServiceTest
{
    def "get part descriptors by modules multiple"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createDescriptor( "foomodule-1.0.0:foomodule-part-descr", "barmodule-1.0.0:barmodule-part-descr" );
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) ) >> modules;

        when:
        def result = this.service.getByModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 2
    }

    def "get part descriptors by modules single"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createDescriptor( "foomodule-1.0.0:foomodule-part-descr" );
        def mod = Modules.from( modules.getModule( ModuleKey.from( "foomodule-1.0.0" ) ) )
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0" ) ) >> mod;

        when:
        def result = this.service.getByModules( ModuleKeys.from( "foomodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 1
    }
}
