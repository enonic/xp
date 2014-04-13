package com.enonic.wem.core.content.page.part

import com.enonic.wem.api.content.page.part.PartDescriptorKey
import com.enonic.wem.api.module.Module
import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleService
import com.enonic.wem.api.module.Modules
import com.enonic.wem.core.resource.MockResourceService
import spock.lang.Specification

abstract class AbstractPartDescriptorServiceTest
    extends Specification
{
    def MockResourceService resourceService

    def PartDescriptorServiceImpl service

    def setup()
    {
        this.resourceService = new MockResourceService()

        this.service = new PartDescriptorServiceImpl()
        this.service.moduleService = Mock( ModuleService.class )
        this.service.resourceService = this.resourceService
    }

    def PartDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = PartDescriptorKey.from( key )
            def descriptorXml = "<part-component><display-name>" + descriptorKey.getName().toString() + "</display-name></part-component>";

            this.resourceService.addResource( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }

    def Module createModule( final String moduleKey )
    {
        def module = Module.newModule().
            moduleKey( ModuleKey.from( moduleKey ) ).
            displayName( moduleKey.toUpperCase() ).
            build();

        this.service.moduleService.getModule( ModuleKey.from( moduleKey ) ) >> module
        return module;
    }

    def createModules( final String... moduleKeys )
    {
        def moduleList = []
        moduleKeys.each {
            def module = createModule( it )
            moduleList.add( module )
        }
        def modules = Modules.from( moduleList )
        this.service.moduleService.getAllModules() >> modules
        return modules;
    }
}
