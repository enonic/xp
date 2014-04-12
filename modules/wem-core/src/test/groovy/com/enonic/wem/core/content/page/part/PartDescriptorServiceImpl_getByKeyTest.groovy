package com.enonic.wem.core.content.page.part

class PartDescriptorServiceImpl_getByKeyTest
    extends AbstractPartDescriptorServiceTest
{
    def "get part descriptor"()
    {
        given:
        def key = createDescriptor( "foomodule-1.0.0:part-descr" ).first();

        when:
        def result = this.service.getByKey( key );

        then:
        result != null
    }
}
