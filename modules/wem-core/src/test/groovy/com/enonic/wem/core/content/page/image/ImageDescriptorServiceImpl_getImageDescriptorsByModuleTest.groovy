package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleKeys
import com.enonic.wem.api.module.Modules

class ImageDescriptorServiceImpl_getImageDescriptorsByModuleTest
        extends AbstractImageDescriptorServiceTest
{
    def "get image descriptors by module"()
    {
        given:
        def modules = createModules( "foomodule-1.0.0", "barmodules-1.0.0" );
        createImageDescriptor( "foomodule-1.0.0:foomodule-image-descr" );
        def mod = Modules.from( modules.getModule( ModuleKey.from( "foomodule-1.0.0" ) ) )
        this.service.moduleService.getModules( ModuleKeys.from( "foomodule-1.0.0" ) ) >> mod;

        when:
        def result = this.service.getImageDescriptorsByModule( ModuleKey.from( "foomodule-1.0.0" ) );

        then:
        result != null && result.getSize() == 1
    }

}
