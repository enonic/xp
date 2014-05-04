package com.enonic.wem.api.value

import com.enonic.wem.api.content.ContentId
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ContentIdValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def id = ContentId.from( "abc" )
        def value = new ContentIdValue( id )

        expect:
        value.getObject() == id
    }

    def "test asString"()
    {
        given:
        def id = ContentId.from( "abc" )
        def value = new ContentIdValue( id )

        expect:
        value.asString() == "abc"
    }
}
