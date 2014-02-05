package com.enonic.wem.core.content.page.image

class ImageDescriptorServiceImpl_getImageDescriptorTest
        extends AbstractImageDescriptorServiceTest
{
    def "get image descriptor"()
    {
        given:
        def key = createImageDescriptor( "foomodule-1.0.0:image-descr" ).first();

        when:
        def result = this.service.getImageDescriptor( key );

        then:
        result != null
    }

}
