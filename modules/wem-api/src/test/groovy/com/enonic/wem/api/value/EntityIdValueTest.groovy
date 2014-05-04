package com.enonic.wem.api.value

import com.enonic.wem.api.entity.EntityId
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EntityIdValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def id = EntityId.from( "abc" )
        def value = new EntityIdValue( id )

        expect:
        value.getObject() == id
    }

    def "test asString"()
    {
        given:
        def id = EntityId.from( "abc" )
        def value = new EntityIdValue( id )

        expect:
        value.asString() == "abc"
    }
}
