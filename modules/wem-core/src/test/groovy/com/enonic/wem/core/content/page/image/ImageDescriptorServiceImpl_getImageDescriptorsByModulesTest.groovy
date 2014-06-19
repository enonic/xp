package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.module.Modules
import spock.lang.Ignore

@Ignore
class ImageDescriptorServiceImpl_getImageDescriptorsByModulesTest
    extends AbstractImageDescriptorServiceTest
{
    def "get image descriptors by modules multiple"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createDescriptor( "foomodule-1.0.0:foomodule-image-descr", "barmodule-1.0.0:barmodule-image-descr" );
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) ) >> modules;

        when:
        def result = this.service.getImageDescriptorsByModules( ModuleKeys.from( "foomodule-1.0.0", "barmodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 2
    }

    def "get image descriptors by modules single"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodule-1.0.0" );
        createDescriptor( "foomodule-1.0.0:foomodule-image-descr" );
        def mod = Modules.from( modules.getModule( ModuleKey.from( "foomodule-1.0.0" ) ) )
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0" ) ) >> mod;

        when:
        def result = this.service.getImageDescriptorsByModules( ModuleKeys.from( "foomodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 1
    }

}
