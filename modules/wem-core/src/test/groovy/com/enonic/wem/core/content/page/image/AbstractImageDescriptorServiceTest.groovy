package com.enonic.wem.core.content.page.image

import com.enonic.wem.api.content.page.image.ImageDescriptorKey
import com.enonic.wem.api.module.*
import com.enonic.wem.api.resource.Resource
import com.google.common.io.ByteStreams
import spock.lang.Specification

import static com.enonic.wem.api.module.ModuleFileEntry.newModuleDirectory

abstract class AbstractImageDescriptorServiceTest
        extends Specification
{
    def ImageDescriptorServiceImpl service

    def setup()
    {
        this.service = new ImageDescriptorServiceImpl()

        this.service.moduleService = Mock( ModuleService.class )
    }

    def createImageDescriptor( final String... keys )
    {
        def resources = [];
        def descriptorKeys = [];
        for (key in keys) {
            def descriptorKey = ImageDescriptorKey.from( key )
            def descriptorXml = "<image-component><display-name>" + descriptorKey.getName().toString() + "</display-name></image-component>";
            def resource = Resource.newResource().name( "image.xml" ).stringValue( descriptorXml ).build();
            resources.add( resource );
            descriptorKeys.add( descriptorKey );
        }
        this.service.moduleService.getResource(_) >>> resources;
        return descriptorKeys;
    }

    def Module createModule( final String moduleKey )
    {
        def descriptorName = ModuleKey.from( moduleKey ).getName().toString(  ) + "-image-descr";
        final ModuleFileEntry componentDir = newModuleDirectory( "component" ).
                addEntry( newModuleDirectory( descriptorName ).addFile( "image.xml", ByteStreams.asByteSource( "xml".getBytes() ) ) ).
                build();
        def module = Module.newModule().
                moduleKey( ModuleKey.from( moduleKey ) ).
                displayName( moduleKey.toUpperCase() ).
                addFileEntry( componentDir ).
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
