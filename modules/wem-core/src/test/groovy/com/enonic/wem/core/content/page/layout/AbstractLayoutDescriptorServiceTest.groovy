package com.enonic.wem.core.content.page.layout

import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey
import com.enonic.wem.api.module.Module
import com.enonic.wem.api.module.ModuleKey
import com.enonic.wem.api.module.ModuleService
import com.enonic.wem.api.module.Modules
import com.enonic.wem.core.module.ModuleBuilder
import com.enonic.wem.core.resource.MockResourceService
import spock.lang.Specification

abstract class AbstractLayoutDescriptorServiceTest
    extends Specification
{
    def MockResourceService resourceService

    def LayoutDescriptorServiceImpl service

    def setup()
    {
        this.resourceService = new MockResourceService()

        this.service = new LayoutDescriptorServiceImpl()
        this.service.moduleService = Mock( ModuleService.class )
        this.service.resourceService = this.resourceService
    }

    def LayoutDescriptorKey[] createDescriptor( final String... keys )
    {
        def descriptorKeys = [];
        for ( key in keys )
        {
            def descriptorKey = LayoutDescriptorKey.from( key )
            def descriptorXml = "<layout-component><display-name>" + descriptorKey.getName().toString() +
                "</display-name></layout-component>";

            this.resourceService.addResource( descriptorKey.toResourceKey(), descriptorXml );
            descriptorKeys.add( descriptorKey );
        }

        return descriptorKeys;
    }

    def Module createModule( final String moduleKey )
    {
        def module = ModuleBuilder.newModule().
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
