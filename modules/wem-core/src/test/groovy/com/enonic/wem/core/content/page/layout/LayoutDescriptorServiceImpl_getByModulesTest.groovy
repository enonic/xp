package com.enonic.wem.core.content.page.layout

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.module.Modules

class LayoutDescriptorServiceImpl_getByModulesTest
    extends AbstractLayoutDescriptorServiceTest
{
    def "get layout descriptors by modules multiple"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createLayoutDescriptor( "foomodule-1.0.0:foomodule-layout-descr", "barmodule-1.0.0:barmodule-layout-descr" );
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) ) >> modules;

        when:
        def result = this.service.getByModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 2
    }

    def "get layout descriptors by modules single"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createLayoutDescriptor( "foomodule-1.0.0:foomodule-layout-descr" );
        def mod = Modules.from( modules.getModule( ModuleKey.from( "foomodule-1.0.0" ) ) )
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0" ) ) >> mod;

        when:
        def result = this.service.getByModules( ModuleKeys.from( "foomodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 1
    }
}
