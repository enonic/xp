package com.enonic.wem.repo.internal.elasticsearch.query.builder.function

import com.enonic.wem.repo.internal.elasticsearch.function.WeightedQueryFieldName
import com.enonic.wem.repo.internal.elasticsearch.function.WeightedQueryFieldNames
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
        "myfield"                | 1
        "myfield^5"              | 1
        "myfield^5,myotherfield" | 2
        "myfield,myotherfield^2" | 2
    }

}
