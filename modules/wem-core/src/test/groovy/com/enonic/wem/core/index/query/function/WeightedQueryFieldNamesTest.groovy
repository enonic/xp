package com.enonic.wem.core.index.query.function

import com.google.common.collect.ImmutableList
import spock.lang.Specification
import spock.lang.Unroll

class WeightedQueryFieldNamesTest
    extends Specification
{

    @Unroll
    def "create WeightedQueryFieldNames from #stringValue"()
    {
        expect:
        WeightedQueryFieldNames weightedQueryFieldNames = WeightedQueryFieldNames.from( stringValue )
        ImmutableList<WeightedQueryFieldName> entries = weightedQueryFieldNames.getWeightedQueryFieldNames();
        entries.size() == numberOfEntries

        where:
        stringValue              | numberOfEntries
        "myField"                | 1
        "myField^5"              | 1
        "myField^5,myOtherField" | 2
        "myField,myOtherField^2" | 2
    }

}
