package com.enonic.wem.core.content.page

class PageDescriptorServiceImpl_getByKeyTest
    extends AbstractPageDescriptorServiceTest
{
    def "get page descriptor"()
    {
        given:
        def key = createDescriptor( "foomodule-1.0.0:page-descr" ).first();

        when:
        def result = this.service.getByKey( key );

        then:
        result != null
    }
}
