package com.enonic.wem.core.content.page.image

class ImageDescriptorServiceImpl_getAllImageDescriptorsTest
        extends AbstractImageDescriptorServiceTest
{
    def "get all image descriptors multiple"()
    {
        given:
        createModules( "foomodule-1.0.0", "barmodule-1.0.0"  );
        createImageDescriptor( "foomodule-1.0.0:foomodule-image-descr" ,  "barmodule-1.0.0:barmodule-image-descr"  );

        when:
        def result = this.service.getAllImageDescriptors();

        then:
        result != null && result.getSize() == 2
    }

    def "get all image descriptors single"()
    {
        given:
        createModules( "foomodule-1.0.0");
        createImageDescriptor( "foomodule-1.0.0:foomodule-image-descr" );

        when:
        def result = this.service.getAllImageDescriptors();

        then:
        result != null && result.getSize() == 1
    }

    def "get all image descriptors none"()
    {
        given:
        createModules( );

        when:
        def result = this.service.getAllImageDescriptors();

        then:
        result != null && result.empty
    }
}
