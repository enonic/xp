package com.enonic.wem.core.content.page.layout

class LayoutDescriptorServiceImpl_getByKeyTest
    extends AbstractLayoutDescriptorServiceTest
{
    def "get layout descriptor"()
    {
        given:
        def key = createDescriptor( "foomodule-1.0.0:layout-descr" ).first();

        when:
        def result = this.service.getByKey( key );

        then:
        result != null
    }
}
