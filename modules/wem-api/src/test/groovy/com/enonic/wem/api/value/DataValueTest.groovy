package com.enonic.wem.api.value

import com.enonic.wem.api.data.RootDataSet
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DataValueTest
    extends Specification
{
    def "test getObject"()
    {
        given:
        def real = new RootDataSet();
        def value = new DataValue( real )

        expect:
        value.getObject() == real
    }

    def "test asString"()
    {
        given:
        def real = new RootDataSet();
        def value = new DataValue( real )

        expect:
        value.asString() == real.toString()
    }
}
